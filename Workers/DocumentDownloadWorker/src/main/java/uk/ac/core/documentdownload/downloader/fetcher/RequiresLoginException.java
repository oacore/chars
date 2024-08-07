package uk.ac.core.documentdownload.downloader.fetcher;

import java.net.URI;

public class RequiresLoginException extends AbstractUriException {

    public RequiresLoginException(String message, URI uri) {
        super(message, uri);
    }
}
