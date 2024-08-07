package uk.ac.core.documentdownload.entities.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.documentdownload.entities.CrawlingHeuristicConversionRule;

/**
 *
 * @author mc26486
 */
public class CrawlingHeuristicConversionRuleMapper implements RowMapper<CrawlingHeuristicConversionRule> {

    @Override
    public CrawlingHeuristicConversionRule mapRow(ResultSet rs, int rowNum) throws SQLException {
        CrawlingHeuristicConversionRule rule = new CrawlingHeuristicConversionRule(rs.getString("input_template"), rs.getString("output_template"));
        return rule;
    }

}
