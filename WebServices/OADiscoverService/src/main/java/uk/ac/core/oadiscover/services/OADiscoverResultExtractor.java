package uk.ac.core.oadiscover.services;

import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.search.SearchHit;
import org.springframework.data.elasticsearch.core.ResultsExtractor;

/**
 *
 * @author lucasanastasiou
 */
public class OADiscoverResultExtractor implements ResultsExtractor<List<OADiscoveryResult>>{

    @Override
    public List<OADiscoveryResult> extract(SearchResponse searchResponse) {
        List<OADiscoveryResult> results = new ArrayList<>();

        //"id", "title", "authors", "year", "urls","fullTextIdentifier"
        for (SearchHit hit : searchResponse.getHits()) {
            OADiscoveryResult oADiscoveryResult = new OADiscoveryResult();
            if (hit != null) {
                if (hit.getFields().get("id") != null) {
                    oADiscoveryResult.setId((String) hit.getFields().get("id").getValue().toString());
                }
                if (hit.getFields().get("title") != null) {
                    oADiscoveryResult.setTitle((String) hit.getFields().get("title").getValue());
                }
                DocumentField authorsHitField = hit.getFields().get("authors");
                if (authorsHitField != null) {
                    List<Object> authrosObjects = authorsHitField.getValues();
                    if (authrosObjects != null && !authrosObjects.isEmpty()) {
                        List<String> authors = (List<String>) (List<?>) authrosObjects;
                        oADiscoveryResult.setAuthors(authors);
                    }
                }
                
                
                DocumentField urlsHitField = hit.getFields().get("urls");
                if (urlsHitField != null) {
                    List<Object> urlsObjects = urlsHitField.getValues();
                    if (urlsObjects != null && !urlsObjects.isEmpty()) {
                        List<String> urls = (List<String>) (List<?>) urlsObjects;
                        oADiscoveryResult.setUrls(urls);
                    }
                }
                
                DocumentField yearHitField = hit.getFields().get("year");
                if (yearHitField != null) {
                    Integer year = (Integer) yearHitField.getValue();
                    oADiscoveryResult.setYear("" + year);
                } else {
                }

                DocumentField ftHitField = hit.getFields().get("fullText");
                DocumentField tdmHitField=hit.getFields().get("repositoryDocument.tdmOnly");
                if (ftHitField != null && ((tdmHitField!=null && tdmHitField.getValue().equals(Boolean.FALSE)) || tdmHitField==null)) {
                    oADiscoveryResult.setHasFullText(true);
                } else {
                    oADiscoveryResult.setHasFullText(false);
                }
                

                oADiscoveryResult.setScore(new Double(hit.getScore()));
                results.add(oADiscoveryResult);
            }
        }
        
        return results;

    }
    
    
}
