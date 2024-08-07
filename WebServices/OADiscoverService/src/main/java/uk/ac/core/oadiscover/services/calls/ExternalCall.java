package uk.ac.core.oadiscover.services.calls;

import java.util.List;
import java.util.concurrent.Callable;
import org.apache.http.client.HttpClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.core.oadiscover.model.CachedDiscoveryDocument;
import uk.ac.core.oadiscover.model.DiscoveryProvider;
import uk.ac.core.oadiscover.model.DiscoverySource;

/**
 *
 * @author lucas
 */
abstract class ExternalCall implements Callable<DiscoverySource> {

    protected HttpClient httpClient;

    protected String doi;
    protected long artificialDelay = 0;
    protected DiscoveryProvider discoveryProvider;
    protected ElasticsearchTemplate elasticSearchTemplate;

    JdbcTemplate jdbcTemplate;

    public ExternalCall(DiscoveryProvider discoveryProvider, HttpClient httpClient, JdbcTemplate jdbcTemplate, ElasticsearchTemplate elasticSearchTemplate, String doi, long artificialDelay) {
        this.discoveryProvider = discoveryProvider;
        this.httpClient = httpClient;
        this.doi = doi;
        this.artificialDelay = artificialDelay;
        this.jdbcTemplate = jdbcTemplate;
        this.elasticSearchTemplate = elasticSearchTemplate;
    }

    public ExternalCall(DiscoveryProvider discoveryProvider, HttpClient httpClient, JdbcTemplate jdbcTemplate, ElasticsearchTemplate elasticSearchTemplate, String doi) {
        this.discoveryProvider = discoveryProvider;
        this.httpClient = httpClient;
        this.doi = doi;
        this.jdbcTemplate = jdbcTemplate;
        this.elasticSearchTemplate = elasticSearchTemplate;
    }

    @Override
    public DiscoverySource call() throws Exception {

        DiscoverySource discoverySource = this.fetchFromCache();

        if (discoverySource == null) {
            long t0 = System.currentTimeMillis();
            discoverySource = this.executeExternalCall();
            long t1 = System.currentTimeMillis();

            this.storeCallToDB(doi, discoverySource, (t1 - t0));

            if (discoverySource != null) {
                this.storeToCache(discoverySource);
            }
        }else {
            System.out.println("Found in external cache for doi:"+doi+" for source:"+discoveryProvider.name());
        }

        return discoverySource;
    }

    abstract DiscoverySource executeExternalCall() throws Exception;

    private void storeCallToDB(String doi, DiscoverySource discoverySource, long duration) {

        String SQL = "INSERT INTO discovery_external_calls(`doi` , `external_source` , `external_link` , `duration` , `finish_time`) "
                + "VALUES(?, ?, ? , ?, NOW())";

        String link = discoverySource != null ? discoverySource.getLink() : null;

        jdbcTemplate.update(SQL, doi, this.discoveryProvider.name(), link, duration);
    }

    private DiscoverySource fetchFromCache() {
        QueryBuilder doiQueryPart = QueryBuilders.termQuery("doi", this.doi);
        QueryBuilder sourceQueryPart = QueryBuilders.termQuery("source", this.discoveryProvider.verbose());

        QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(doiQueryPart).must(sourceQueryPart);

        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();

        List<CachedDiscoveryDocument> cachedDiscoveryDocuments = elasticSearchTemplate.queryForList(build, CachedDiscoveryDocument.class);
        if (cachedDiscoveryDocuments != null && cachedDiscoveryDocuments.size() > 0) {
            return cachedDiscoveryDocuments.get(0).convertToDiscoverySource();
        }
        return null;
    }

    private void storeToCache(DiscoverySource discoverySource) {

        CachedDiscoveryDocument cachedDiscoveryDocument = CachedDiscoveryDocument.buildFromDiscoverySource(this.doi, discoverySource);

        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setIndexName("caching-discovery");
        indexQuery.setType("_doc");
        indexQuery.setObject(cachedDiscoveryDocument);
        elasticSearchTemplate.index(indexQuery);
    }

}
