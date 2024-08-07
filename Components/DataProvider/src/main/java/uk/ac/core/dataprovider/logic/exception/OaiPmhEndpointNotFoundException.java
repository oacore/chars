package uk.ac.core.dataprovider.logic.exception;

/**
 * OAI-PMH endpoint not found exception.
 */
public class OaiPmhEndpointNotFoundException extends Exception {

    private static final String OAI_PMH_ENDPOINT_NOT_FOUND_MSG = "A data provider doesn't seem to have an OAI-PMH endpoint.";

    public OaiPmhEndpointNotFoundException() {
        super(OAI_PMH_ENDPOINT_NOT_FOUND_MSG);
    }
}