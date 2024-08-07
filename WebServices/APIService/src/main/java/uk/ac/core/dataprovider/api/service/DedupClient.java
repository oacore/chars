package uk.ac.core.dataprovider.api.service;

import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.supervisor.client.SupervisorClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DedupClient {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(SupervisorClient.class);

    private final String dedupUrl;
    private final RestTemplate restTemplate;

    public DedupClient(String deduplicationUrl) {
        this.restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        this.restTemplate.setMessageConverters(messageConverters);
        this.dedupUrl = deduplicationUrl;
    }

    public Set<Integer> getDuplicatesRequest(String title) throws CHARSException {
        try {
            String uri = UriComponentsBuilder.fromHttpUrl(dedupUrl)
                    .path("duplicates/find_duplicates")
                    .queryParam("title", title)
                    .toUriString();
            logger.debug("Calling: {}", uri);

            ResponseEntity<Integer[]> responseEntity = restTemplate.getForEntity(uri, Integer[].class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                logger.info("Founded {} annoy duplicates for title {}", responseEntity.getBody().length, title);
                return Stream.of(responseEntity.getBody()).collect(Collectors.toSet());
            } else {
                logger.error("Failed to check duplicates for title '{}'. Response {}", title, responseEntity.getStatusCode());
                throw new CHARSException();
            }

        } catch (Exception e) {
            logger.error("Failed to check duplicates for title '{}'", title, e);
            throw new CHARSException();
        }
    }
}
