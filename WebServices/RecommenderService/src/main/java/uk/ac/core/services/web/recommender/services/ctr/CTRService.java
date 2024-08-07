/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.services.web.recommender.services.ctr;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.core.database.service.ctr.CTRDAO;
import uk.ac.core.elasticsearch.entities.ElasticSearchSimilarDocument;
import uk.ac.core.elasticsearch.recommendation.ElasticSearchRecommendationService;

import java.util.UUID;

/**
 * @author mc26486
 */
@Component
public class CTRService {

    @Autowired
    CTRDAO ctrdao;

    public List<ElasticSearchSimilarDocument> generateUrls(CTRSource source, String similarToId,
                                                           String similarToKey, List<ElasticSearchSimilarDocument> results, String algorithmName,
                                                           JsonObject algorithmParams, String recommendation_type, String repositoryId,
                                                           String idRecommender, String resultType) {
        int sourceId = source.getValue();
        int algorithmId = ctrdao.getAlgorithmID(algorithmName, algorithmParams);

        String resultsIDs = "";

        UUID recSetID = UUID.randomUUID();
        int pos = 1;

        for (ElasticSearchSimilarDocument result : results) {
            if (repositoryId.equals("144") || idRecommender.equals("24c597")) {
                result.setUrl(generateSelfCTRUrl(result, similarToId, similarToKey, algorithmId, sourceId, recSetID, pos, recommendation_type, resultType));
                resultsIDs += result.getId() + ",";
                pos++;
            } else {
                result.setUrl(generateCTRUrl(similarToId, similarToKey, result.getId(), algorithmId, sourceId, recSetID, pos, recommendation_type, resultType));
                resultsIDs += result.getId() + ",";
                pos++;
            }
        }

        resultsIDs = resultsIDs.replaceAll(",$", "");

        for (ElasticSearchSimilarDocument result : results) {
            result.setUrl(result.getUrl() + "&otherRecs=" + resultsIDs);
        }

        return results;

    }

    private String generateCTRUrl(String similarToId, String similarToKey, String id, int algorithmId, int sourceId, UUID recSetID, int pos, String recommendation_type, String resultType) {
        if (resultType.equals(ElasticSearchRecommendationService.RESULT_TYPE_OUTPUT)) {
            return "https://core.ac.uk/display/" + id
                    + "?source=" + sourceId + "&algorithmId="
                    + algorithmId + "&similarToDoc="
                    + similarToId + "&similarToDocKey="
                    + similarToKey + "&recSetID="
                    + String.valueOf(recSetID) + "&position="
                    + pos + "&recommendation_type="
                    + recommendation_type;
        } else {
            return "https://core.ac.uk/works/" + id
                    + "?source=" + sourceId + "&algorithmId="
                    + algorithmId + "&similarToDoc="
                    + similarToId + "&similarToDocKey="
                    + similarToKey + "&recSetID="
                    + String.valueOf(recSetID) + "&position="
                    + pos + "&recommendation_type="
                    + recommendation_type;
        }
    }

    private String generateSelfCTRUrl(ElasticSearchSimilarDocument result, String similarToId, String similarToKey, int algorithmId, int sourceId, UUID recSetID, int pos, String recommendation_type, String resultType) {
        if (result.getRepositoryId().equals("144")) {
            return result.getUrls().get(0);
        }
        return this.generateCTRUrl(similarToId, similarToKey, result.getId(), algorithmId, sourceId, recSetID, pos, recommendation_type, resultType);
    }

}
