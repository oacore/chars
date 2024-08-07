package uk.ac.core.documentdownload.downloader.crawling;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.core.common.model.article.PDFUrlSource;
import uk.ac.core.common.model.legacy.RepositoryDocumentBase;
import uk.ac.core.documentdownload.entities.dao.ConversionRulesDAO;
import uk.ac.core.documentdownload.entities.CrawlingHeuristicConversionRule;

/**
 *
 * @author lucasanastasiou
 */
public class CrawlingHeuristic {

    //multiton
    private static final Map<Integer, CrawlingHeuristic> instances = new ConcurrentHashMap<>();

    @Autowired
    private ConversionRulesDAO repositoryConversionRuleDAO;
    private final List<CrawlingHeuristicConversionRule> conversionRules;

    public CrawlingHeuristic(Integer repositoryId) {
        this.conversionRules = repositoryConversionRuleDAO.loadConversionRules(repositoryId);
    }

    public static synchronized CrawlingHeuristic getInstance(Integer repositoryId) {
        CrawlingHeuristic instance = instances.get(repositoryId);
        if (instance == null) {
            instance = new CrawlingHeuristic(repositoryId);
            instances.put(repositoryId, instance);
        }
        return instance;
    }

    public List<CrawlingHeuristicConversionRule> getConversionRules() {
        return conversionRules;
    }

}
