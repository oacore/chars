package uk.ac.core.elasticsearch.recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import uk.ac.core.elasticsearch.entities.ElasticSearchSimilarDocument;

/**
 * @author mc26486
 */
class ElasticSearchSimilarDocumentExtractor implements ResultsExtractor<List<ElasticSearchSimilarDocument>> {

    @Override
    public List<ElasticSearchSimilarDocument> extract(SearchResponse response) {
        List<ElasticSearchSimilarDocument> results = new ArrayList<>();

        for (SearchHit hit : response.getHits()) {
            ElasticSearchSimilarDocument elasticSearchSimilarDocument = new ElasticSearchSimilarDocument();
            if (hit != null) {

                Map<String, Object> hitMap = hit.getSourceAsMap();

                if (hitMap.get("id") != null) {
                    elasticSearchSimilarDocument.setId(hitMap.get("id").toString());
                }
                if (hitMap.get("title") != null) {
                    elasticSearchSimilarDocument.setTitle(hitMap.get("title").toString());
                }
                if (hitMap.get("repositories") != null) {
                    ArrayList<HashMap<String, String>> repo = (ArrayList<HashMap<String, String>>) hitMap.get("repositories");
                    String repoName = repo.get(0).get("name");
                    String repoId = repo.get(0).get("id");
                    elasticSearchSimilarDocument.setRepositoryName(repoName);
                    elasticSearchSimilarDocument.setRepositoryId(repoId);
                }
                if (hitMap.get("publisher") != null) {
                    if (hitMap.get("publisher").toString().contains("{")) {
                        HashMap<String, String> publisherMap = (HashMap<String, String>) hitMap.get("publisher");
                        elasticSearchSimilarDocument.setPublisher(publisherMap.get("name"));
                    } else {
                        String publisher = hitMap.get("publisher").toString();
                        elasticSearchSimilarDocument.setPublisher(publisher);
                    }
                }

                if (hitMap.get("authors") != null) {
                    List<Object> authrosObjects = (List<Object>) hitMap.get("authors");
                    if (authrosObjects != null && !authrosObjects.isEmpty()) {
                        List<String> authors = (List<String>) (List<?>) authrosObjects;
                        elasticSearchSimilarDocument.setAuthors(authors);
                    }
                }

                if (hitMap.get("urls") != null) {
                    List<Object> urlsObject = (List<Object>) hitMap.get("urls");
                    if (urlsObject != null && !urlsObject.isEmpty()) {
                        List<String> urls = (List<String>) (List<?>) urlsObject;
                        elasticSearchSimilarDocument.setUrls(urls);
                    }
                }

                if (hitMap.get("year") != null) {
                    Integer year = (Integer) hitMap.get("year");
                    elasticSearchSimilarDocument.setYear("" + year);
                }
                if (hitMap.get("language") != null) {
                    HashMap<String, String> lang = (HashMap<String, String>) hitMap.get("language");
                    String langCode = lang.get("code").toString();
                    elasticSearchSimilarDocument.setLanguage(langCode);
                }
                if (hitMap.get("downloadUrl") != null) {
                    String downloadUrl = hitMap.get("downloadUrl").toString();
                    elasticSearchSimilarDocument.setDownloadUrl(downloadUrl);
                }


                // will be calculated later
                elasticSearchSimilarDocument.setSimhash(null);

                elasticSearchSimilarDocument.setScore(new Double(hit.getScore()));
                results.add(elasticSearchSimilarDocument);
            }
        }
        return results;
    }
}
