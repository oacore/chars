package uk.ac.core.documentdownload.entities.dao.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.documentdownload.entities.CrawlingHeuristicConversionRule;
import uk.ac.core.documentdownload.entities.dao.ConversionRulesDAO;

/**
 *
 * @author mc26486
 */
@Service
public class MySQLConversionRulesDAO implements ConversionRulesDAO{
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public List<CrawlingHeuristicConversionRule> loadConversionRules(Integer repositoryId) {
        String query = "SELECT * FROM repository_conversion_rules WHERE id_repository=?";
        return this.jdbcTemplate.query(query, new Object[]{repositoryId},new CrawlingHeuristicConversionRuleMapper());
    }
    
    
}
