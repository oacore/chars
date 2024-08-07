package uk.ac.core.database.service.recommendations;

import java.util.List;

/**
 *
 * @author lucasanastasiou
 */
public interface RecommendationComplaintsDAO {
/**
     * Return core ids that have been blacklisted for this external source
     * @param sourceUrl
     * @return 
     */
    public List<Integer> fetchBlacklistedRecommendations(String sourceUrl);
}
