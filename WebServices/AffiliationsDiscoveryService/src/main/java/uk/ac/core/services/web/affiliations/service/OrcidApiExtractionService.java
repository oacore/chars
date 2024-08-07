package uk.ac.core.services.web.affiliations.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
public class OrcidApiExtractionService {

    private static final Logger log = LoggerFactory.getLogger(OrcidApiExtractionService.class);
    private static final String BASE_URL = "https://pub.orcid.org/v3.0/";

    private final OrcidDAO orcidDAO;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final HttpEntity entity;

    @Autowired
    public OrcidApiExtractionService(OrcidDAO orcidDAO, RestTemplate restTemplate, ObjectMapper objectMapper, HttpEntity entity) {
        this.orcidDAO = orcidDAO;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.entity = entity;
    }

    public AffiliationsDiscoveryResponse extract(AffiliationsDiscoveryRequest request) {
        /**
         * STEP 0: PREPARE OBJECTS
         */
        log.info("Extracting affiliations with ORCID API method ...");
        long start = System.currentTimeMillis(), end;
        // orcids
        List<String> orcids = this.orcidDAO.getOrcidsOfAuthorsOfArticle(request.getCoreId());
        if (orcids.isEmpty()) {
            end = System.currentTimeMillis();
            AffiliationsDiscoveryResponse response = new AffiliationsDiscoveryResponse();
            response.setCoreId(request.getCoreId());
            response.setRepoId(request.getRepoId());
            response.setDateCreated(new Date());
            response.setCount(0);
            response.setTook(end - start);
            response.setMessage("No ORCIDs");
            response.setSource("orcid-api");
            return response;
        }
        // response
        AffiliationsDiscoveryResponse response = new AffiliationsDiscoveryResponse();
        response.setCoreId(request.getCoreId());
        response.setRepoId(request.getRepoId());
        response.setDateCreated(new Date());
        response.setSource("orcid-api");
        try {
            /**
             * STEP 1: QUERY ORCID API AND PARSE THE RESPONSE
             */
            for (String orcid : orcids) {
                AffiliationsDiscoveryResponseItem hit = this.processSingleOrcid(orcid);
                if (hit != null) {
                    response.getHits().add(hit);
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
        response.setCount(response.getHits().size());
        response.setMessage("OK");
        end = System.currentTimeMillis();
        response.setTook(end - start);
        log.info("ORCID API method finished in {} ms", response.getTook());
        log.info("Extracted {} affiliations", response.getCount());
        return response;
    }

    private AffiliationsDiscoveryResponseItem processSingleOrcid(String orcid) throws JsonProcessingException {
        AffiliationsDiscoveryResponseItem responseItem = new AffiliationsDiscoveryResponseItem();
        // author name
        String personUrl = BASE_URL + orcid + "/person";
        ResponseEntity<String> personInfo = this.restTemplate.exchange(
                personUrl,
                HttpMethod.GET,
                this.entity,
                String.class
        );
        JsonNode personRoot = this.objectMapper.readTree(personInfo.getBody());
        if (!personRoot.get("name").isNull()) {
            String givenName = personRoot
                    .get("name")
                    .get("given-names")
                    .get("value")
                    .asText();
            String familyName = personRoot
                    .get("name")
                    .get("family-name")
                    .get("value")
                    .asText();
            responseItem.setAuthor(givenName + " " + familyName);
        } else {
            return null;
        }
        // author institution
        String relevantInstitution = "";
        String employmentsUrl = BASE_URL + orcid + "/employments";
        ResponseEntity<String> employmentsResponse = this.restTemplate.exchange(
                employmentsUrl, HttpMethod.GET, this.entity, String.class);
        JsonNode employmentsRoot = this.objectMapper.readTree(employmentsResponse.getBody());
        Iterator<JsonNode> affiliationGroup = employmentsRoot.get("affiliation-group").elements();
        while (affiliationGroup.hasNext()) {
            JsonNode affiliationGroupElement = affiliationGroup.next();
            Iterator<JsonNode> summariesIterator = affiliationGroupElement.get("summaries").elements();
            while (summariesIterator.hasNext()) {
                JsonNode employmentSummary = summariesIterator.next().get("employment-summary");
                if (employmentSummary.get("end-date").asText().equals("null")) {
                    relevantInstitution = employmentSummary.get("organization").get("name").asText();
                }
            }
        }
        responseItem.setInstitution(relevantInstitution);
        // external ids
        String externalIdsUrl = BASE_URL + orcid + "/external-identifiers";
        ResponseEntity<String> externalIdsResponse = this.restTemplate.exchange(
                externalIdsUrl, HttpMethod.GET, this.entity, String.class);
        JsonNode externalIdsRoot = this.objectMapper.readTree(externalIdsResponse.getBody());
        Iterator<JsonNode> externalIdsIterator = externalIdsRoot.get("external-identifier").elements();
        while (externalIdsIterator.hasNext()) {
            Map<String, String> externalIds = new HashMap<>();
            JsonNode externalIdsElement = externalIdsIterator.next();
            String idType = externalIdsElement.get("external-id-type").asText();
            String idValue = externalIdsElement.get("external-id-value").asText();
            externalIds.put(idType, idValue);
            responseItem.setIdentifiers(externalIds);
        }
        // confidence
        responseItem.setConfidence(1.);
        return responseItem;
    }

}
