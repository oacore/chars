package uk.ac.core.services.web.ref.model;

import java.util.Date;

public class RefReportDTO {

    private Integer idDocument;
    private String doi;
    private Integer idRepository;
    private String repoName;
    private Date publicReleaseDate;
    private Date publicationDate;
    private Date publicationDateCrossref;


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

    public Date getPublicReleaseDate() {
        return publicReleaseDate;
    }

    public void setPublicReleaseDate(Date publicReleaseDate) {
        this.publicReleaseDate = publicReleaseDate;
    }


    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }


    public Date getPublicationDateCrossref() {
        return publicationDateCrossref;
    }

    public void setPublicationDateCrossref(Date publicationDateCrossref) {
        this.publicationDateCrossref = publicationDateCrossref;
    }
    @Override
    public String toString() {
        return "" + idDocument + ", " + doi  + ", " + idRepository + ", " + repoName + ", " + publicationDate
                + ", " + publicationDateCrossref  + ", " + publicReleaseDate;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }
}
