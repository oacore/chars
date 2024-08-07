package uk.ac.core.workermetrics.data.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Downloaded documents metrics.
 */
@Entity
@Table(name = "document_download_metrics")
public final class DocumentDownloadMetrics {

    @Id
    @Column(name = "id_repository")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int repositoryId;

    @Column(name = "number_of_pdfs")
    private int numberOfPdfDownloaded;

    @Column(name = "attempted_documents")
    private int numberOfDocumentsAttempted;

    @Column(name = "requests")
    private int numberOfRequestsPerformed;

    private long duration;
    private LocalDateTime date = LocalDateTime.now();

    public DocumentDownloadMetrics(int numberOfPdfDownloaded, Integer numberOfDocumentsAttempted, int numberOfRequestsPerformed, long duration, LocalDateTime date) {
        this.numberOfPdfDownloaded = numberOfPdfDownloaded;
        this.numberOfDocumentsAttempted = numberOfDocumentsAttempted;
        this.numberOfRequestsPerformed = numberOfRequestsPerformed;
        this.duration = duration;
        this.date = date;
    }

    public DocumentDownloadMetrics() {

    }

    public int getNumberOfPdfDownloaded() {
        return numberOfPdfDownloaded;
    }

    public void setNumberOfPdfDownloaded(int numberOfPdfDownloaded) {
        this.numberOfPdfDownloaded = numberOfPdfDownloaded;
    }

    public int getNumberOfDocumentsAttempted() {
        return numberOfDocumentsAttempted;
    }

    public void setNumberOfDocumentsAttempted(int numberOfDocumentsAttempted) {
        this.numberOfDocumentsAttempted = numberOfDocumentsAttempted;
    }

    public int getNumberOfRequestsPerformed() {
        return numberOfRequestsPerformed;
    }

    public void setNumberOfRequestsPerformed(int numberOfRequestsPerformed) {
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