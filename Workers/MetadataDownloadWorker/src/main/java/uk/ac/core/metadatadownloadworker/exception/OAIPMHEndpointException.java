package uk.ac.core.metadatadownloadworker.exception;

public class OAIPMHEndpointException extends Exception {

    public OAIPMHEndpointException() {
        super("OAIPMH Endpoint issues were detected.");
    }

    public OAIPMHEndpointException(Throwable cause) {
        super("OAIPMH Endpoint issues were detected: " + cause.getMessage(), cause);
    }
}
