package uk.ac.core.reporting.metrics.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OverallStats {

    @JsonProperty("all_metadata")
    private long allMetadataCount;

    @JsonProperty("allowed_metadata")
    private long allowedMetadataCount;

    @JsonProperty("full_text")
    private long extractedTextsCount;

    @JsonProperty("downloaded_documents")
    private long downloadedPdfsCount;

    @JsonProperty("thumbnails")
    private long thumbnailsCount;

    @JsonProperty("harvested_repos")
    private long harvestedReposCount;

    @JsonProperty("freshness_gb")
    private double freshnessGB;

    @JsonProperty("all_freshness")
    private double freshness;

    @JsonProperty("document_freshness")
    private double documentFreshness;

    OverallStats(long allMetadataCount, long allowedMetadataCount, long extractedTextsCount, long downloadedPdfsCount, long thumbnailsCount, long harvestedReposCount, double freshnessGB, double freshness, double documentFreshness) {
        this.allMetadataCount = allMetadataCount;
        this.allowedMetadataCount = allowedMetadataCount;
        this.extractedTextsCount = extractedTextsCount;
        this.downloadedPdfsCount = downloadedPdfsCount;
        this.thumbnailsCount = thumbnailsCount;
        this.harvestedReposCount = harvestedReposCount;
        this.freshnessGB = freshnessGB;
        this.freshness = freshness;
        this.documentFreshness = documentFreshness;
    }

    public long getMetadataCount() {
        return allMetadataCount;
    }

    public long getAllowedMetadataCount() {
        return allowedMetadataCount;
    }

    public long getExtractedTextsCount() {
        return extractedTextsCount;
    }

    public long getDownloadedPdfsCount() {
        return downloadedPdfsCount;
    }

    public long getThumbnailsCount() {
        return thumbnailsCount;
    }

    public long getHarvestedReposCount() {
        return harvestedReposCount;
    }

    public double getFreshnessGB() {
        return freshnessGB;
    }

    public double getFreshness() {
        return freshness;
    }

    public double getDocumentFreshness() {
        return documentFreshness;
    }
}
