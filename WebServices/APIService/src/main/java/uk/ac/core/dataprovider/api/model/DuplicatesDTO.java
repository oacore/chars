package uk.ac.core.dataprovider.api.model;

import java.util.Set;

public class DuplicatesDTO {

    private Integer documentId;
    private Set<Integer> annoyDuplicates;
    private Set<Integer> worksDuplicates;

    public DuplicatesDTO(Integer documentId, Set<Integer> annoyDuplicates, Set<Integer> worksDuplicates) {
        this.documentId = documentId;
        this.annoyDuplicates = annoyDuplicates;
        this.worksDuplicates = worksDuplicates;
    }

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }

    public Set<Integer> getAnnoyDuplicates() {
        return annoyDuplicates;
    }

    public void setAnnoyDuplicates(Set<Integer> annoyDuplicates) {
        this.annoyDuplicates = annoyDuplicates;
    }

    public Set<Integer> getWorksDuplicates() {
        return worksDuplicates;
    }

    public void setWorksDuplicates(Set<Integer> worksDuplicates) {
        this.worksDuplicates = worksDuplicates;
    }
}
