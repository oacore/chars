package uk.ac.core.reporting.metrics.service.dto;

public final class OverallStatsBuilder {
    private long allMetadataCount;
    private long allowedMetadataCount;
    private long extractedTextsCount;
    private long downloadedPdfsCount;
    private long thumbnailsCount;
    private long harvestedReposCount;
    private double freshnessGB;
    private double freshness;
    private double documentFreshness;

    public OverallStatsBuilder allMetadataCount(long allMetadataCount) {
        this.allMetadataCount = allMetadataCount;
        return this;
    }

    public OverallStatsBuilder allowedMetadataCount(long allowedMetadataCount) {
        this.allowedMetadataCount = allowedMetadataCount;
        return this;
    }

    public OverallStatsBuilder extractedTextsCount(long extractedTextsCount) {
        this.extractedTextsCount = extractedTextsCount;
        return this;
    }

    public OverallStatsBuilder downloadedPdfsCount(long downloadedPdfsCount) {
        this.downloadedPdfsCount = downloadedPdfsCount;
        return this;
    }

    public OverallStatsBuilder thumbnailsCount(long thumbnailsCount) {
        this.thumbnailsCount = thumbnailsCount;
        return this;
    }

    public OverallStatsBuilder harvestedReposCount(long harvestedReposCount) {
        this.harvestedReposCount = harvestedReposCount;
        return this;
    }

    public OverallStatsBuilder freshnessGB(double freshnessGB) {
        this.freshnessGB = freshnessGB;
        return this;
    }

    public OverallStatsBuilder freshness(double freshness) {
        this.freshness = freshness;
        return this;
    }

    public OverallStatsBuilder documentFreshness(double documentFreshness) {
        this.documentFreshness = documentFreshness;
        return this;
    }

    public OverallStats build() {
        return new OverallStats(allMetadataCount, allowedMetadataCount, extractedTextsCount, downloadedPdfsCount, thumbnailsCount, harvestedReposCount, freshnessGB, freshness, documentFreshness);
    }
}