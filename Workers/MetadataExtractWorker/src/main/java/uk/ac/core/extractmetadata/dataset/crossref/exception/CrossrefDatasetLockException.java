package uk.ac.core.extractmetadata.dataset.crossref.exception;

public class CrossrefDatasetLockException extends Exception {
    public CrossrefDatasetLockException(String message) {
        super(message);
    }

    public CrossrefDatasetLockException(Throwable cause) {
        super(cause);
    }

    public CrossrefDatasetLockException(String message, Throwable cause) {
        super(message, cause);
    }
}
