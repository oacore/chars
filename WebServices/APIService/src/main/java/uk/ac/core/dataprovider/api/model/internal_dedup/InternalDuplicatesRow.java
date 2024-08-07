package uk.ac.core.dataprovider.api.model.internal_dedup;

public class InternalDuplicatesRow {
    private int documentId;
    private int workId;
    private int count;
    private int idRepository;
    private double confidence;

    public InternalDuplicatesRow(int documentId, int workId, int count, int idRepository, double confidence) {
        this.documentId = documentId;
        this.workId = workId;
        this.count = count;
        this.idRepository = idRepository;
        this.confidence = confidence;
    }

    public InternalDuplicatesRow() {
    }

    public int getDocumentId() {
        return documentId;
    }

    public int getWorkId() {
        return workId;
    }

    public int getCount() {
        return count;
    }

    public int getIdRepository() {
        return idRepository;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public void setWorkId(int workId) {
        this.workId = workId;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setIdRepository(int idRepository) {
        this.idRepository = idRepository;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
