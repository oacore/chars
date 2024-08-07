package uk.ac.core.database.model;

public class WorksToDocumentDTO {
    private Integer workId;
    private Integer documentId;
    private Explanation explanation;
    private Double confidence;

    public WorksToDocumentDTO() {
    }

    public WorksToDocumentDTO(Integer documentId, Explanation explanation) {
        this.documentId = documentId;
        this.explanation = explanation;
    }

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }

    public Explanation getExplanation() {
        return explanation;
    }

    public void setExplanation(Explanation explanation) {
        this.explanation = explanation;
    }

    public Integer getWorkId() {
        return workId;
    }

    public void setWorkId(Integer workId) {
        this.workId = workId;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }


    public enum Explanation {
        doi, dedupv1
    }
}
