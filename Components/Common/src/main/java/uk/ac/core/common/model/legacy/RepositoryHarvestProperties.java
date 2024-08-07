package uk.ac.core.common.model.legacy;

import uk.ac.core.common.model.article.LicenseStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mc26486
 */
public class RepositoryHarvestProperties {

    private String repositoryId;
    private HarvestLevel harvestLevel = HarvestLevel.LEVEL_2;
    private TryOnlyPdf tryOnlyPdf = TryOnlyPdf.LINK_ENDS_WITH_PDF;
    private boolean disabled;
    private boolean sameDomainPolicy;
    private AlgorithmType algorithmType;
    private List<String> acceptedContentTypes = new ArrayList<>();
    private String[] blackList = new String[0];
    private String harvestHeuristic = "";
    private boolean skipAlreadyDownloaded = true;
    private boolean prioritiseOldDocumentsForDownload;
    private boolean tdmOnly;
    private boolean useSignpost;
    private String pdfUrlSearchPattern; // used in metadataextraction stage
    private String pdfUrlReplacePattern; // used in metadataextraction stage
    private LicenseStrategy licenseStrategy;

    public LicenseStrategy getLicenseStrategy() {
        return licenseStrategy;
    }

    public void setLicenseStrategy(LicenseStrategy licenseStrategy) {
        this.licenseStrategy = licenseStrategy;
    }

    public String getPdfUrlSearchPattern() {
        return pdfUrlSearchPattern;
    }

    public void setPdfUrlSearchPattern(String pdfUrlSearchPattern) {
        this.pdfUrlSearchPattern = pdfUrlSearchPattern;
    }

    public String getPdfUrlReplacePattern() {
        return pdfUrlReplacePattern;
    }

    public void setPdfUrlReplacePattern(String pdfUrlReplacePattern) {
        this.pdfUrlReplacePattern = pdfUrlReplacePattern;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public HarvestLevel getHarvestLevel() {
        return harvestLevel;
    }

    public void setHarvestLevel(HarvestLevel harvestLevel) {
        this.harvestLevel = harvestLevel;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isSameDomainPolicy() {
        return sameDomainPolicy;
    }

    public void setSameDomainPolicy(boolean sameDomainPolicy) {
        this.sameDomainPolicy = sameDomainPolicy;
    }

    public AlgorithmType getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(AlgorithmType algorithmType) {
        this.algorithmType = algorithmType;
    }

    public List<String> getAcceptedContentTypes() {
        return acceptedContentTypes;
    }

    public void setAcceptedContentTypes(List<String> acceptedContentTypes) {
        this.acceptedContentTypes = acceptedContentTypes;
    }

    public String[] getBlackList() {
        return blackList;
    }

    public void setBlackList(String[] blackList) {
        this.blackList = blackList;
    }

    public String getHarvestHeuristic() {
        return harvestHeuristic;
    }

    public void setHarvestHeuristic(String harvestHeuristic) {
        this.harvestHeuristic = harvestHeuristic;
    }

    public boolean isSkipAlreadyDownloaded() {
        return skipAlreadyDownloaded;
    }

    public void setSkipAlreadyDownloaded(boolean skipAlreadyDownloaded) {
        this.skipAlreadyDownloaded = skipAlreadyDownloaded;
    }

    public boolean isPrioritiseOldDocumentsForDownload() {
        return prioritiseOldDocumentsForDownload;
    }

    public void setPrioritiseOldDocumentsForDownload(boolean prioritiseOldDocumentsForDownload) {
        this.prioritiseOldDocumentsForDownload = prioritiseOldDocumentsForDownload;
    }

    public TryOnlyPdf getTryOnlyPdf() {
        return tryOnlyPdf;
    }

    public void setTryOnlyPdf(TryOnlyPdf tryOnlyPdf) {
        this.tryOnlyPdf = tryOnlyPdf;
    }

    public boolean isTdmOnly() {
        return tdmOnly;
    }

    public void setTdmOnly(boolean tdmOnly) {
        this.tdmOnly = tdmOnly;
    }

    public boolean isUseSignpost() {
        return useSignpost;
    }

    public void setUseSignpost(boolean useSignpost) {
        this.useSignpost = useSignpost;
    }

}
