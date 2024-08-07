package uk.ac.core.cloud.client;

/**
 *
 * @author lucas
 */
public class CloudException extends Exception {

    public CloudException() {
        super();    
    }

    public CloudException(String message) {
        super(message);
    }

    public CloudException(Throwable cause) {
        super(cause);
    }

    public CloudException(String message, Throwable cause) {
        super(message, cause);
    }
}
