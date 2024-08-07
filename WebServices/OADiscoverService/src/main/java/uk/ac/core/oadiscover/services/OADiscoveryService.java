package uk.ac.core.oadiscover.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Component;
import uk.ac.core.common.util.FuzzyMatcher;
import uk.ac.core.elasticsearch.entities.ElasticSearchArticleMetadata;
import uk.ac.core.oadiscover.model.CrossrefArticle;
import uk.ac.core.oadiscover.model.DiscoveryDocument;
import uk.ac.core.oadiscover.model.DiscoverySource;
import uk.ac.core.oadiscover.model.License;
import uk.ac.core.oadiscover.model.Link;

/**
 *
 * @author lucasanastasiou
 */
@Component
public class OADiscoveryService {

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    HttpClient httpClient;

    private static List<String> prioritisedSources = Arrays.asList(
            "Crossref by link",
            "CORE by link",
            "Crossref by data repo link",
            "Crossref by preprints link",
            "Crossref DOAJ paper",
            "CORE by preprints link",
            "CORE by data repo link",
            "CORE DOAJ paper",
            "Preprints doi link",
            "Crossref by license",
            "From CORE"
    );

    public OADiscoveryResult queryByTitleAndYear(String title, String year, List<String> authors) {

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        MatchQueryBuilder titleQueryBuilder = QueryBuilders.matchQuery("title", title);

        boolQueryBuilder.must(titleQueryBuilder);

        if (!year.isEmpty()) {
            MatchQueryBuilder yearQueryBuilder = QueryBuilders.matchQuery("year", year);

            boolQueryBuilder.should(yearQueryBuilder);
        }
        if (authors != null && !authors.isEmpty()) {
            for (String author : authors) {

                MatchQueryBuilder authorQueryBuilder = QueryBuilders.matchQuery("authors", author);
                boolQueryBuilder.should(authorQueryBuilder);

            }
        }
        TermQueryBuilder notDeletedQueryBuilder = QueryBuilders.termQuery("deleted", "ALLOWED");
        boolQueryBuilder.must(notDeletedQueryBuilder);

        // full text : * is a should and is just to boost FT items to the top
        MatchQueryBuilder fullTextQueryBuilder = QueryBuilders.matchQuery("fullText", "*");
        boolQueryBuilder.should(fullTextQueryBuilder);

        SearchQuery searchQuery = new NativeSearchQuery(boolQueryBuilder);
        searchQuery.addIndices("articles");
        searchQuery.addTypes("article");
        searchQuery.addFields("id", "title", "authors", "year", "repositories.name", "urls", "fullTextIdentifier", "fullText", "repositoryDocument.tdmOnly");
        searchQuery.setPageable(PageRequest.of(0, 1));

        List<OADiscoveryResult> results = elasticsearchTemplate.query(searchQuery, new OADiscoverResultExtractor());

        if (results != null && !results.isEmpty()) {
            OADiscoveryResult aResult = results.get(0);

            if (validateSearchResultWithInputArticle(aResult, title, year, authors)) {
                return aResult;
            }

        }

        return null;

    }

    private boolean validateSearchResultWithInputArticle(OADiscoveryResult aResult, String title, String year, List<String> authors) {

        boolean validResult = FuzzyMatcher.fuzzyStringMatch(title, aResult.getTitle(), 0.1);;

        if (validResult) {

            String resultYear = aResult.getYear();
            if (resultYear != null && !resultYear.isEmpty()) {
                try {
                    Integer resultIntYear = Integer.parseInt(resultYear);
                    Integer inputYear = Integer.parseInt(year);
                    validResult = validResult || (Math.abs(resultIntYear - inputYear) < 2);//alow up to 1 year of difference

                } catch (NumberFormatException nfe) {

                }
            }

            if (!validResult) {
                Collections.sort(authors);
                List<String> resultAuthors = aResult.getAuthors();

                if (resultAuthors != null && !resultAuthors.isEmpty()) {
                    Collections.sort(resultAuthors);
                    int a_ctn = 0;
                    for (String rAuthor : resultAuthors) {
                        String correspondingAuthor = authors.get(a_ctn);
                        validResult = validResult || FuzzyMatcher.fuzzyStringMatch(correspondingAuthor, rAuthor, 0.1);
                        a_ctn++;
                        // check only the first three authors 
                        if (a_ctn > 2) {
                            break;
                        }
                    }

                }
            }
        }
        return validResult;

    }

    public List<String> validateCrossrefLinks(CrossrefArticle crossRefArticle) {
        List<String> validLinks = new ArrayList<String>();
        if (crossRefArticle.getLink() != null) {
            for (Link link : crossRefArticle.getLink()) {
                if (link.getContentType().contains("pdf") || link.getURL().contains("pdf")) {
                    HttpHead httpHead = new HttpHead(link.getURL());
                    httpHead.addHeader("User-Agent", "");
                    try {
                        HttpResponse response = this.httpClient.execute(httpHead);
                        Integer statusCode = response.getStatusLine().getStatusCode();
                        if (statusCode < 399) {
                            Header[] contentTypeHeaders = response.getHeaders("Content-Type");
                            boolean isPdf = false;
                            if (contentTypeHeaders != null) {
                                for (Header contentTypeHeader : contentTypeHeaders) {
                                    if (contentTypeHeader.getValue().contains("pdf")) {
                                        isPdf = true;
                                        break;
                                    }
                                }
                            }

                            if (isPdf) {
                                validLinks.add(link.getURL());
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(OADiscoveryService.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }
        }
        if (crossRefArticle.getLicense() != null) {
            for (License license : crossRefArticle.getLicense()) {
                String licenseUrl = license.getURL().toLowerCase();
                if (licenseUrl.contains("creativecommons") || licenseUrl.contains("open")) {
                    validLinks.add(crossRefArticle.getURL());
                }
            }
        }
        return validLinks;
    }

    public List<String> validateCORELinks(List<String> urls) {
        List<String> valid = new ArrayList<String>();
        boolean isOpen = false;
        String licenseUrl = null;
        for (String url : urls) {
            String urlToCompare = url.toLowerCase();
            if (urlToCompare.contains("pdf") || urlToCompare.contains("bitstream") || urlToCompare.contains("arxiv") || (urlToCompare.contains("doaj"))) {
                valid.add(url);
            }
            if (urlToCompare.contains("creativecommons")) {
                isOpen = true;
                licenseUrl = url;
            }
        }
        if (isOpen) {
            urls.remove(licenseUrl);
            valid.addAll(urls);
        }
        // thia is to prefer article level links to table of content (especially for DOAJ)
        valid.sort((o1, o2) -> {
            return (o1.contains("article")) ? -1 : 1;
        });
        return valid;
    }

    public DiscoveryDocument query(String doi) {

        GetRequestBuilder requestBuilder = this.elasticsearchTemplate.getClient().prepareGet().setIndex("discovery").setId(doi);
        GetResponse getResponse = requestBuilder.get();
        DiscoveryDocument discoveryDocument = null;
        if (getResponse.isExists()) {
            discoveryDocument = new DiscoveryDocument();
            List<HashMap> sources = (List<HashMap>) getResponse.getSourceAsMap().get("sources");
            List<DiscoverySource> discoverySources = new ArrayList<>();
            for (HashMap source : sources) {
                DiscoverySource discoverySource = new DiscoverySource();
                discoverySource.setLink((String) source.get("link"));
                discoverySource.setSource((String) source.get("source"));
                discoverySource.setValid((Boolean) source.getOrDefault("valid", null));
                discoverySources.add(discoverySource);

            }
            discoveryDocument.setSources(discoverySources);
            discoveryDocument.setDoi((String) getResponse.getSourceAsMap().get("doi"));
        }

        return discoveryDocument;
    }

    public DiscoverySource returnVerifiedLink(String doi) {
        DiscoveryDocument discoveryDocument = this.query(doi);
        DiscoverySource bestSource;
        if (discoveryDocument == null) {
            return null;
        }
        Collections.sort(discoveryDocument.getSources(), (o1, o2) -> {
            return prioritisedSources.indexOf(o1) - prioritisedSources.indexOf(o2); //To change body of generated lambdas, choose Tools | Templates.
        });
        bestSource = discoveryDocument.getSources().get(0);
        if (prioritisedSources.indexOf(bestSource.getSource()) < 2) {
            boolean isValid;
            if (discoveryDocument.getBestSourceIsValid() == null) {
                {
                    try {
                        bestSource.setValid(isAValidPdfLink(bestSource.getLink()));
                        this.indexDiscoveryDocument(discoveryDocument, bestSource);
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
        return bestSource;

    }

    public boolean isAValidPdfLink(String link) throws IOException {
        HttpHead httpHead = new HttpHead(link);
        httpHead.addHeader("User-Agent", "");
        HttpResponse response = this.httpClient.execute(httpHead);
        Integer statusCode = response.getStatusLine().getStatusCode();
        if (statusCode < 399) {
            Header[] contentTypeHeaders = response.getHeaders("Content-Type");
            if (contentTypeHeaders != null) {
                for (Header contentTypeHeader : contentTypeHeaders) {
                    if (contentTypeHeader.getValue().contains("pdf")) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    private void indexDiscoveryDocument(DiscoveryDocument discoveryDocument, DiscoverySource bestSource) {

        IndexRequest indexRequest = new IndexRequest("discovery", "documents", discoveryDocument.getDoi());
        indexRequest.source("bestSourceIsValid", bestSource.getValid());

        boolean updateQuery = new UpdateQueryBuilder()
                .withIndexName("discovery").withIndexRequest(indexRequest)
                .withType("documents").withId(discoveryDocument.getDoi()).build().DoUpsert();

        this.elasticsearchTemplate.refresh(DiscoveryDocument.class);
    }
    
    public OADiscoveryResult queryCORE(String doi, String title, String year, String authors) {
        OADiscoveryResult result = null;
        
        ElasticSearchArticleMetadata articleMetadata = null;
        
        if (title != null) {
            if (!authors.isEmpty()) {
                List<String> authorsAsList = Arrays.asList(authors.split(","));
                result = this.queryByTitleAndYear(title, year, authorsAsList);
            } else {
                result = this.queryByTitleAndYear(title, year, null);
            }
            
        }
        return result;
    }
}
