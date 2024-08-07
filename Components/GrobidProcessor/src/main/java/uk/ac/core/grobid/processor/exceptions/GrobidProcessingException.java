package uk.ac.core.grobid.processor.exceptions;

/**
 *
 * @author vb4826
 */
public class GrobidProcessingException extends Exception {

    public GrobidProcessingException() {
    }

    public GrobidProcessingException(String message) {
        super("Grobid processing exception : " + message);
    }

    public GrobidProcessingException(String message, Throwable cause) {
        super("Grobid processing exception : " + message, cause);
    }

}
