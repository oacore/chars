package uk.ac.core.documentdownload.downloader.crawling;

/**
 *
 * @author Drahomira Herrmannova <d.herrmannova@gmail.com>
 */
public class CrawlingUrl {

    private String originalUrl;
    private String currentUrl;
    private Integer currentHarvestLevel = 0;

    public CrawlingUrl(String originalUrl, String currentUrl, Integer currentHarvestLevel) {
        this.originalUrl = originalUrl;
        this.currentUrl = currentUrl;
        this.currentHarvestLevel = currentHarvestLevel;
    }

    public CrawlingUrl(String currentUrl) {
        this.currentUrl = currentUrl;
        this.currentHarvestLevel = 0;
    }

    @Override
    public String toString() {
        return "CrawlingUrl{" + "originalUrl=" + originalUrl + ", currentUrl=" + currentUrl + '}';
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    public Integer getCurrentHarvestLevel() {
        return currentHarvestLevel;
    }

    public void setCurrentHarvestLevel(Integer currentHarvestLevel) {
        this.currentHarvestLevel = currentHarvestLevel;
    }

}
