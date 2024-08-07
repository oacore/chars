package uk.ac.core.database.service.recommendations.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.database.service.recommendations.RecommendationComplaintsDAO;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class MySQLRecommendationComplaintsDAO implements RecommendationComplaintsDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Integer> fetchBlacklistedRecommendations(String sourceUrl) {
        String sql = "SELECT core_article_id "
                + "FROM recommendations_complaints "
                + "WHERE source_url=? AND enabled=1";
        List<Integer> blackIds = jdbcTemplate.queryForList(sql, Integer.class, sourceUrl);
        return blackIds;
    }

}
