package uk.ac.core.dataprovider.logic.exception;

public class OaiPmhInvalidException extends Exception {

    private static final String OAI_PMH_ENDPOINT_NOT_VALID_MSG = "The OAI-PMH endpoint you entered is not valid";

    public OaiPmhInvalidException() {
        this(OAI_PMH_ENDPOINT_NOT_VALID_MSG);
    }

    public OaiPmhInvalidException(String message) {
        this(message, null);
    }

    public OaiPmhInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
