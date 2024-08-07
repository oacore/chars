package uk.ac.core.services.web.affiliations.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryRequest;
import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryResponse;
import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryResponseItem;

import java.util.Date;
import java.util.Iterator;

@Service
public class CrossrefApiExtractionService {

    private static final Logger log = LoggerFactory.getLogger(CrossrefApiExtractionService.class);
    private static final String BASE_URL = "https://api.crossref.org/works?filter=doi:";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final HttpEntity entity;

    @Autowired
    public CrossrefApiExtractionService(RestTemplate restTemplate, ObjectMapper objectMapper, HttpEntity entity) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.entity = entity;
    }

    public AffiliationsDiscoveryResponse extract(AffiliationsDiscoveryRequest request) {
        /**
         * STEP 0: PREPARE EVERYTHING
         */
        log.info("Extracting affiliations with Crossref API method ...");
        // performance
        long start = System.currentTimeMillis(), end;
        // http
        if (request.getDoi().equals("null")) {
            log.error("Article DOI is null");
            AffiliationsDiscoveryResponse response = new AffiliationsDiscoveryResponse();
            response.setCoreId(request.getCoreId());
            response.setRepoId(request.getRepoId());
            response.setDateCreated(new Date());
            response.setSource("crossref-api");
            end = System.currentTimeMillis();
            response.setTook(end - start);
            response.setCount(0);
            response.setMessage("DOI is null");
            return response;
        }
        // response
        AffiliationsDiscoveryResponse response = new AffiliationsDiscoveryResponse();
        response.setCoreId(request.getCoreId());
        response.setRepoId(request.getRepoId());
        response.setDateCreated(new Date());
        response.setSource("crossref-api");
        try {
            /**
             * STEP 1: GET JSON
             */
            String url = BASE_URL + request.getDoi();
            ResponseEntity<String> httpResponse = this.restTemplate.exchange(
                    url, HttpMethod.GET, this.entity, String.class
            );
            JsonNode root = this.objectMapper.readTree(httpResponse.getBody());
            /**
             * STEP 2: EXTRACT DATA AND ADD IT TO RESPONSE
             */
            JsonNode itemsList = root.get("message").get("items");
            Iterator<JsonNode> itemsIterator = itemsList.elements();
            while (itemsIterator.hasNext()) {
                JsonNode item = itemsIterator.next();
                JsonNode authorsList = item.get("author");
                Iterator<JsonNode> authorsIterator = authorsList.elements();
                while (authorsIterator.hasNext()) {
                    JsonNode author  = authorsIterator.next();
                    AffiliationsDiscoveryResponseItem responseItem = new AffiliationsDiscoveryResponseItem();
                    // author name
                    responseItem.setAuthor(
                            author.get("given").asText() + " " + author.get("family").asText()
                    );
                    // author employment
                    String institutionName = author
                            .get("affiliation")
                            .elements().next()
                            .get("name").asText();
                    responseItem.setInstitution(institutionName);
                    responseItem.setConfidence(1.0);
                    response.getHits().add(responseItem);
                }
            }
        } catch (Exception e) {
            log.error("Exception occurred", e);
            end = System.currentTimeMillis();
            response.setTook(end - start);
            response.setCount(0);
            response.setMessage("Error message: " + e.getMessage());
            return response;
        }
        end = System.currentTimeMillis();
        response.setCount(response.getHits().size());
        response.setTook(end - start);
        response.setMessage("OK");
        log.info("Crossref API method finished in {} ms", response.getTook());
        log.info("Extracted {} affiliations", response.getCount());
        return response;
    }

}
