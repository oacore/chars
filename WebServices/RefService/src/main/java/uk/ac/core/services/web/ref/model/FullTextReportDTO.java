package uk.ac.core.services.web.ref.model;

public class FullTextReportDTO {

    private Integer idDocument;
    private String doi;
    private Integer idRepository;
    private String repoName;
    private Boolean fullTextAvailable;
    private long fulltextFileSize;


    public Integer getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(Integer idDocument) {
        this.idDocument = idDocument;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public Integer getIdRepository() {
        return idRepository;
    }

    public void setIdRepository(Integer idRepository) {
        this.idRepository = idRepository;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    @Override
    public String toString() {
        return "" + idDocument + ", " + doi  + ", " + idRepository + ", " + repoName + ", " + fullTextAvailable;
    }

    public Boolean getFullTextAvailable() {
        return fullTextAvailable;
    }

    public void setFullTextAvailable(Boolean fullTextAvailable) {
        this.fullTextAvailable = fullTextAvailable;
    }

    public long getFulltextFileSize() {
        return fulltextFileSize;
    }

    public void setFulltextFileSize(long fulltextFileSize) {
        this.fulltextFileSize = fulltextFileSize;
    }
}
