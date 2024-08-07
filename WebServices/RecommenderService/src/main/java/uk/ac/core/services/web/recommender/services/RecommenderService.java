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

/**
 * @author mc26486
 */
@Component
public class RecommenderService {

    @Autowired
    RecommendationComplaintsDAO recommendationComplaintsDAO;

    @Autowired
    ElasticSearchRecommendationService elasticSearchRecommendationService;

    @Autowired
    DuplicateService duplicateService;

    @Autowired
    PostfilteringService postfilteringService;

    @Autowired
    CTRService cTRService;

    public RecommenderServiceResponse recommend(RecommenderServiceRequest recommenderServiceRequest) {

        final Boolean useYearDecayScoring = Boolean.TRUE;
        final Boolean useFullTextFilter = Boolean.TRUE;
        final Integer year_decay_scale = 20;

        JsonObject algorithmParameters = new JsonObject();

        algorithmParameters.addProperty("useFullTextFilter", useFullTextFilter);
        algorithmParameters.addProperty("useYearDecayScoring", useYearDecayScoring);
        algorithmParameters.addProperty("year_decay_scale", year_decay_scale);
        algorithmParameters.addProperty("min_doc_freq", 2);

        List<Integer> blacklistIds = this.recommendationComplaintsDAO.fetchBlacklistedRecommendations(recommenderServiceRequest.getReferer());

        List<ElasticSearchSimilarDocument> results;

        if (recommenderServiceRequest.getTargetArticleId() == null ) {
            results = this.elasticSearchRecommendationService.moreLikeThis(recommenderServiceRequest.getTitle(),
                    recommenderServiceRequest.getAabstract(),
                    blacklistIds,
                    recommenderServiceRequest.getRepositoryId(),
                    algorithmParameters, recommenderServiceRequest.getResultType());
        } else {
            results = this.elasticSearchRecommendationService.moreLikeThisWithArticleID(
                    recommenderServiceRequest.getTargetArticleId(),
                    blacklistIds,
                    recommenderServiceRequest.getRepositoryId(),
                    algorithmParameters, recommenderServiceRequest.getResultType());
        }

        String similarToDocId = "";
        String similarToKey = "";

        if (recommenderServiceRequest.getTargetArticleId() != null) {
            similarToDocId = String.valueOf(recommenderServiceRequest.getTargetArticleId());
            similarToKey = "CORE";
        } else if (recommenderServiceRequest.getOai() != null && !recommenderServiceRequest.getOai().isEmpty()) {
            similarToDocId = recommenderServiceRequest.getOai();
            similarToKey = "OAI";
        } else if (recommenderServiceRequest.getUrl() != null && !recommenderServiceRequest.getUrl().isEmpty()) {
            similarToDocId = recommenderServiceRequest.getUrl();
            similarToKey = "COREURL";
        } else {
            similarToDocId = recommenderServiceRequest.getReferer();
            similarToKey = "URL";

            try {
                // Currently, the front-end will not send the CORE ID of the requested display article. Attempt
                // to parse the URL to retrieve it here if supplied.
                // Making this better would be to somehow catch the ID generated at the ArticleController level in
                // the front-end project and send it to the recommender.
                URL aURL = new URL(similarToDocId);

                if (aURL.getHost().contains("core")) {
                    String[] coreURLPath = aURL.getPath().split("/");

                    if (coreURLPath.length >= 3) {
                        if (isOnlyNumber(coreURLPath[2])) {
                            similarToDocId = coreURLPath[2];
                            similarToKey = "CORE";
                        }
                    }
                }
            } catch (MalformedURLException ex) {
                // ex.printStackTrace();
            }
        }

        String recommendation_type = "same_repo";
        if (recommenderServiceRequest.getRepositoryId() == null) {
            recommendation_type = "general";
        }



        long t2 = System.currentTimeMillis();

        List<ElasticSearchSimilarDocument> cleanResults = this.duplicateService.cleanResults(recommenderServiceRequest.getTargetArticle(), results);
        long t3 = System.currentTimeMillis();

        List<ElasticSearchSimilarDocument> postFilteredResults = this.postfilteringService.postFilter(cleanResults, recommenderServiceRequest.getSize());
        long t4 = System.currentTimeMillis();
        List<ElasticSearchSimilarDocument> ctrResults = this.cTRService.generateUrls(CTRSource.fromRecType(recommenderServiceRequest.getRecType()),
                similarToDocId,
                similarToKey,
                postFilteredResults,
                recommenderServiceRequest.getAlgorithm(),
                algorithmParameters,
                recommendation_type,
                recommenderServiceRequest.getRepositoryId(), recommenderServiceRequest.getIdRecommender(), recommenderServiceRequest.getResultType());
        long t5 = System.currentTimeMillis();
        RecommenderServiceResponse recommenderServiceResponse = new RecommenderServiceResponse();
        recommenderServiceResponse.setDocuments(ctrResults);

        return recommenderServiceResponse;
    }

    public boolean isBlank(String value) {
        return (value == null || value.equals("") || value.equals("null") || value.trim().equals(""));
    }

    public boolean isOnlyNumber(String value) {
        boolean ret = false;

        if (!isBlank(value)) {
            ret = value.matches("^[0-9]+$");
        }

        return ret;
    }
}
