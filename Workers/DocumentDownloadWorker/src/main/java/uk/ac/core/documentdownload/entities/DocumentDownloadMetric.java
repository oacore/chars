package uk.ac.core.documentdownload.entities;

/**
 *
 * @author mc26486
 */
public class DocumentDownloadMetric {

    private Integer repositoryId;
    private Long startTime = 0L;
    private Integer numberOfRequestsPerformed =0;
    private Integer numberOfPdfDownloaded =0;
    private Integer numberOfDocumentsProcessed =0;
    private Long endTime = 0L;

    public Integer getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Integer repositoryId) {
        this.repositoryId = repositoryId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Integer getNumberOfRequestsPerformed() {
        return numberOfRequestsPerformed;
    }

    public void setNumberOfRequestsPerformed(Integer numberOfRequestsPerformed) {
        this.numberOfRequestsPerformed = numberOfRequestsPerformed;
    }

    public Integer getNumberOfPdfDownloaded() {
        return numberOfPdfDownloaded;
    }

    public void setNumberOfPdfDownloaded(Integer numberOfPdfDownloaded) {
        this.numberOfPdfDownloaded = numberOfPdfDownloaded;
    }

    public Integer getNumberOfDocumentsProcessed() {
        return numberOfDocumentsProcessed;
    }

    public void setNumberOfDocumentsProcessed(Integer numberOfDocumentsProcessed) {
        this.numberOfDocumentsProcessed = numberOfDocumentsProcessed;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "DocumentDownloadMetric{" + "repositoryId=" + repositoryId + ", startTime=" + startTime + ", numberOfRequestsPerformed=" + numberOfRequestsPerformed + ", numberOfPdfDownloaded=" + numberOfPdfDownloaded + ", numberOfDocumentProcessed=" + numberOfDocumentsProcessed + ", endTime=" + endTime + '}';
    }

}
