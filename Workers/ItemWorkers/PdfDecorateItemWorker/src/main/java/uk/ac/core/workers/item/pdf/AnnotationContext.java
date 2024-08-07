package uk.ac.core.workers.item.pdf;

import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.dataprovider.logic.dto.DataProviderBO;

public class AnnotationContext {
    public final static String UTM_CAMPAIGN_NAME = "pdf-decoration-v1";
    private final static String UTM_MARKS = "utm_source=pdf&utm_medium=banner&utm_campaign=" + UTM_CAMPAIGN_NAME;

    private final static String productName = "CORE";
    private final static String productWebsite = "core.ac.uk";
    private final static String linkCaption = "View metadata, citation and similar papers at ";
    private final static String logoPrefix = "brought to you by ";
    private final static String dataProviderPrefix = "provided by ";

    private String url = "";
    private String creditString = "";
    private String dataProviderName = "";

    public String getUrl() {
        return url;
    }

    public String getCreditString() {
        return creditString;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductWebsite() {
        return productWebsite;
    }

    public String getLogoPrefix() {
        return logoPrefix;
    }

    public String getDataProviderPrefix() {
        return dataProviderPrefix;
    }

    public String getDataProviderName() {
        return dataProviderName;
    }

    public String getLinkCaption() {
        return linkCaption + productWebsite;
    }

    private static String withTracking(String url) {
        return String.format("%s?%s", url, UTM_MARKS);
    }

    public AnnotationContext(RepositoryDocument repositoryDocument, DataProviderBO dataProvider) {
        final String dataProviderName = dataProvider.getName();
        final String documentId = repositoryDocument.getIdDocument().toString();

        this.url = withTracking(String.format("https://core.ac.uk/display/%s", documentId));
        this.creditString = String.format("Provided by %s", dataProviderName);
        this.dataProviderName = dataProviderName;
    }
}
