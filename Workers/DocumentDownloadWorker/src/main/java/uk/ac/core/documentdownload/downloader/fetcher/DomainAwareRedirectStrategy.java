package uk.ac.core.documentdownload.downloader.fetcher;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.core.database.service.repositories.RepositoryDomainException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author samuel
 */
public class DomainAwareRedirectStrategy extends DefaultRedirectStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainAwareRedirectStrategy.class);

    // Keys used to access data in the Http execution context.
    private static final String PERM_REDIRECT_CONTEXT_KEY = "perm-redirect";
    private static final String REDIRECT_COUNT_CONTEXT_KEY = "redirect-count";

    private final boolean sameDomainPolicyEnforced;
    private final List<RepositoryDomainException> domainExceptions;
    private final String repositoryDomain;

    public DomainAwareRedirectStrategy(boolean sameDomainPolicyEnforced, List<RepositoryDomainException> domainExceptions, String repositoryDomain) {
        this.sameDomainPolicyEnforced = sameDomainPolicyEnforced;
        this.domainExceptions = domainExceptions;
        this.repositoryDomain = repositoryDomain;
    }

    @Override
    public URI getLocationURI(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        URI result = super.getLocationURI(request, response, context);

        // HACK - some sites return a redirect with an explicit port number
        // that's the same as
        // the default port (e.g. 80 for http), and then when you use this
        // to make the next
        // request, the presence of the port in the domain triggers another
        // redirect, so you
        // fail with a circular redirect error. Avoid that by converting the
        // port number to
        // -1 in that case.
        //
        // Detailed scenrio:
        // http://www.test.com/MyPage ->
        // http://www.test.com:80/MyRedirectedPage ->
        // http://www.test.com/MyRedirectedPage
        // We can save bandwidth:
        if (result.getScheme().equalsIgnoreCase("http") && (result.getPort() == 80)) {
            try {
                result = new URI(result.getScheme(), result.getUserInfo(), result.getHost(), -1, result.getPath(), result.getQuery(), result.getFragment());
            } catch (URISyntaxException e) {
                LOGGER.warn("Unexpected exception removing port from URI", e);
            }
        }

        // Keep track of the number of redirects.
        Integer count = (Integer) context.getAttribute(REDIRECT_COUNT_CONTEXT_KEY);
        if (count == null) {
            count = 0;
        }

        context.setAttribute(REDIRECT_COUNT_CONTEXT_KEY, count + 1);

        // Record the last permanent redirect
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
            context.setAttribute(PERM_REDIRECT_CONTEXT_KEY, result);
        }

        if (this.sameDomainPolicyEnforced && this.repositoryDomain != null && !this.repositoryDomain.isEmpty()) {
            boolean domainExceptionMatch = false;
            if (this.domainExceptions != null) {
                for (RepositoryDomainException domainException : this.domainExceptions) {
                    boolean contains = result.getHost().toLowerCase().contains(domainException.getDomainUrl().toLowerCase());
                    domainExceptionMatch |= contains;
                }
            }
            if (!domainExceptionMatch
                    && !result.getHost().equals("hdl.handle.net")
                    && !result.getHost().equals("dx.doi.org")
                    && !result.getHost().equals("doi.org")
                    && !result.getHost().equals(this.repositoryDomain)) {

                throw new IllegalDomainException(
                        String.format("The current URL is not allowed to be visted %s", result.getHost()),
                        result);
            }
        }

        return result;
    }
    
    /**
     * @param location
     * @return 
     * @throws org.apache.http.ProtocolException
     * @since 4.1
     */
    @Override
    protected URI createLocationURI(String location) throws ProtocolException {        
        // See CORE-1760. Pls suggest a better way if possible
        try {
            URIBuilder uriBuilder = new URIBuilder(new URI(location).normalize());
        } catch (final URISyntaxException ex) {
            // URL encoding the whole String will also encode the /
            // We don't want this
            location = location
                    .replace(" ", "%20")
                    .replace(",", "%2C");
        }
        try {            
            final URIBuilder b = new URIBuilder(new URI(location).normalize());
            final String host = b.getHost();
            if (host != null) {
                b.setHost(host.toLowerCase(Locale.ROOT));
            }
            final String path = b.getPath();
            if (TextUtils.isEmpty(path)) {
                b.setPath("/");
            }
            return b.build();
        } catch (final URISyntaxException ex) {
            throw new ProtocolException("Invalid redirect URI: " + location, ex);
        }
    }
}
