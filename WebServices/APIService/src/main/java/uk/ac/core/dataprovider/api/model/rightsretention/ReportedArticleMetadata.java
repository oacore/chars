package uk.ac.core.dataprovider.api.model.rightsretention;

import java.sql.Timestamp;
import java.util.List;

public class ReportedArticleMetadata {
    private int articleId;
    private String title;
    private String oai;
    private Timestamp publicationDate;
    private String rightsRetentionSentence;
    private double confidence;
    private String licenceRecognised;
    private String licenceMetadata;
    private List<String> authors;
    private List<String> setSpecs;

    public ReportedArticleMetadata() {
    }

    public ReportedArticleMetadata(HighlightedArticleMetadata ham) {
        if (ham != null) {
            this.articleId = ham.getCoreId();
            this.oai = ham.getOai();
            this.publicationDate = ham.getPublicationDate();
            this.authors = ham.getAuthors();
            this.setSpecs = ham.getSetSpecs();
        }
    }

    public List<String> getSetSpecs() {
        return setSpecs;
    }

    public void setSetSpecs(List<String> setSpecs) {
        this.setSpecs = setSpecs;
    }

    public String getRightsRetentionSentence() {
        return rightsRetentionSentence;
    }

    public void setRightsRetentionSentence(String rightsRetentionSentence) {
        this.rightsRetentionSentence = rightsRetentionSentence;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOai() {
        return oai;
    }

    public void setOai(String oai) {
        this.oai = oai;
    }

    public Timestamp getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Timestamp publicationDate) {
        this.publicationDate = publicationDate;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getLicenceRecognised() {
        return licenceRecognised;
    }

    public void setLicenceRecognised(String licenceRecognised) {
        this.licenceRecognised = licenceRecognised;
    }

    public String getLicenceMetadata() {
        return licenceMetadata;
    }

    public void setLicenceMetadata(String licenceMetadata) {
        this.licenceMetadata = licenceMetadata;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
}
