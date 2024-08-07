package uk.ac.core.documentdownload.downloader.fetcher;

import java.net.URI;

/**
 *
 * @author samuel
 */
public class FileTooBigException extends Exception {

    private URI uri;
    
    public FileTooBigException(String message, URI uri) {
        super(message);
        this.uri = uri;
    }

    public URI getUrl() {
        return uri;
    }
}
