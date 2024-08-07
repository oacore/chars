package uk.ac.core.services.web.affiliations.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import uk.ac.core.database.orcid.OrcidDAO;
import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryRequest;
import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryResponse;
import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryResponseItem;

import java.util.*;

@Service
public class OpenAlexApiExtractionService {
    private static final Logger log = LoggerFactory.getLogger(OpenAlexApiExtractionService.class);
    private static final String BASE_URL_WORKS = "https://api.openalex.org/works/";
    private static final String BASE_URL_AUTHORS = "https://api.openalex.org/authors/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final HttpEntity entity;

    @Autowired
    public OpenAlexApiExtractionService(RestTemplate restTemplate, ObjectMapper objectMapper, HttpEntity entity) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.entity = entity;
    }

    public AffiliationsDiscoveryResponse extract(AffiliationsDiscoveryRequest request) {
        long start = System.currentTimeMillis(), end;
        AffiliationsDiscoveryResponse response = new AffiliationsDiscoveryResponse();
        response.setSource("openalex-api");
        response.setCoreId(request.getCoreId());
        response.setRepoId(request.getRepoId());
        response.setDateCreated(new Date());
        try {
            /**
             * STEP 1: SEARCH 'WORKS' WITH DOI
             */
            List<AffiliationsDiscoveryResponseItem> worksResponseItems = new ArrayList<>();
            if (request.getDoi() != null) {
                String worksUrl = BASE_URL_WORKS + "doi:" + request.getDoi();
                log.info("Trying 'works' endpoint");
                log.info("Requesting {} ...", worksUrl);
                ResponseEntity<String> worksResponse = this.restTemplate.exchange(
                        worksUrl,
                        HttpMethod.GET,
                        this.entity,
                        String.class
                );
                log.info("Parsing response ...");
                worksResponseItems = this.parseWorksResponse(worksResponse);
                log.info("Received {} response items from 'works' endpoint", worksResponseItems.size());
            } else {
                log.info("DOI for this article is null");
                log.info("Searching 'works' endpoint failed");
            }
            /**
             * STEP 2: ENRICH WITH IDENTIFIERS
             */
            if (!worksResponseItems.isEmpty()) {
                log.info("Adding more identifiers");
                this.enrichWithIdentifiers(worksResponseItems);
            }
            /**
             * STEP 3: MERGE RESULTS AND COMPOSE A RESPONSE
             */
            if (worksResponseItems.isEmpty()) {
                log.info("Endpoints failed");
                end = System.currentTimeMillis();
                response.setTook(end - start);
                response.setCount(0);
                response.setMessage("No data");
                return response;
            }
            response.setHits(worksResponseItems);
            response.setCount(worksResponseItems.size());
            end = System.currentTimeMillis();
            response.setTook(end - start);
            response.setMessage("OK");
            return response;
        } catch (Exception e) {
            log.error("Exception occurred", e);
            end = System.currentTimeMillis();
            response.setTook(end - start);
            response.setCount(0);
            response.setMessage("Error message: " + e.getMessage());
            return response;
        }
    }

    private void enrichWithIdentifiers(List<AffiliationsDiscoveryResponseItem> worksResponseItems) throws JsonProcessingException {
        for (AffiliationsDiscoveryResponseItem responseItem: worksResponseItems) {
            String authorUrl = BASE_URL_AUTHORS + responseItem.getIdentifiers().get("OpenAlexId");
            ResponseEntity<String> authorResponse = this.restTemplate.exchange(
                    authorUrl,
                    HttpMethod.GET,
                    this.entity,
                    String.class
            );
            JsonNode root = this.objectMapper.readTree(authorResponse.getBody());
            JsonNode ids = root.get("ids");
            Map<String, String> identifiers = this.objectMapper.convertValue(
                    ids,
                    new TypeReference<Map<String, String>>() {}
            );
            responseItem.setIdentifiers(identifiers);
        }
    }

    private List<AffiliationsDiscoveryResponseItem> parseWorksResponse(ResponseEntity<String> worksResponse) throws JsonProcessingException {
        List<AffiliationsDiscoveryResponseItem> result = new ArrayList<>();
        JsonNode root = this.objectMapper.readTree(worksResponse.getBody());
        if (root.get("authorships").isArray()) {
            Iterator<JsonNode> authorships = root.get("authorships").elements();
            while (authorships.hasNext()) {
                JsonNode authorship = authorships.next();
                // author name
                String authorName = authorship.get("author").get("display_name").asText();
                // OpenAlex id
                String openAlexUrl = authorship.get("author").get("id").asText();
                String openAlexId = openAlexUrl.substring(openAlexUrl.lastIndexOf('/'));
                // institution
                String relevantInstitution = "";
                if (authorship.get("institutions").isArray()) {
                    JsonNode latestInstitution = authorship.get("institutions").elements().next();
                    String instName = latestInstitution.get("display_name").asText();
                    String countryCode = latestInstitution.get("country_code").asText();
                    relevantInstitution = countryCode != null
                            ? (instName + ", " + countryCode) : instName;
                }
                AffiliationsDiscoveryResponseItem item = new AffiliationsDiscoveryResponseItem();
                item.setAuthor(authorName);
                item.setInstitution(relevantInstitution);
                item.getIdentifiers().put("OpenAlexId", openAlexId);
                item.setConfidence(1.);
                result.add(item);
            }
        }
        return result;
    }
}
