package uk.ac.core.dataprovider.logic.util;

import uk.ac.core.dataprovider.logic.util.exception.UrlOperationsException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Common operations on the URL address.
 *
 * Not to be mixed up with HTTP operations.
 */
public final class UrlOperations {

    private static final String URL_HOST_RETRIEVAL_FAILURE_MSG = "Couldn't retrieve the URL host";

    private UrlOperations() {

    }

    public static String getUrlHost(String url) throws UrlOperationsException {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new UrlOperationsException(URL_HOST_RETRIEVAL_FAILURE_MSG);
        }
        return uri.getHost();
    }

}