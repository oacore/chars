package uk.ac.core.documentdownload.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.article.PDFUrlSource;
import uk.ac.core.common.model.legacy.RepositoryDocumentBase;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrl;
import uk.ac.core.documentdownload.entities.dao.ConversionRulesDAO;

/**
 *
 * @author mc26486
 */
@Service
public class CrawlingHeuristicService {

    @Autowired
    ConversionRulesDAO conversionRulesDAO;
    private List<CrawlingHeuristicConversionRule> conversionRules;

    public void loadHeuristic(Integer repositoryId) {
        conversionRules = this.conversionRulesDAO.loadConversionRules(repositoryId);
    }

    public List<CrawlingUrl> predict(RepositoryDocumentBase repositoryDocumentBase) {
        List<CrawlingUrl> predictedUrls = new ArrayList<>();
        HashMap<String, PDFUrlSource> urls = repositoryDocumentBase.getUrls();

        conversionRules.forEach((conversionRule) -> {
            urls.forEach((url, source) -> {
                if (source.equals(PDFUrlSource.OAIPMH)) {
                    String predicted = conversionRule.predict(url);
                    if (conversionRule.predict(url) != null) {
                        predictedUrls.add(new CrawlingUrl(predicted));
                    }
                }
            });
        });
        return predictedUrls;
    }

}
