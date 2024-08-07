package uk.ac.core.dataprovider.logic.util.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ConnectionBackoffStrategy;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import uk.ac.core.dataprovider.logic.dto.HttpResponseDTO;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.Optional;

/**
 * HTTP Executor based on Apache Http Client.
 * ONLY for generic batch requests to OAI-PMH endpoints.
 */
public final class ApacheHttpExecutor implements HttpExecutor {

    private final HttpClient client;
    private final static Charset DEFAULT_CONTENT_TYPE_NAME = StandardCharsets.UTF_8;
    private final static int CONNECT_TIMEOUT = 5000;
    private final static int CONNECTION_REQUEST_TIMEOUT = 5000;
    private final static int SOCKET_TIMEOUT = 3000;

    public ApacheHttpExecutor() {
        this(true);
    }

    public ApacheHttpExecutor(boolean isRedirectEnabled) {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setRedirectsEnabled(isRedirectEnabled)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();

        client = HttpClients.custom()
                .disableCookieManagement()
                .setConnectionBackoffStrategy(new ConnectionTimeoutBackOffStrategy())
                .setDefaultRequestConfig(config)
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();
    }

    private static final class ConnectionTimeoutBackOffStrategy implements ConnectionBackoffStrategy {
        @Override
        public boolean shouldBackoff(final Throwable t) {
            return (t instanceof SocketTimeoutException
                    || t instanceof ConnectException
                    || t instanceof ConnectTimeoutException
                    || t instanceof NullPointerException);
        }

        @Override
        public boolean shouldBackoff(final HttpResponse resp) {
            int responseStatusCode = resp.getStatusLine().getStatusCode();
            return (responseStatusCode == HttpStatus.SC_SERVICE_UNAVAILABLE
                    || responseStatusCode == HttpStatus.SC_BAD_GATEWAY
                    || responseStatusCode == HttpStatus.SC_GATEWAY_TIMEOUT
                    || responseStatusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public HttpResponseDTO get(String url) throws IOException {
        HttpClientContext context = HttpClientContext.create();
        HttpResponse response = client.execute(new HttpGet(url), context);

        Optional<URI> redirectURI = findRedirectURI(context.getRedirectLocations());

        String finalUri;
        if(redirectURI.isPresent()) {
            finalUri = redirectURI.get().toString();
        } else {
            finalUri = url;
        }

        HttpEntity responseBody = response.getEntity();
        ContentType contentType = getContentType(responseBody);

        return new HttpResponseDTO(
                response.getStatusLine().getStatusCode(),
                contentType.getMimeType(),
                contentType.getCharset().name(),
                EntityUtils.toString(responseBody),
                finalUri
        );
    }

    private Optional<URI> findRedirectURI(List<URI> redirectURIs) {
        if (redirectURIs != null && !redirectURIs.isEmpty()) {
            return Optional.of(redirectURIs.get(redirectURIs.size() - 1));
        } else {
            return Optional.empty();
        }
    }

    private ContentType getContentType(HttpEntity responseBody) throws IOException {

        ContentType contentType;

        try {
            contentType = ContentType.getOrDefault(responseBody);
        } catch (UnsupportedCharsetException e) {
            throw new IOException();
        }

        if (contentType.getCharset() == null) {
            return contentType.withCharset(DEFAULT_CONTENT_TYPE_NAME);
        }

        return contentType;

    }
}