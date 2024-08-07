package uk.ac.core.oadiscover.services.calls;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.elasticsearch.entities.ElasticSearchArticleMetadata;
import uk.ac.core.elasticsearch.repositories.ArticleMetadataRepository;
import uk.ac.core.oadiscover.model.DiscoveryDocument;
import uk.ac.core.oadiscover.model.DiscoverySource;
import uk.ac.core.oadiscover.model.MuccDocumentUrl;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author lucas
 */
public class CoreCall implements Callable<DiscoverySource> {

    private ElasticsearchTemplate elasticsearchTemplate;
    private HttpClient httpClient;
    private JdbcTemplate jdbcTemplate;
    private String doi;
    private static final Logger LOG = Logger.getLogger(CoreCall.class.getName());

    private ArticleMetadataRepository elasticsearchArticleMetadataRepository;

    private static List<String> prioritisedSources = Arrays.asList(
            "Crossref by link",
            "CORE by link",
            "From Unpaywall",
            "Crossref by data repo link",
            "Crossref by preprints link",
            "Crossref DOAJ paper",
            "CORE by preprints link",
            "CORE by data repo link",
            "CORE DOAJ paper",
            "Preprints doi link",
            "Crossref by license",
            "From CORE",
            "From CORE (MUCC)"
    );

    public CoreCall(ElasticsearchTemplate elasticsearchTemplate, ArticleMetadataRepository elasticSearchArticleMetadata, HttpClient httpClient, JdbcTemplate jdbcTemplate, String doi) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.doi = doi;
        this.httpClient = httpClient;
        this.elasticsearchArticleMetadataRepository = elasticSearchArticleMetadata;
    }

    private static int preferCoreArticles(ElasticSearchArticleMetadata compareFrom, ElasticSearchArticleMetadata compareTo) {
        if (compareFrom.getDownloadUrl().contains("core.ac.uk") && !compareTo.getDownloadUrl().contains("core.ac.uk")) {
            return -1;
        } else if (!compareFrom.getDownloadUrl().contains("core.ac.uk") && compareTo.getDownloadUrl().contains("core.ac.uk")) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public DiscoverySource call() throws Exception {
        LOG.log(Level.INFO, "CORE: looking for oa links in CORE for doi : {0}", doi);
        long funcStart = System.currentTimeMillis();
        DiscoveryDocument discoveryDocument = null;
        DiscoverySource bestSource;
        List<ElasticSearchArticleMetadata> articles = elasticsearchArticleMetadataRepository.findByDoi(escapeUris(doi));
        Optional<ElasticSearchArticleMetadata> maybeArticle = articles.stream().filter(elasticSearchArticleMetadata ->
                !elasticSearchArticleMetadata.getDownloadUrl().isEmpty()).sorted(CoreCall::preferCoreArticles).findFirst();

        if (maybeArticle.isPresent()) {
            ElasticSearchArticleMetadata article = maybeArticle.get();
            discoveryDocument = new DiscoveryDocument();
            discoveryDocument.setDoi(doi);
            discoveryDocument.setId(article.getId());
            List<DiscoverySource> sources = new ArrayList<>();
            DiscoverySource source = new DiscoverySource();
            source.setLink(article.getDownloadUrl());
            source.setSource("From CORE");
            source.setValid(true);
            sources.add(source);
            discoveryDocument.setSources(sources);
            discoveryDocument.setBestSourceIsValid(true);
        } else {
            discoveryDocument = this.queryES(doi);
        }
        if (discoveryDocument == null) {

            long funcEnd = System.currentTimeMillis();
            long duration = funcEnd - funcStart;
            LOG.log(Level.INFO, "CORE: returning null after {0} ms", duration);
            return null;
        }
        // Core discovery can be from various sources - prefer those that we are sure that are valid
        Collections.sort(discoveryDocument.getSources(), Comparator.comparingInt(o -> prioritisedSources.indexOf(o)));
        bestSource = discoveryDocument.getSources().get(0);
        // if not in the top 2, i.e. NOT from CORE or from Crossref by license 
        if (prioritisedSources.indexOf(bestSource.getSource()) < 2) {
            boolean isValid;
            if (discoveryDocument.getBestSourceIsValid() == null ||
                    (discoveryDocument.getBestSourceIsValid() != null && !bestSource.getValid().equals(discoveryDocument.getBestSourceIsValid()))) {
                {
                    try {
                        //validate link and update index for future reference
                        bestSource.setValid(isAValidPdfLink(bestSource.getLink()));
                        discoveryDocument.setBestSourceIsValid(bestSource.getValid());
                        if (bestSource.getValid().equals(Boolean.FALSE)) {
                            bestSource = null;
                        }
                    } catch (IOException ex) {
                        bestSource = null;
                    }

                }
            } else if (discoveryDocument.getBestSourceIsValid() != null && !discoveryDocument.getBestSourceIsValid()) {
                bestSource = null;
            }
        }
        long funcEnd = System.currentTimeMillis();
        long duration = funcEnd - funcStart;
        LOG.log(Level.INFO, "CORE: returning {0} after {1} ms", new Object[]{bestSource, duration});
        return bestSource;
    }

    private String escapeUris(String url) {
        String escapedUrl = url.replaceAll("\\:", "\\\\:").replaceAll("\\/", "\\\\/");
        return escapedUrl;
    }

    private DiscoveryDocument queryES(String doi) {

        System.out.println("Querying ES with doi = " + doi);

        TermQueryBuilder query = new TermQueryBuilder("doi", doi);

        SearchQuery searchQuery = new NativeSearchQuery(query);
        searchQuery.addIndices("discovery");

        List<DiscoveryDocument> discoveryDocuments = this.elasticsearchTemplate.queryForList(searchQuery, DiscoveryDocument.class);
        DiscoveryDocument discoveryDocument = null;
        if (discoveryDocuments != null && !discoveryDocuments.isEmpty()) {
            discoveryDocument = discoveryDocuments.get(0);
        }

        return discoveryDocument;

    }

    private boolean isAValidPdfLink(String link) throws IOException {
        HttpHead httpHead = new HttpHead(link);
        httpHead.addHeader("User-Agent", "");
        HttpResponse response = this.httpClient.execute(httpHead);
        Integer statusCode = response.getStatusLine().getStatusCode();
        LOG.info("Validating: " + link + " return code: " + statusCode);
        if (statusCode < 399) {
            Header[] contentTypeHeaders = response.getHeaders("Content-Type");
            if (contentTypeHeaders != null) {
                for (Header contentTypeHeader : contentTypeHeaders) {
                    LOG.log(Level.INFO, "contentTypeHeader = {0}", contentTypeHeader);
                    if (contentTypeHeader.getValue().contains("pdf")) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

}
