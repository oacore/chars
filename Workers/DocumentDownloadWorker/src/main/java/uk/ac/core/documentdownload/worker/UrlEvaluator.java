package uk.ac.core.documentdownload.worker;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import uk.ac.core.common.model.legacy.HarvestLevel;
import uk.ac.core.common.model.legacy.RepositoryHarvestProperties;
import uk.ac.core.documentdownload.downloader.crawling.BlackListFilter;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrl;

/**
 *
 * @author mc26486
 */
public class UrlEvaluator {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(UrlEvaluator.class);

    private final BlackListFilter blackListEvaluator;
    private final RepositoryHarvestProperties repositoryHarvestProperties;
    private final LinksToPdfFilter linksToPdfFilter;

    public UrlEvaluator(RepositoryHarvestProperties repositoryHarvestProperties) {
        this.repositoryHarvestProperties = repositoryHarvestProperties;
        this.linksToPdfFilter = new LinksToPdfFilter();
        this.blackListEvaluator = new BlackListFilter(repositoryHarvestProperties.getBlackList());
    }

    List<CrawlingUrl> applyFilters(List<CrawlingUrl> urlsFromPage) {
        List<CrawlingUrl> listOfUrlsToCrawl = new ArrayList<>();
        for (CrawlingUrl url : urlsFromPage) {
            // If we have reached the harvest level, then we should applyFilters each url and determine 
            // if we should download any URLs on that page. For example, it might only be Urls that
            // end in .pdf
            // If we have not reached the harvest level, then try all links and do not filter.
            // This method should not be called in we are above the harvest level.
            //TODO @Sam write it in a decorator pattern way and show me that is not unreadable
            if (blackListEvaluator.canDownloadUrl(url.getCurrentUrl())) {
                if (url.getCurrentHarvestLevel() == 0) {
                    // If the repository is set to only harvest level 0 then we want to filter the input url                    
                    if (this.repositoryHarvestProperties.getHarvestLevel().equals(HarvestLevel.LEVEL_0)) {
                        logger.debug("At maximum harvest level, filter URL " + url.getCurrentUrl());
                        if (linksToPdfFilter.canDownloadUrl(url)) {
                            listOfUrlsToCrawl.add(url);
                        }
                    } else {
                        //sort the urls from the db to priorities the most likely
                        if (linksToPdfFilter.canDownloadUrl(url)) {
                            logger.debug("Can attempt to download URL with priority " + url.getCurrentUrl());
                            listOfUrlsToCrawl.add(0, url);
                        } else {
                            logger.debug("Can attempt to download URL " + url.getCurrentUrl());
                            listOfUrlsToCrawl.add(url);
                        }
                    }
                } else {
                    if (linksToPdfFilter.canDownloadUrl(url)) {
                        logger.debug("Harvest level not 0 + can download url " + url.getCurrentUrl());
                        listOfUrlsToCrawl.add(url);
                    }
                }
            }
        }
        return listOfUrlsToCrawl;
    }

}
