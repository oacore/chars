package uk.ac.core.common.exceptions;

/**
 *
 * @author lucasanastasiou
 */
public class CHARSException extends Exception {

    public CHARSException() {
        super("CHARSException ");
    }

    public CHARSException(String message) {
        super("CHARSException " + message);
    }

    public CHARSException(String message, Throwable cause) {
        super("CHARSException " + message, cause);
    }
}
