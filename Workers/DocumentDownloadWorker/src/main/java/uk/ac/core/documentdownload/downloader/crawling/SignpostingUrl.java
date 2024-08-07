package uk.ac.core.documentdownload.downloader.crawling;

/**
 *
 * @author mc26486
 */
public class SignpostingUrl extends CrawlingUrl {

    private String relType;
    private String applicationType;

    public SignpostingUrl(String originalUrl, String currentUrl, Integer currentHarvestLevel) {
        super(originalUrl, currentUrl, currentHarvestLevel);
    }

    public String getRelType() {
        return relType;
    }

    public void setRelType(String relType) {
        this.relType = relType;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    @Override
    public String toString() {
        return super.toString() + "relType=" + relType + ", applicationType=" + applicationType + '}';
    }

}
