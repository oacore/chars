/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.core.documentdownload.downloader.fetcher;

import crawlercommons.fetcher.*;
import crawlercommons.fetcher.RedirectFetchException.RedirectExceptionReason;
import crawlercommons.fetcher.http.BaseHttpFetcher;
import crawlercommons.fetcher.http.LocalCookieStore;
import crawlercommons.fetcher.http.UserAgent;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientParamBean;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.params.CookieSpecParamBean;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.params.*;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@SuppressWarnings("serial")
public class DomainAwareHttpFetcher extends BaseHttpFetcher {
    private static Logger LOGGER = LoggerFactory.getLogger(DomainAwareHttpFetcher.class);

    // We tried 10 seconds for all of these, but got a number of connection/read
    // timeouts for
    // sites that would have eventually worked, so bumping it up to 30 seconds.
    private static final int DEFAULT_SOCKET_TIMEOUT = 30 * 1000;

    // As of HttpComponents v.4.2.1, this will also include timeout needed to
    // get Connection from Pool.
    // From initial comment of the deprecated 'CONNECTION_POOL_TIMEOUT' static
    // element:
    // "This normally doesen't ever hit this timeout, since we manage the number
    // of
    // fetcher threads to be <= the maxThreads value used to configure a
    // HttpFetcher. However the limit of connections/host can cause a timeout,
    // when redirects cause multiple threads to hit the same domain.
    // We therefore jack this right up."
    private static final int DEFAULT_CONNECTION_TIMEOUT = 100 * 1000;

    private static final int DEFAULT_MAX_THREADS = 1;

    private static final int BUFFER_SIZE = 8 * 1024;
    private static final int DEFAULT_MAX_RETRY_COUNT = 10;

    private static final int DEFAULT_BYTEARRAY_SIZE = 32 * 1024;

    // Use the same values as Firefox (except that we don't accept deflate,
    // which we're not sure is implemented correctly - see the notes in
    // EncodingUtils/EncodingUtilsTest for more details).
    private static final String DEFAULT_ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String DEFAULT_ACCEPT_CHARSET = "utf-8,ISO-8859-1;q=0.7,*;q=0.7";
    private static final String DEFAULT_ACCEPT_ENCODING = "x-gzip, gzip";

    // Keys used to access data in the Http execution context.
    private static final String PERM_REDIRECT_CONTEXT_KEY = "perm-redirect";
    private static final String REDIRECT_COUNT_CONTEXT_KEY = "redirect-count";
    private static final String HOST_ADDRESS = "host-address";

    // To be polite, set it small; if we use it, we will use less than a second
    // delay between subsequent fetches
    private static final int DEFAULT_KEEP_ALIVE_DURATION = 5000;

    private IdleConnectionMonitorThread monitor;

    private ThreadLocal<CookieStore> localCookieStore = new ThreadLocal<CookieStore>() {
        protected CookieStore initialValue() {
            CookieStore cookieStore = new LocalCookieStore();
            return cookieStore;
        }
    };

    private static final String SSL_CONTEXT_NAMES[] = { "TLS", "Default", "SSL", };

    private static final String TEXT_MIME_TYPES[] = { "text/html", "application/x-asp", "application/xhtml+xml", "application/vnd.wap.xhtml+xml", };

    private HttpVersion _httpVersion;
    private int _socketTimeout;
    private int _connectionTimeout;
    private int _maxRetryCount;

    transient private DefaultHttpClient _httpClient;
   
    private static class MyRequestRetryHandler implements HttpRequestRetryHandler {
        private int _maxRetryCount;

        public MyRequestRetryHandler(int maxRetryCount) {
            _maxRetryCount = maxRetryCount;
        }

        @Override
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Decide about retry #" + executionCount + " for exception " + exception.getMessage());
            }

            if (executionCount >= _maxRetryCount) {
                // Do not retry if over max retry count
                return false;
            } else if (exception instanceof NoHttpResponseException) {
                // Retry if the server dropped connection on us
                return true;
            } else if (exception instanceof SSLHandshakeException) {
                // Do not retry on SSL handshake exception
                return false;
            }

            HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            // Retry if the request is considered idempotent
            return idempotent;
        }
    }

    private static class MyRedirectException extends RedirectException {

        private URI _uri;
        private RedirectExceptionReason _reason;

        public MyRedirectException(String message, URI uri, RedirectExceptionReason reason) {
            super(message);
            _uri = uri;
            _reason = reason;
        }

        public URI getUri() {
            return _uri;
        }

        public RedirectExceptionReason getReason() {
            return _reason;
        }
    }

    /**
     * Interceptor to record host address in context.
     * 
     */
    private static class MyRequestInterceptor implements HttpRequestInterceptor {

        @Override
        public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
            HttpInetConnection connection = (HttpInetConnection) (context.getAttribute(ExecutionContext.HTTP_CONNECTION));
            context.setAttribute(HOST_ADDRESS, connection.getRemoteAddress().getHostAddress());
        }
    }

    private static class DummyX509TrustManager implements X509TrustManager {
        private X509TrustManager standardTrustManager = null;

        /**
         * Constructor for DummyX509TrustManager.
         */
        public DummyX509TrustManager(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {
            super();
            String algo = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory factory = TrustManagerFactory.getInstance(algo);
            factory.init(keystore);
            TrustManager[] trustmanagers = factory.getTrustManagers();
            if (trustmanagers.length == 0) {
                throw new NoSuchAlgorithmException(algo + " trust manager not supported");
            }
            this.standardTrustManager = (X509TrustManager) trustmanagers[0];
        }

        /**
         * @see javax.net.ssl.X509TrustManager#checkClientTrusted(X509Certificate[],
         *      String)
         */
        @SuppressWarnings("unused")
        public boolean isClientTrusted(X509Certificate[] certificates) {
            return true;
        }

        /**
         * @see javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[],
         *      String)
         */
        @SuppressWarnings("unused")
        public boolean isServerTrusted(X509Certificate[] certificates) {
            return true;
        }

        /**
         * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
         */
        public X509Certificate[] getAcceptedIssuers() {
            return this.standardTrustManager.getAcceptedIssuers();
        }

        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            // do nothing

        }

        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            // do nothing

        }
    }

    public static class MyConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {

        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            if (response == null) {
                throw new IllegalArgumentException("HTTP response may not be null");
            }
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
            return DEFAULT_KEEP_ALIVE_DURATION;
        }
    }

    public static class IdleConnectionMonitorThread extends Thread {

        private final ClientConnectionManager connMgr;

        public IdleConnectionMonitorThread(ClientConnectionManager connMgr) {
            super();
            this.connMgr = connMgr;
            this.setDaemon(true);
        }

        @Override
        public void run() {
            while (!interrupted()) {
                // Close expired connections
                connMgr.closeExpiredConnections();
                // Optionally, close connections
                // that have been idle longer than 30 sec
                connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                try {
                    // TODO is it better to implement as
                    // Thread.currentThread().sleep(30000);
                    // and add a javac declaration?
                    Thread.currentThread();
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public DomainAwareHttpFetcher(UserAgent userAgent) {
        this(DEFAULT_MAX_THREADS, userAgent, DEFAULT_MAX_REDIRECTS);
    }
    
    public DomainAwareHttpFetcher(int maxThreads, UserAgent userAgent, int maxRedirects) {
        super(maxThreads, userAgent);

        _httpVersion = HttpVersion.HTTP_1_1;
        _socketTimeout = DEFAULT_SOCKET_TIMEOUT;
        _connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        _maxRetryCount = DEFAULT_MAX_RETRY_COUNT;

        // Just to be explicit, we rely on lazy initialization of this so that
        // we don't have to worry about serializing it.
        _httpClient = null;
    }
    
    public HttpVersion getHttpVersion() {
        return _httpVersion;
    }

    public void setHttpVersion(HttpVersion httpVersion) {
        if (_httpClient == null) {
            _httpVersion = httpVersion;
        } else {
            throw new IllegalStateException("Can't change HTTP version after HttpClient has been initialized");
        }
    }

    public int getSocketTimeout() {
        return _socketTimeout;
    }

    public void setSocketTimeout(int socketTimeoutInMs) {
        if (_httpClient == null) {
            _socketTimeout = socketTimeoutInMs;
        } else {
            throw new IllegalStateException("Can't change socket timeout after HttpClient has been initialized");
        }
    }

    public int getConnectionTimeout() {
        return _connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeoutInMs) {
        if (_httpClient == null) {
            _connectionTimeout = connectionTimeoutInMs;
        } else {
            throw new IllegalStateException("Can't change connection timeout after HttpClient has been initialized");
        }
    }

    public int getMaxRetryCount() {
        return _maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        _maxRetryCount = maxRetryCount;
    }
    
    @Override
    public FetchedResult get(String string, Payload pld) throws BaseFetchException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public FetchedResult get(String url, RedirectStrategy redirectStrategy, String filePath) throws BaseFetchException, FileTooBigException, IllegalDomainException, RequiresLoginException {
        try {
            URL realUrl = new URL(url);
            String protocol = realUrl.getProtocol();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                throw new BadProtocolFetchException(url);
            }
        } catch (MalformedURLException e) {
            throw new UrlFetchException(url, e.getMessage());
        }

        return request(new HttpGet(), url, null, redirectStrategy, filePath);
    }

    private FetchedResult request(HttpRequestBase request, String url, Payload payload, RedirectStrategy redirectStrategy,
                                  String filePath) throws BaseFetchException, FileTooBigException, IllegalDomainException, RequiresLoginException {
        init(redirectStrategy);

        try {
            return doRequest(request, url, payload, filePath);
        } catch (HttpFetchException e) {
            // Don't bother generating a trace for a 404 (not found)
            if (LOGGER.isTraceEnabled() && (e.getHttpStatus() != HttpStatus.SC_NOT_FOUND)) {
                LOGGER.trace(String.format("Exception fetching %s (%s)", url, e.getMessage()));
            }

            throw e;
        } catch (AbortedFetchException e) {
            // Don't bother reporting that we bailed because the mime-type
            // wasn't one that we wanted.
            if (e.getAbortReason() != AbortedFetchReason.INVALID_MIMETYPE) {
                LOGGER.debug(String.format("Exception fetching %s (%s)", url, e.getMessage()));
            }
            throw e;
        } catch (BaseFetchException e) {
            LOGGER.debug(String.format("Exception fetching %s (%s)", url, e.getMessage()));
            throw e;
        }
        
    }

    private FetchedResult doRequest(HttpRequestBase request, String url, Payload payload, String filePath) throws BaseFetchException, FileTooBigException, IllegalDomainException, RequiresLoginException {
        LOGGER.trace("Fetching " + url);

        HttpResponse response;
        long readStartTime;
        Metadata headerMap = new Metadata();
        String redirectedUrl = null;
        String newBaseUrl = null;
        int numRedirects = 0;
        boolean needAbort = true;
        String contentType = "";
        String mimeType = "";
        String hostAddress = null;
        int statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
        String reasonPhrase = null;

        // Create a local instance of cookie store, and bind to local context
        // Without this we get killed w/lots of threads, due to sync() on single
        // cookie store.
        HttpContext localContext = new BasicHttpContext();
        CookieStore cookieStore = localCookieStore.get();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        StringBuilder fetchTrace = null;
        if (LOGGER.isTraceEnabled()) {
            fetchTrace = new StringBuilder("Fetched url: " + url);
        }

        try {
            request.setURI(new URI(url));

            readStartTime = System.currentTimeMillis();
            response = _httpClient.execute(request, localContext);
            
            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                headerMap.add(header.getName(), header.getValue());
            }

            if (headerMap.get(HttpHeaders.CONTENT_LENGTH) != null) {
                boolean fileTooBig = Long.parseLong(headerMap.get(HttpHeaders.CONTENT_LENGTH)) > 524288000;
                if (fileTooBig) {
                    needAbort = true;
                    throw new FileTooBigException("The download is larger than 500MB. Aborting download", new URI(url));
                }
            }
            
            statusCode = response.getStatusLine().getStatusCode();
            reasonPhrase = response.getStatusLine().getReasonPhrase();

            if (LOGGER.isTraceEnabled()) {
                fetchTrace.append("; status code: " + statusCode);
                if (headerMap.get(HttpHeaders.CONTENT_LENGTH) != null) {
                    fetchTrace.append("; Content-Length: " + headerMap.get(HttpHeaders.CONTENT_LENGTH));
                }

                if (headerMap.get(HttpHeaders.LOCATION) != null) {
                    fetchTrace.append("; Location: " + headerMap.get(HttpHeaders.LOCATION));
                }
            }

            if ((statusCode < 200) || (statusCode >= 300)) {
                // We can't just check against SC_OK, as some wackos return 201,
                // 202, etc
                throw new HttpFetchException(url, "Error fetching " + url + " due to \"" + reasonPhrase + "\"", statusCode, headerMap);
            }

            redirectedUrl = extractRedirectedUrl(url, localContext);

            URI permRedirectUri = (URI) localContext.getAttribute(PERM_REDIRECT_CONTEXT_KEY);
            if (permRedirectUri != null) {
                newBaseUrl = permRedirectUri.toURL().toExternalForm();
            }

            Integer redirects = (Integer) localContext.getAttribute(REDIRECT_COUNT_CONTEXT_KEY);
            if (redirects != null) {
                numRedirects = redirects.intValue();
            }

            hostAddress = (String) (localContext.getAttribute(HOST_ADDRESS));
            if (hostAddress == null) {
                throw new UrlFetchException(url, "Host address not saved in context");
            }

            Header cth = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
            if (cth != null) {
                contentType = cth.getValue();
            }

            // Check if we should abort due to mime-type filtering. Note that
            // this will fail if the server
            // doesn't report a mime-type, but that's how we want it as this
            // configuration is typically
            // used when only a subset of parsers are installed/enabled, so we
            // don't want the auto-detect
            // code in Tika to get triggered & try to process an unsupported
            // type. If you want unknown
            // mime-types from the server to be processed, set "" as one of the
            // valid mime-types in
            // FetcherPolicy.
            mimeType = getMimeTypeFromContentType(contentType);
            Set<String> mimeTypes = getValidMimeTypes();
            if ((mimeTypes != null) && (mimeTypes.size() > 0)) {
                if (!mimeTypes.contains(mimeType)) {
                    throw new AbortedFetchException(url, "Invalid mime-type: " + mimeType, AbortedFetchReason.INVALID_MIMETYPE);
                }
            }

            needAbort = false;
        } catch (ClientProtocolException e) {
            // Oleg guarantees that no abort is needed in the case of an
            // IOException
            // (which is is a subclass of)
            needAbort = false;

            // If the root case was a "too many redirects" error, we want to map
            // this to a specific
            // exception that contains the final redirect.
            if (e.getCause() instanceof MyRedirectException) {
                MyRedirectException mre = (MyRedirectException) e.getCause();
                String redirectUrl = url;

                try {
                    redirectUrl = mre.getUri().toURL().toExternalForm();
                } catch (MalformedURLException e2) {
                    LOGGER.warn("Invalid URI saved during redirect handling: " + mre.getUri());
                }

                throw new RedirectFetchException(url, redirectUrl, mre.getReason());
            } else if (e.getCause() instanceof RedirectException) {
                throw new RedirectFetchException(url, extractRedirectedUrl(url, localContext), RedirectExceptionReason.TOO_MANY_REDIRECTS);
            } else if (e.getCause() instanceof IllegalDomainException) {
                IllegalDomainException illegalDomainException = (IllegalDomainException) e.getCause();
                String uri = illegalDomainException.getUri().toString();
                if (uri.contains("login") ||
                        uri.contains("signin") ||
                        uri.contains("signon")) {
                    throw new RequiresLoginException("Login required", illegalDomainException.getUri());                }
                throw illegalDomainException;
            } else {
                throw new IOFetchException(url, e);
            }
        } catch (IOException e) {
            // Oleg guarantees that no abort is needed in the case of an
            // IOException
            needAbort = false;
            throw new IOFetchException(url, e);
        } catch (URISyntaxException e) {
            throw new UrlFetchException(url, e.getMessage());
        } catch (IllegalStateException e) {
            throw new UrlFetchException(url, e.getMessage());
        } catch (BaseFetchException | FileTooBigException e) {
            throw e;
        } catch (Exception e) {
            // Map anything else to a generic IOFetchException
            // TODO KKr - create generic fetch exception
            throw new IOFetchException(url, new IOException(e));
        } finally {
            safeAbort(needAbort, request);
        }

        // Figure out how much data we want to try to fetch.
        int maxContentSize = getMaxContentSize(mimeType);
        int targetLength = maxContentSize;
        boolean truncated = false;
        String contentLengthStr = headerMap.get(HttpHeaders.CONTENT_LENGTH);
        if (contentLengthStr != null) {
            try {
                int contentLengthFromDownload = Integer.parseInt(contentLengthStr);
                if (contentLengthFromDownload > targetLength) {
                    truncated = true;
                } else {
                    targetLength = contentLengthFromDownload;
                }
            } catch (NumberFormatException e) {
                // Ignore (and log) invalid content length values.
                LOGGER.warn("Invalid content length in header: " + contentLengthStr);
            }
        }

        // Now finally read in response body, up to targetLength bytes.
        // Note that entity might be null, for zero length responses.
        byte[] contentFirstBytes = null;
        long contentLength = 0;
        long readRate = 0;
        HttpEntity entity = response.getEntity();
        needAbort = true;

        if (entity != null) {
            InputStream in = null;
            FileOutputStream out = null;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                in = entity.getContent();
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = 0;
                int totalRead = 0;

                out = new FileOutputStream(filePath);

                int readRequests = 0;
                int minResponseRate = getMinResponseRate();
                // TODO KKr - we need to monitor the rate while reading a
                // single block. Look at HttpClient
                // metrics support for how to do this. Once we fix this, fix
                // the test to read a smaller (< 20K)
                // chuck of data.
                while ((totalRead < targetLength) && ((bytesRead = in.read(buffer, 0, Math.min(buffer.length, targetLength - totalRead))) != -1)) {
                    readRequests += 1;
                    totalRead += bytesRead;
                    out.write(buffer, 0, bytesRead);
                    if(byteArrayOutputStream.size() < 5000) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }

                    contentLength += bytesRead;

                    // Assume read time is at least one millisecond, to avoid
                    // DBZ exception.
                    long totalReadTime = Math.max(1, System.currentTimeMillis() - readStartTime);
                    readRate = (totalRead * 1000L) / totalReadTime;

                    // Don't bail on the first read cycle, as we can get a
                    // hiccup starting out.
                    // Also don't bail if we've read everything we need.
                    if ((readRequests > 1) && (totalRead < targetLength) && (readRate < minResponseRate)) {
                        throw new AbortedFetchException(url, "Slow response rate of " + readRate + " bytes/sec", AbortedFetchReason.SLOW_RESPONSE_RATE);
                    }

                    // Check to see if we got interrupted, but don't clear the
                    // interrupted flag.
                    if (Thread.currentThread().isInterrupted()) {
                        throw new AbortedFetchException(url, AbortedFetchReason.INTERRUPTED);
                    }
                }
                contentFirstBytes = byteArrayOutputStream.toByteArray();

                String contentLengthHeader = headerMap.get(HttpHeaders.CONTENT_LENGTH);
                if (contentLengthHeader != null) {
                    long length = Long.parseLong(contentLengthHeader);
                    if(contentLength != length) {
                        headerMap.add(HttpHeaders.CONTENT_LENGTH, Objects.toString(contentLength));
                    }

                } else {
                    headerMap.add(HttpHeaders.CONTENT_LENGTH, Objects.toString(contentLength));
                }

                needAbort = truncated || (in.available() > 0);
            } catch (IOException e) {
                // We don't need to abort if there's an IOException
                throw new IOFetchException(url, e);
            } finally {
                safeAbort(needAbort, request);
                safeClose(in);
                safeClose(out);
                safeClose(byteArrayOutputStream);
            }
        }

        // Toss truncated image content.
        if ((truncated) && (!isTextMimeType(mimeType))) {
            throw new AbortedFetchException(url, "Truncated image", AbortedFetchReason.CONTENT_SIZE);
        }

        // Now see if we need to uncompress the content.
        String contentEncoding = headerMap.get(HttpHeaders.CONTENT_ENCODING);
        if (contentEncoding != null) {
            if (LOGGER.isTraceEnabled()) {
                fetchTrace.append("; Content-Encoding: " + contentEncoding);
            }

            // TODO KKr We might want to just decompress a truncated gzip
            // containing text (since we have a max content size to save us
            // from any gzip corruption). We might want to break the following
            // out into a separate method, by the way (if not refactor this
            // entire monolithic method).
            //
            try {
                if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
                    if (truncated) {
                        throw new AbortedFetchException(url, "Truncated compressed data", AbortedFetchReason.CONTENT_SIZE);
                    } else {

                        truncated = processGzipFileUncompressing(filePath, maxContentSize, contentFirstBytes);
                        if ((truncated) && (!isTextMimeType(mimeType))) {
                            throw new AbortedFetchException(url, "Truncated decompressed image", AbortedFetchReason.CONTENT_SIZE);
                        } else {
                            // If un-gzipped, read the uncompressed file into contentFirstBytes
                            try(InputStream is = new FileInputStream(filePath)) {
                                byte[] buffer = new byte[500];
                                is.read(buffer);
                                contentFirstBytes = buffer;
                            }
                            if (LOGGER.isTraceEnabled()) {
                                fetchTrace.append("; unzipped to file " + filePath);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new IOFetchException(url, e);
            }
        }

        // Finally dump out the trace msg we've been building.
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(fetchTrace.toString());
        }

        // TODO KKr - Save truncated flag in FetchedResult/FetchedDatum.
        return new FetchedResult(url, redirectedUrl, System.currentTimeMillis(), headerMap, contentFirstBytes, contentType, (int) readRate, payload, newBaseUrl, numRedirects, hostAddress, statusCode,
                        reasonPhrase);
    }

    private boolean processGzipFileUncompressing(String filePath, int maxContentSize, byte[] contentFirstBytes) throws IOException {
        final String SUFFIX = "-temporary";
        OutputStream outStream = new FileOutputStream(filePath + SUFFIX);
        GZIPInputStream inStream = new GZIPInputStream(new FileInputStream(filePath));
        boolean isTruncated = false;
        byte[] buf = new byte[4096];
        int written = 0;

        while(true) {
            try {
                int size = inStream.read(buf);
                if (size <= 0) {
                    break;
                }

                if (contentFirstBytes == null) {
                    contentFirstBytes = Arrays.copyOf(buf, 100);
                }

                if (written + size > maxContentSize) {
                    isTruncated = true;
                    outStream.write(buf, 0, maxContentSize - written);
                    break;
                }

                outStream.write(buf, 0, size);
                written += size;
            } catch (Exception var8) {
                LOGGER.trace("Exception unzipping content", var8);
                break;
            }
        }

        safeClose(inStream);
        safeClose(outStream);

        Files.move(new File(filePath + SUFFIX).toPath(), new File(filePath).toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        return isTruncated;
    }

    private boolean isTextMimeType(String mimeType) {
        for (String textContentType : TEXT_MIME_TYPES) {
            if (textContentType.equals(mimeType)) {
                return true;
            }
        }
        return false;
    }

    private String extractRedirectedUrl(String url, HttpContext localContext) {
        // This was triggered by HttpClient with the redirect count was
        // exceeded.
        HttpHost host = (HttpHost) localContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
        HttpUriRequest finalRequest = (HttpUriRequest) localContext.getAttribute(ExecutionContext.HTTP_REQUEST);

        try {
            URL hostUrl = new URI(host.toURI()).toURL();
            return new URL(hostUrl, finalRequest.getURI().toString()).toExternalForm();
        } catch (MalformedURLException e) {
            LOGGER.warn("Invalid host/uri specified in final fetch: " + host + finalRequest.getURI());
            return url;
        } catch (URISyntaxException e) {
            LOGGER.warn("Invalid host/uri specified in final fetch: " + host + finalRequest.getURI());
            return url;
        }
    }

    private static void safeClose(Closeable o) {
        if (o != null) {
            try {
                o.close();
            } catch (Exception e) {
                // Ignore any errors
            }
        }
    }

    private static void safeAbort(boolean needAbort, HttpRequestBase request) {
        if (needAbort && (request != null)) {
            try {
                request.abort();
            } catch (Throwable t) {
                // Ignore any errors
            }
        }
    }

    private void init(RedirectStrategy redirectStrategy) {
        if (_httpClient == null) {
            synchronized (DomainAwareHttpFetcher.class) {
                if (_httpClient != null)
                    return;

                // Create and initialize HTTP parameters
                HttpParams params = new BasicHttpParams();

                // Set the socket and connection timeout to be something
                // reasonable.
                HttpConnectionParams.setSoTimeout(params, _socketTimeout);
                HttpConnectionParams.setConnectionTimeout(params, _connectionTimeout);

                /*
                 * CoreConnectionPNames.TCP_NODELAY='http.tcp.nodelay':
                 * determines whether Nagle's algorithm is to be used. Nagle's
                 * algorithm tries to conserve bandwidth by minimizing the
                 * number of segments that are sent. When applications wish to
                 * decrease network latency and increase performance, they can
                 * disable Nagle's algorithm (that is enable TCP_NODELAY. Data
                 * will be sent earlier, at the cost of an increase in bandwidth
                 * consumption. This parameter expects a value of type
                 * java.lang.Boolean. If this parameter is not set, TCP_NODELAY
                 * will be enabled (no delay).
                 */
                HttpConnectionParams.setTcpNoDelay(params, true);

                /*
                 * CoreConnectionPNames.STALE_CONNECTION_CHECK=
                 * 'http.connection.stalecheck': determines whether stale
                 * connection check is to be used. Disabling stale connection
                 * check may result in a noticeable performance improvement (the
                 * check can cause up to 30 millisecond overhead per request) at
                 * the risk of getting an I/O error when executing a request
                 * over a connection that has been closed at the server side.
                 * This parameter expects a value of type java.lang.Boolean. For
                 * performance critical operations the check should be disabled.
                 * If this parameter is not set, the stale connection check will
                 * be performed before each request execution.
                 * 
                 * We don't need I/O exceptions in case if Server doesn't
                 * support Kee-Alive option; our client by default always tries
                 * keep-alive.
                 */
                // Even with stale checking enabled, a connection can "go stale"
                // between the check and the
                // next request. So we still need to handle the case of a closed
                // socket (from the server side),
                // and disabling this check improves performance.
                HttpConnectionParams.setStaleCheckingEnabled(params, false);

                HttpProtocolParams.setVersion(params, _httpVersion);
                HttpProtocolParams.setUserAgent(params, _userAgent.getUserAgentString());
                HttpProtocolParams.setContentCharset(params, "UTF-8");
                HttpProtocolParams.setHttpElementCharset(params, "UTF-8");

                /*
                 * CoreProtocolPNames.USE_EXPECT_CONTINUE=
                 * 'http.protocol.expect-continue': activates the Expect:
                 * 100-Continue handshake for the entity enclosing methods. The
                 * purpose of the Expect: 100-Continue handshake is to allow the
                 * client that is sending a request message with a request body
                 * to determine if the origin server is willing to accept the
                 * request (based on the request headers) before the client
                 * sends the request body. The use of the Expect: 100-continue
                 * handshake can result in a noticeable performance improvement
                 * for entity enclosing requests (such as POST and PUT) that
                 * require the target server's authentication. The Expect:
                 * 100-continue handshake should be used with caution, as it may
                 * cause problems with HTTP servers and proxies that do not
                 * support HTTP/1.1 protocol. This parameter expects a value of
                 * type java.lang.Boolean. If this parameter is not set,
                 * HttpClient will not attempt to use the handshake.
                 */
                HttpProtocolParams.setUseExpectContinue(params, true);

                /*
                 * CoreProtocolPNames.WAIT_FOR_CONTINUE=
                 * 'http.protocol.wait-for-continue': defines the maximum period
                 * of time in milliseconds the client should spend waiting for a
                 * 100-continue response. This parameter expects a value of type
                 * java.lang.Integer. If this parameter is not set HttpClient
                 * will wait 3 seconds for a confirmation before resuming the
                 * transmission of the request body.
                 */
                params.setIntParameter(CoreProtocolPNames.WAIT_FOR_CONTINUE, 5000);

                CookieSpecParamBean cookieParams = new CookieSpecParamBean(params);
                cookieParams.setSingleHeader(false);

                // Create and initialize scheme registry
                SchemeRegistry schemeRegistry = new SchemeRegistry();
                schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

                SSLSocketFactory sf = null;

                for (String contextName : SSL_CONTEXT_NAMES) {
                    try {
                        SSLContext sslContext = SSLContext.getInstance(contextName);
                        sslContext.init(null, new TrustManager[] { new DummyX509TrustManager(null) }, null);
                        sf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                        break;
                    } catch (NoSuchAlgorithmException e) {
                        LOGGER.debug("SSLContext algorithm not available: " + contextName);
                    } catch (Exception e) {
                        LOGGER.debug("SSLContext can't be initialized: " + contextName, e);
                    }
                }
                if (sf != null) {
                    schemeRegistry.register(new Scheme("https", 443, sf));
                } else {
                    LOGGER.warn("No valid SSLContext found for https");
                }

                PoolingClientConnectionManager poolingClientConnectionManager = new PoolingClientConnectionManager(schemeRegistry);
                poolingClientConnectionManager.setMaxTotal(_maxThreads);
                poolingClientConnectionManager.setDefaultMaxPerRoute(getMaxConnectionsPerHost());

                _httpClient = new DefaultHttpClient(poolingClientConnectionManager, params);

                _httpClient.setHttpRequestRetryHandler(new MyRequestRetryHandler(_maxRetryCount));
                _httpClient.setRedirectStrategy(redirectStrategy);
                _httpClient.addRequestInterceptor(new MyRequestInterceptor());

                // FUTURE KKr - support authentication
                HttpClientParams.setAuthenticating(params, false);
                HttpClientParams.setCookiePolicy(params, CookiePolicy.BEST_MATCH);

                ClientParamBean clientParams = new ClientParamBean(params);
                if (getMaxRedirects() == 0) {
                    clientParams.setHandleRedirects(false);
                } else {
                    clientParams.setHandleRedirects(true);
                    clientParams.setMaxRedirects(getMaxRedirects());
                }

                // Set up default headers. This helps us get back from servers
                // what we want.
                HashSet<Header> defaultHeaders = new HashSet<Header>();
                defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, getAcceptLanguage()));
                defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT_CHARSET, DEFAULT_ACCEPT_CHARSET));
                defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, DEFAULT_ACCEPT_ENCODING));
                defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT, DEFAULT_ACCEPT));

                clientParams.setDefaultHeaders(defaultHeaders);

                ((DefaultHttpClient) _httpClient).setKeepAliveStrategy(new MyConnectionKeepAliveStrategy());

                monitor = new IdleConnectionMonitorThread(poolingClientConnectionManager);
                monitor.start();
            }
        }

    }

    @Override
    public void abort() {
        // TODO Actually try to abort
    }

    @Override
    protected void finalize() {
        monitor.interrupt();
        _httpClient.getConnectionManager().shutdown();
        _httpClient = null;
    }
}
