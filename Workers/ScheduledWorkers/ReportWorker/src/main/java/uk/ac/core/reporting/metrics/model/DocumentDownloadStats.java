package uk.ac.core.reporting.metrics.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Document download stats.
 */
public final class DocumentDownloadStats {

    @JsonProperty("attempted_documents_downloads")
    private final long attemptedDownloadDocumentsCount;

    @JsonProperty("successful_document_downloads")
    private final long successfulDownloadDocumentsCount;

    public DocumentDownloadStats(long attemptedDownloadDocumentsCount, long successfulDownloadDocumentsCount) {
        this.attemptedDownloadDocumentsCount = attemptedDownloadDocumentsCount;
        this.successfulDownloadDocumentsCount = successfulDownloadDocumentsCount;
    }

    public long getAttemptedDownloadDocumentsCount() {
        return attemptedDownloadDocumentsCount;
    }

    public long getSuccessfulDownloadDocumentsCount() {
        return successfulDownloadDocumentsCount;
    }
}