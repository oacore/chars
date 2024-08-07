package uk.ac.core.services.web.affiliations.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.services.web.affiliations.exception.RequestPreparationException;
import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryRequest;
import uk.ac.core.services.web.affiliations.model.InputMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class InternalMetadataConverter {

    private static final Logger log = LoggerFactory.getLogger(InternalMetadataConverter.class);

    private final Client client;
    private final ObjectMapper objectMapper;

    @Autowired
    public InternalMetadataConverter(Client client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public AffiliationsDiscoveryRequest getRequest(InputMetadata metadata) throws RequestPreparationException {
        try {
            AffiliationsDiscoveryRequest request = new AffiliationsDiscoveryRequest();
            log.info("Trying to compose a request ...");
            // CORE ID
            if (metadata.getCoreId() != null) {
                request.setCoreId(metadata.getCoreId());
            } else {
                request.setCoreId(this.getCoreIdFromMetadata(metadata));
            }
            // REPO ID
            if (metadata.getRepoId() != null) {
                request.setRepoId(metadata.getRepoId());
            } else {
                request.setRepoId(this.getRepoIdFromMetadata(metadata));
            }
            // DOI
            if (metadata.getDoi() != null) {
                request.setDoi(metadata.getDoi());
            } else {
                request.setDoi(this.getDoiFromMetadata(metadata));
            }
            // AUTHORS
            request.setAuthors(this.getAuthorsFromMetadata(metadata));
            return request;
        } catch (Exception e) {
            throw new RequestPreparationException(
                    "Exception while preparing the affiliations discovery request: " + e.getMessage(), e);
        }
    }

    private List<String> getAuthorsFromMetadata(InputMetadata metadata) throws JsonProcessingException, ExecutionException, InterruptedException {
        log.info("fetching authors ...");
        List<String> authors = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest("articles");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (metadata.getCoreId() != null) {
            queryBuilder.must(QueryBuilders.matchQuery("id", metadata.getCoreId()));
        }
        if (metadata.getDoi() != null) {
            queryBuilder.must(QueryBuilders.matchQuery("doi", metadata.getDoi()));
        }
        if (metadata.getOai() != null) {
            queryBuilder.must(QueryBuilders.matchQuery("oai", metadata.getOai()));
        }
        if (metadata.getTitle() != null && metadata.getYear() != null) {
            queryBuilder.must(QueryBuilders.matchQuery("title", metadata.getTitle()));
            queryBuilder.must(QueryBuilders.matchQuery("year", metadata.getYear()));
        }
        if (metadata.getRepoId() != null) {
            queryBuilder.must(QueryBuilders.nestedQuery(
                    "repositories",
                    QueryBuilders.matchQuery("repositories.id", metadata.getRepoId()),
                    ScoreMode.Max));
//            queryBuilder.must(QueryBuilders.matchQuery("repositories.id", metadata.getRepoId()));
        }
        searchSourceBuilder
                .query(queryBuilder)
                .sort(new ScoreSortBuilder().order(SortOrder.DESC));
        searchRequest.source(searchSourceBuilder);
        log.info("request finished, ready to search ...");
        Future<SearchResponse> future = this.client.search(searchRequest);
        do {
            log.info("waiting for the result ...");
            TimeUnit.SECONDS.sleep(1);
        } while (!future.isDone());
        SearchResponse response = future.get();
        SearchHit hit = response.getHits().getAt(0);
        String json = hit.toString();
        JsonNode source = objectMapper.readTree(json).get("_source");
        source.get("authors").forEach(
                a -> log.info("author fetched: {}", authors.add(a.asText()))
        );
        return authors;
    }

    private String getDoiFromMetadata(InputMetadata metadata) throws JsonProcessingException, ExecutionException, InterruptedException {
        log.info("fetching doi ...");
        SearchRequest searchRequest = new SearchRequest("articles");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (metadata.getCoreId() != null) {
            queryBuilder.must(QueryBuilders.matchQuery("id", metadata.getCoreId()));
        }
        if (metadata.getOai() != null) {
            queryBuilder.must(QueryBuilders.matchQuery("oai", metadata.getOai()));
        }
        if (metadata.getTitle() != null && metadata.getYear() != null) {
            queryBuilder.must(QueryBuilders.matchQuery("title", metadata.getTitle()));
            queryBuilder.must(QueryBuilders.matchQuery("year", metadata.getYear()));
        }
        if (metadata.getRepoId() != null) {
            queryBuilder.must(QueryBuilders.nestedQuery(
                    "repositories",
                    QueryBuilders.matchQuery("repositories.id", metadata.getRepoId()),
                    ScoreMode.Max));
//            queryBuilder.must(QueryBuilders.matchQuery("repositories.id", metadata.getRepoId()));
        }
        searchSourceBuilder
                .query(queryBuilder)
                .sort(new ScoreSortBuilder().order(SortOrder.DESC));
        searchRequest.source(searchSourceBuilder);
        log.info("request finished, ready to search ...");
        Future<SearchResponse> future = this.client.search(searchRequest);
        do {
            log.info("waiting for the result ...");
            TimeUnit.SECONDS.sleep(1);
        } while (!future.isDone());
        SearchResponse response = future.get();
        SearchHit hit = response.getHits().getAt(0);
        String json = hit.toString();
        JsonNode source = objectMapper.readTree(json).get("_source");
        return source.get("doi").asText();
    }

    private Integer getRepoIdFromMetadata(InputMetadata metadata) throws JsonProcessingException, ExecutionException, InterruptedException {
        log.info("fetching repository id ...");
        SearchRequest searchRequest = new SearchRequest("articles");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (metadata.getDoi() != null) {
            queryBuilder.must(QueryBuilders.matchQuery("doi", metadata.getDoi()));
        }
        if (metadata.getOai() != null) {
            queryBuilder.must(QueryBuilders.matchQuery("oai", metadata.getOai()));
        }
        if (metadata.getTitle() != null && metadata.getYear() != null) {
            queryBuilder.must(QueryBuilders.matchQuery("title", metadata.getTitle()));
            queryBuilder.must(QueryBuilders.matchQuery("year", metadata.getYear()));
        }
        if (metadata.getCoreId() != null) {
            queryBuilder.must(QueryBuilders.matchQuery("id", metadata.getCoreId()));
        }
        searchSourceBuilder
                .query(queryBuilder)
                .sort(new ScoreSortBuilder().order(SortOrder.DESC));
        searchRequest.source(searchSourceBuilder);
        log.info("request finished, ready to search ...");
        Future<SearchResponse> future = this.client.search(searchRequest);
        do {
            log.info("waiting for the result ...");
            TimeUnit.SECONDS.sleep(1);
        } while (!future.isDone());
        SearchResponse response = future.get();
        SearchHit hit = response.getHits().getAt(0);
        String json = hit.toString();
        JsonNode source = objectMapper.readTree(json).get("_source");
        AtomicInteger repoId = new AtomicInteger();
        source.get("repositories").forEach(
                r -> repoId.set(r.get("id").asInt())
        );
        return repoId.get();
    }

    private Integer getCoreIdFromMetadata(InputMetadata metadata) throws JsonProcessingException, ExecutionException, InterruptedException {
        log.info("fetching core id ...");
        AtomicInteger coreId = new AtomicInteger();
        SearchRequest searchRequest = new SearchRequest("articles");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (metadata.getDoi() != null) {
            queryBuilder.must(QueryBuilders.matchQuery("doi", metadata.getDoi()));
        }
        if (metadata.getOai() != null) {
            queryBuilder.must(QueryBuilders.matchQuery("oai", metadata.getOai()));
        }
        if (metadata.getTitle() != null && metadata.getYear() != null) {
            queryBuilder.must(QueryBuilders.matchQuery("title", metadata.getTitle()));
            queryBuilder.must(QueryBuilders.matchQuery("year", metadata.getYear()));
        }
        if (metadata.getRepoId() != null) {
            queryBuilder.must(QueryBuilders.nestedQuery(
                    "repositories",
                    QueryBuilders.matchQuery("repositories.id", metadata.getRepoId()),
                    ScoreMode.Max));
//            queryBuilder.must(QueryBuilders.matchQuery("repositories.id", metadata.getRepoId()));
        }
        searchSourceBuilder
                .query(queryBuilder)
                .sort(new ScoreSortBuilder().order(SortOrder.DESC));
        searchRequest.source(searchSourceBuilder);
        log.info("request finished, ready to search ...");
        Future<SearchResponse> future = this.client.search(searchRequest);
        do {
            log.info("waiting for the result ...");
            TimeUnit.SECONDS.sleep(1);
        } while (!future.isDone());
        SearchResponse response = future.get();
        SearchHit hit = response.getHits().getAt(0);
        String json = hit.toString();
        JsonNode source = objectMapper.readTree(json).get("_source");
        coreId.set(source
                .get("id")
                .asInt());
        return coreId.get();
    }
}
