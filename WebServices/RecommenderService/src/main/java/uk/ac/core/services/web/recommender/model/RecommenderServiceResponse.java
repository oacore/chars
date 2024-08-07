package uk.ac.core.services.web.recommender.model;

import java.util.List;
import uk.ac.core.elasticsearch.entities.ElasticSearchSimilarDocument;

/**
 *
 * @author mc26486
 */
public class RecommenderServiceResponse {

    private List<ElasticSearchSimilarDocument> documents;

    public List<ElasticSearchSimilarDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<ElasticSearchSimilarDocument> documents) {
        this.documents = documents;
    }

}
