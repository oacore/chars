package uk.ac.core.documentdownload.downloader.fetcher;

import org.apache.http.ProtocolException;

import java.net.URI;

public abstract class AbstractUriException extends ProtocolException {
    private final URI uri;

    public AbstractUriException(String message, URI uri) {
        super(message);
        this.uri = uri;
    }

    public URI getUri() {
        return uri;
    }
}
