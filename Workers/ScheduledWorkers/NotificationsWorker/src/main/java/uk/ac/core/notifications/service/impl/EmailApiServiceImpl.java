package uk.ac.core.notifications.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.core.notifications.exceptions.NoDataForEmailException;
import uk.ac.core.notifications.model.DeduplicationData;
import uk.ac.core.notifications.model.EmailType;
import uk.ac.core.notifications.model.HarvestingData;
import uk.ac.core.notifications.service.EmailApiService;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class EmailApiServiceImpl implements EmailApiService {
    private static final Logger log = LoggerFactory.getLogger(EmailApiServiceImpl.class);

    private static final int MAX_ATTEMPTS = 5;
    private static final String ERR_MSG_5XX = String.format("Unable to get response from the API endpoint after %s attempts", MAX_ATTEMPTS);
    private static final String ERR_MSG_NO_DATA = "Unable to find necessary fields in the API response";

    public static final String STATS_ENDPOINT = "https://api.core.ac.uk/v3/data-providers/${repo_id}/stats";
    public static final String ISSUES_ENDPOINT = "https://api.core.ac.uk/internal/data-providers/${repo_id}/issues/aggregation";
    public static final String DUPLICATES_ENDPOINT = "https://api.core.ac.uk/internal/data-providers/${repo_id}/duplicates";

    @Override
    public HarvestingData getHarvestingData(int repoId) throws NoDataForEmailException {
        HarvestingData data = new HarvestingData(repoId);
        // make an API call 1
        final String url1 = STATS_ENDPOINT
                .replace("${repo_id}", String.valueOf(repoId));
        String rawResponse1 = this.makeApiCall(url1);
        if (rawResponse1 == null) {
            throw new RuntimeException(ERR_MSG_5XX);
        }
        // parse response 1
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(rawResponse1);
            String metadataCountFieldName = "countMetadata";
            String fulltextCountFieldName = "countFulltext";

            int metadataCount = root.get(metadataCountFieldName).asInt();
            int fulltextCount = root.get(fulltextCountFieldName).asInt();

            if (metadataCount == 0 && fulltextCount == 0) {
                throw new NoDataForEmailException(EmailType.HARVEST_COMPLETED);
            }

            data.setMetadataCount(metadataCount);
            data.setFulltextCount(fulltextCount);
        } catch (JsonProcessingException e) {
            this.logOnJsonError(rawResponse1);
            throw new RuntimeException(e);
        }

        // make an API call 2
        final String url2 = ISSUES_ENDPOINT
                .replace("${repo_id}", String.valueOf(repoId));
        String rawResponse2 = this.makeApiCall(url2);
        if (rawResponse2 == null) {
            throw new RuntimeException(ERR_MSG_5XX);
        }
        // parse response 2
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(rawResponse2);

            String typesCountFieldName = "typesCount";
            String affectedCountFieldName = "total";

            int typesCount = root.get(typesCountFieldName).asInt();
            int affectedCount = root.get(affectedCountFieldName).asInt();

            data.setTypesCount(typesCount);
            data.setAffectedRecordsCount(affectedCount);
        } catch (JsonProcessingException e) {
            this.logOnJsonError(rawResponse1);
            throw new RuntimeException(e);
        }
        return data;
    }

    @Override
    public DeduplicationData getDuplicatesData(int repoId) throws NoDataForEmailException {
        DeduplicationData data = new DeduplicationData(repoId);

        // make an API call
        final String url = DUPLICATES_ENDPOINT.replace("${repo_id}", String.valueOf(repoId));
        String rawResponse = this.makeApiCall(url);
        if (rawResponse == null) {
            throw new RuntimeException(ERR_MSG_5XX);
        }

        // parse response
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(rawResponse);
            String dupListFieldName = "duplicateList";
            String dupCountFieldName = "count";
            if (root.has(dupListFieldName) && root.has(dupCountFieldName)) {

                int actionCount = root.get(dupListFieldName).size();
                int duplicatesCount = root.get(dupCountFieldName).asInt();

                if (actionCount == 0 && duplicatesCount == 0) {
                    throw new NoDataForEmailException(EmailType.DEDUPLICATION_COMPLETED);
                }

                data.setActionCount(actionCount);
                data.setDuplicatesCount(duplicatesCount);
            } else {
                throw new NoDataForEmailException(ERR_MSG_NO_DATA);
            }
        } catch (JsonProcessingException e) {
            this.logOnJsonError(rawResponse);
            throw new RuntimeException(e);
        }
        return data;
    }

    private String makeApiCall(String url) {
        String rawResponse = null;
        int attempts = 0;
        long waitMillis = 5000;
        log.info("Making an API call to {}", url);
        while (attempts < MAX_ATTEMPTS) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

                HttpGet httpGet = new HttpGet(url);
                HttpResponse response = httpClient.execute(httpGet);
                InputStream is = response.getEntity().getContent();
                rawResponse = IOUtils.toString(is, StandardCharsets.UTF_8);
                if (rawResponse != null) {
                    break;
                }
            } catch (Exception e) {
                attempts++;
                this.sleep(waitMillis);
            }
        }
        return rawResponse;
    }

    private void sleep(long waitMillis) {
        try {
            Thread.sleep(waitMillis);
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void logOnJsonError(String apiResponse) {
        log.error("JSON response from API failed to parse");
        log.error("Head of the response:");
        if (apiResponse.length() > 25) {
            log.error(apiResponse.substring(0, 25));
        } else {
            log.error(apiResponse);
        }
    }
}
