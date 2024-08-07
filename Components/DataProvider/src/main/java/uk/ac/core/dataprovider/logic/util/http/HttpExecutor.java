package uk.ac.core.dataprovider.logic.util.http;

import uk.ac.core.dataprovider.logic.dto.HttpResponseDTO;
import java.io.IOException;

public interface HttpExecutor {
    /**
     * Performs HTTP GET request to a target url.
     * The redirect is performed by default.
     *
     * @param url target url
     *
     * @return response
     *
     * @throws IOException if url is invalid or the request can't be done
     */
    HttpResponseDTO get(String url) throws IOException;
}
