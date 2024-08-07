package uk.ac.core.notification.api;

/**
 *
 * @author lucasanastasiou
 */
public class CannotSendEmailException extends Exception{

    public CannotSendEmailException() {
    }

    public CannotSendEmailException(String message) {
        super("Cannot send email");
    }

    public CannotSendEmailException(String message, Throwable cause) {
        super("Cannot send email", cause);
    }

    public CannotSendEmailException(Throwable cause) {
        super(cause);
    }

    public CannotSendEmailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super("Cannot send email", cause, enableSuppression, writableStackTrace);
    }
    
    
}
