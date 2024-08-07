package uk.ac.core.documentdownload.entities.dao;

import java.util.List;
import uk.ac.core.documentdownload.entities.CrawlingHeuristicConversionRule;

/**
 *
 * @author mc26486
 */
public interface ConversionRulesDAO {

    public List<CrawlingHeuristicConversionRule> loadConversionRules(Integer repositoryId);
    
}
