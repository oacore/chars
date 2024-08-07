package uk.ac.core.dataprovider.logic.service.oaipmhdiscovery.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.ac.core.dataprovider.logic.dto.HttpResponseDTO;
import uk.ac.core.dataprovider.logic.entity.IdentifyResponse;
import uk.ac.core.dataprovider.logic.service.oaipmhdiscovery.OaiPmhEndpointService;
import uk.ac.core.dataprovider.logic.util.http.ApacheHttpExecutor;
import uk.ac.core.dataprovider.logic.util.http.HttpExecutor;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class OaiPmhEndpointImpl implements OaiPmhEndpointService {

    private static final List<String> OAI_PMH_URL_PATHS = Collections.unmodifiableList(Arrays.asList(
            "oai",
            "oai/",
            "oai/rioxx",
            "oai/openaire",
            "oai/request",
            "dspace-oai/request",
            "oai/driver",
            "do/oai/",
            "cgi/oai2",
            "index/oai",
            "index.php/index/oai",
            "ws/oai",
            "oai-pmh-repository/request",
            "oai/oai.php",
            "dlibra/oai-pmh-repository.xml",
            "oai-pmh-repository.xml",
            "OAI/Server",
            "luna/servlet/oai",
            "oai.pl",
            "cgi-bin/oai.exe",
            "OAI-PUB",
            "oaicat/OAIHandler",
            "phpoai/oai2.php",
            "oai2d.py/",
            "oai2d",
            "cgi-bin/oaiserver",
            "fedora/oai",
            "perl/oai2"
    ));

    private static final String BASIC_CHECK_XML_OAIPMH_ELEMENT = "<repositoryName>";
    private static final String QUERY_SEPARATOR = "[?]";
    private static final String TRAILING_SYMBOL = "/";
    private static final String NOT_REACHABLE_URL_MSG = "Couldn't access the OAI-PMH endpoint: %s. The error message: %s";
    private static final String IDENTIFY_VERB_QUERY = "?verb=Identify";
    private static final int AUTHORITY_INDEX = 2;

    private final HttpExecutor httpExecutor = new ApacheHttpExecutor();

    private static final Logger LOG = LoggerFactory.getLogger(OaiPmhEndpointImpl.class);

    @Override
    public Optional<IdentifyResponse> findOaiPmhEndpoint(String url) throws IOException {

        HttpResponseDTO responseFromRootUrl;
        responseFromRootUrl = checkHostUrlForAccessibility(normalizeUrl(url));

        for (String oaiPmhUrlPath : OAI_PMH_URL_PATHS) {
            HttpResponseDTO response;

            try {
                String checkQuery = oaiPmhUrlPath + IDENTIFY_VERB_QUERY;

                if (responseFromRootUrl.getUri() != null) {
                    response = httpExecutor.get(attachTrailingSlashIfNone(responseFromRootUrl.getUri()) + checkQuery);
                } else {
                    response = httpExecutor.get(attachTrailingSlashIfNone(url) + checkQuery);
                }

            } catch (Exception ignored) {
                //there are too many unidentified exceptions that might occur,
                // therefore this is the only reliable way to loop through all paths
                continue;
            }

            if (isPartiallyCompliantWithOaiPmhProtocol(response)) {
                String[] checkUrlArray = response.getUri().split(QUERY_SEPARATOR);
                String oaiPmhEndpoint = checkUrlArray[0];
                return this.parseIdentifyResponse(response);
            }

        }

        return Optional.empty();
    }

    private String normalizeUrl(String uri) {
        return URI.create(uri.replaceAll(" ", "")).normalize().toString();
    }

    public HttpResponseDTO checkUrlForAccessibility(String url) throws IOException {
        HttpResponseDTO responseFromHostUrl = httpExecutor.get(url);

        if (responseFromHostUrl.is5xxStatusCode()) {
            throw new IOException("Host url isn't accessible.");
        }

        return responseFromHostUrl;
    }

    public HttpResponseDTO checkHostUrlForAccessibility(String url) throws IOException {
        return this.checkUrlForAccessibility(getHomepageUrl(url));
    }

    private String getHomepageUrl(String url) {
        String[] urlArray = url.split(TRAILING_SYMBOL);
        return urlArray[0] + "//" + urlArray[1] + urlArray[AUTHORITY_INDEX];
    }

    private String attachTrailingSlashIfNone(String url) {
        return url.endsWith(TRAILING_SYMBOL) ? url : url + TRAILING_SYMBOL;
    }

    private Optional<IdentifyResponse> parseIdentifyResponse(HttpResponseDTO response) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            Document parsedResponse = documentBuilderFactory.newDocumentBuilder().parse(new InputSource(new StringReader(response.getResponseBody())));
            IdentifyResponse identifyResponse = new IdentifyResponse(
                    parsedResponse.getElementsByTagName("repositoryName").item(0).getTextContent(),
                    parsedResponse.getElementsByTagName("adminEmail").item(0).getTextContent(),
                    parsedResponse.getElementsByTagName("baseURL").item(0).getTextContent()
            );
            return Optional.of(identifyResponse);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
            LOG.error(String.format("Identify responded but was corrupted. %s %s", response.getUri(), e.getMessage()));
        }
        return Optional.empty();
    }

    @Override
    public boolean isPartiallyCompliantWithOaiPmhProtocol(HttpResponseDTO httpResponse) {
        if (!httpResponse.isSuccessful() || !httpResponse.isXmlResponse()) {
            return false;
        }
        return httpResponse.getResponseBody().contains(BASIC_CHECK_XML_OAIPMH_ELEMENT);
    }

    @Override
    public boolean isPartiallyCompliantWithOaiPmhProtocol(String url) {
        HttpResponseDTO responseDTO;

        try {
            responseDTO = httpExecutor.get(url);
        } catch (IOException e) {
            LOG.error(String.format(NOT_REACHABLE_URL_MSG, url, e.getMessage()));
            return false;
        }

        return isPartiallyCompliantWithOaiPmhProtocol(responseDTO);
    }
}