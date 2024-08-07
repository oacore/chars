package uk.ac.core.documentdownload.downloader.fetcher;

import java.net.URI;

import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectException;

/**
 *
 * @author samuel
 */
public class IllegalDomainException extends AbstractUriException {

    public IllegalDomainException(String message, URI uri) {
        super(message, uri);
    }
}