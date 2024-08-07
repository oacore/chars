package uk.ac.core.dataprovider.api.model.internal_dedup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class DeduplicationReport {
    private int idRepository;
    private int count;
    private long millis;
    private DuplicateList duplicateList;
    private Date generationTime;

    private String errorMessage;

    public DeduplicationReport(DuplicateList duplicateList, int idRepository, int count, long millis) {
        this.duplicateList = duplicateList;
        this.idRepository = idRepository;
        this.count = count;
        this.millis = millis;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public DeduplicationReport() {
        this.generationTime = new Date();
    }

    public DuplicateList getDuplicateList() {
        return duplicateList;
    }

    public void setDuplicateList(DuplicateList duplicateList) {
        this.duplicateList = duplicateList;
    }

    public int getIdRepository() {
        return idRepository;
    }

    public void setIdRepository(int idRepository) {
        this.idRepository = idRepository;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public Date getGenerationTime() {
        return generationTime;
    }

    public void setGenerationTime(Date generationTime) {
        this.generationTime = generationTime;
    }
}
