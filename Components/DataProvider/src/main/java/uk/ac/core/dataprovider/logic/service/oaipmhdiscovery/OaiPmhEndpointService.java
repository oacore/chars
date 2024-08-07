package uk.ac.core.dataprovider.logic.service.oaipmhdiscovery;

import uk.ac.core.dataprovider.logic.dto.HttpResponseDTO;
import uk.ac.core.dataprovider.logic.entity.IdentifyResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * OAI-PMH Endpoint Service.
 */
public interface OaiPmhEndpointService {

    /**
     * Finds OAI-PMH endpoint of the given url with high probability, if the one exists.
     *
     * @param url given url
     *
     * @return OAI-PMH endpoint if there is one
     */
    Optional<IdentifyResponse> findOaiPmhEndpoint(String url) throws IOException;

    /**
     * Checks if the response from an OAI-PMH endpoint meets bare minimum requirements.
     *
     * @param httpResponse http response from an OAI-PMH endpoint
     *
     * @return true if the endpoint is valid, otherwise false
     */
    boolean isPartiallyCompliantWithOaiPmhProtocol(HttpResponseDTO httpResponse);

    HttpResponseDTO checkHostUrlForAccessibility(String url) throws IOException;

    HttpResponseDTO checkUrlForAccessibility(String url) throws IOException;


        /**
         * @see OaiPmhEndpointService#isPartiallyCompliantWithOaiPmhProtocol(HttpResponseDTO)
         */
    boolean isPartiallyCompliantWithOaiPmhProtocol(String url);
}