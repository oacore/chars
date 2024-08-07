package uk.ac.core.elasticsearch.exceptions;

import uk.ac.core.common.exceptions.CHARSException;

/**
 *
 * @author lucasanastasiou
 */
public class IndexException extends CHARSException {

    public IndexException() {
    }

    public IndexException(String message) {
        super(message);
    }

    public IndexException(String message, Throwable cause) {
        super(message, cause);
    }

}
