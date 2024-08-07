package uk.ac.core.workermetrics.service.dto;

import java.time.LocalDateTime;

/**
 * Downloaded documents BO.
 */
public final class DocumentDownloadMetricsBO {

    private long numberOfDocumentsDownloaded;
    private long numberOfDocumentsAttempted;
    private long numberOfRequestsPerformed;
    private long duration;
    private LocalDateTime date;

    public DocumentDownloadMetricsBO(int numberOfDocumentsDownloaded, Integer numberOfDocumentsAttempted, int numberOfRequestsPerformed, long duration, LocalDateTime date) {
        this.numberOfDocumentsDownloaded = numberOfDocumentsDownloaded;
        this.numberOfDocumentsAttempted = numberOfDocumentsAttempted;
        this.numberOfRequestsPerformed = numberOfRequestsPerformed;
        this.duration = duration;
        this.date = date;
    }

    public DocumentDownloadMetricsBO() {

    }

    public long getNumberOfDocumentsDownloaded() {
        return numberOfDocumentsDownloaded;
    }

    public void setNumberOfDocumentsDownloaded(long numberOfDocumentsDownloaded) {
        this.numberOfDocumentsDownloaded = numberOfDocumentsDownloaded;
    }

    public long getNumberOfDocumentsAttempted() {
        return numberOfDocumentsAttempted;
    }

    public void setNumberOfDocumentsAttempted(long numberOfDocumentsAttempted) {
        this.numberOfDocumentsAttempted = numberOfDocumentsAttempted;
    }

    public long getNumberOfRequestsPerformed() {
        return numberOfRequestsPerformed;
    }

    public void setNumberOfRequestsPerformed(long numberOfRequestsPerformed) {
        this.numberOfRequestsPerformed = numberOfRequestsPerformed;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}