package uk.ac.core.services.web.recommender.services;

import uk.ac.core.services.web.recommender.services.ctr.CTRService;
import com.google.gson.JsonObject;
import java.util.Calendar;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.core.database.service.recommendations.RecommendationComplaintsDAO;
import uk.ac.core.elasticsearch.entities.ElasticSearchSimilarDocument;
import uk.ac.core.elasticsearch.recommendation.ElasticSearchRecommendationService;
import uk.ac.core.services.web.recommender.model.RecommenderServiceRequest;
import uk.ac.core.services.web.recommender.model.RecommenderServiceResponse;
import uk.ac.core.services.web.recommender.services.ctr.CTRSource;
import java.net.*;
import java.util.ArrayList;

/**
 *
 * @author Samuel Pearce <samuel.pearce@open.ac.uk>
 */
@Component
public class FreeTextRecommenderService {

    @Autowired
    ElasticSearchRecommendationService elasticSearchRecommendationService;

    @Autowired
    PostfilteringService postfilteringService;

    public RecommenderServiceResponse recommend(String freeText) {

        final Boolean useYearDecayScoring = Boolean.TRUE;
        final Boolean useFullTextFilter = Boolean.TRUE;
        final Integer year_decay_scale = 20;

        JsonObject algorithmParameters = new JsonObject();

        algorithmParameters.addProperty("useFullTextFilter", useFullTextFilter);
        algorithmParameters.addProperty("useYearDecayScoring", useYearDecayScoring);
        algorithmParameters.addProperty("year_decay_scale", year_decay_scale);
        algorithmParameters.addProperty("min_doc_freq", 2);

        List<ElasticSearchSimilarDocument> results = this.elasticSearchRecommendationService.moreLikeThis(freeText, 
                new ArrayList<Integer>(), 
                null,
                algorithmParameters, "output");
                
        List<ElasticSearchSimilarDocument> postFilteredResults = this.postfilteringService.postFilter(results, 10);
       
		RecommenderServiceResponse recommenderServiceResponse = new RecommenderServiceResponse();
        recommenderServiceResponse.setDocuments(postFilteredResults);

        return recommenderServiceResponse;
    }
}
