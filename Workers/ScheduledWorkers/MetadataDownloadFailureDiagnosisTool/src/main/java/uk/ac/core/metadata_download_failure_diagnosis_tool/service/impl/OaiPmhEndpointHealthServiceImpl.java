package uk.ac.core.metadata_download_failure_diagnosis_tool.service.impl;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import uk.ac.core.metadata_download_failure_diagnosis_tool.service.OaiPmhEndpointHealthService;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;

@Service
public class OaiPmhEndpointHealthServiceImpl implements OaiPmhEndpointHealthService {
    private static final Logger log = LoggerFactory.getLogger(OaiPmhEndpointHealthServiceImpl.class);
    private static final String OAI_PMH_IDENTIFY_VERB = "?verb=Identify";
    private static final String OAI_PMH_LIST_RECORDS_VERB = "?verb=ListRecords&metadataPrefix=";

    @Override
    public boolean isOaiPmhAlive(String oaiPmhEndpoint, String metadataFormat) {
        boolean isIdentifyAlive = this.testUrl(this.composeIdentifyUrl(oaiPmhEndpoint));
        boolean isListRecordsAlive = this.testUrl(this.composeListRecordsUrl(oaiPmhEndpoint, metadataFormat));
        log.info("Identify alive? {}", isIdentifyAlive ? "YES" : "NO");
        log.info("ListRecords alive? {}", isListRecordsAlive ? "YES" : "NO");
        return isIdentifyAlive && isListRecordsAlive;
    }

    private boolean testUrl(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            ResponseHandler<Boolean> responseHandler = httpResponse -> {
                boolean result;
                int status = httpResponse.getStatusLine().getStatusCode();
                log.info("GET {} - status code {}", url, status);
                if (status >= 500) {
                    result = false;
                } else if (status >= 400) {
                    result = false;
                } else {
                    log.info("Trying to parse the response to check if it's compliant with OAI-PMH protocol");
                    String response = EntityUtils.toString(httpResponse.getEntity());
                    result = this.isCompliantWithOaiPmhProtocol(response);
                    log.info("Is it compliant with OAI-PMH protocol? {}", result ? "YES" : "NO");
                }
                return result;
            };
            return httpClient.execute(get, responseHandler);
        } catch (IOException e) {
            log.info("IOException caught while testing URL {}", url);
            return false;
        }
    }

    private boolean isCompliantWithOaiPmhProtocol(String response) {
        try {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new StringReader(response)));
            boolean isValidListRecords = doc.getElementsByTagName("ListRecords").getLength() != 0;
            boolean isValidIdentify = doc.getElementsByTagName("Identify").getLength() != 0;
            return isValidIdentify || isValidListRecords;
        } catch (Exception e) {
            log.error("Exception occurred while parsing the response", e);
            return false;
        }
    }

    private String composeListRecordsUrl(String oaiPmhEndpoint, String metadataFormat) {
        return oaiPmhEndpoint + OAI_PMH_LIST_RECORDS_VERB + metadataFormat;
    }

    private String composeIdentifyUrl(String oaiPmhEndpoint) {
        return oaiPmhEndpoint + OAI_PMH_IDENTIFY_VERB;
    }

    @Override
    public boolean isOaiPmhEmpty(String oaiPmhEndpoint, String metadataFormat) {
        String url = oaiPmhEndpoint + OAI_PMH_LIST_RECORDS_VERB + metadataFormat;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            boolean isEmpty;
            String errorTag = "<error code=\"noRecordsMatch\">No matches for the query</error>";

            HttpGet get = new HttpGet(url);
            ResponseHandler<Boolean> responseHandler = httpResponse -> {
                String response = EntityUtils.toString(httpResponse.getEntity());
                return response.contains(errorTag);
            };
            isEmpty = httpClient.execute(get, responseHandler);

            log.info("Is ListRecords empty? {}", isEmpty ? "YES" : "NO");

            return isEmpty;
        } catch (IOException e) {
            log.info("IOException caught while checking OAI-PMH {} on emptiness", url);
            return false;
        }
    }
}
