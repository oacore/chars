package uk.ac.core.dataprovider.api.model.rightsretention;

import java.sql.Timestamp;
import java.util.List;

public class HighlightedArticleMetadata {
    private String highlightDataEs;
    private String highlightDataRx;
    private int coreId;
    private String oai;
    private Timestamp publicationDate;
    private String downloadUrl;
    private double scoreEs;
    private double scoreRx;
    private List<String> authors;
    private List<String> setSpecs;

    public HighlightedArticleMetadata() {
    }

    public List<String> getSetSpecs() {
        return setSpecs;
    }

    public void setSetSpecs(List<String> setSpecs) {
        this.setSpecs = setSpecs;
    }

    public double getScoreEs() {
        return scoreEs;
    }

    public void setScoreEs(double scoreEs) {
        this.scoreEs = scoreEs;
    }

    public double getScoreRx() {
        return scoreRx;
    }

    public void setScoreRx(double scoreRx) {
        this.scoreRx = scoreRx;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getCoreId() {
        return coreId;
    }

    public void setCoreId(int coreId) {
        this.coreId = coreId;
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

    public String getHighlightDataEs() {
        return highlightDataEs;
    }

    public void setHighlightDataEs(String highlightDataEs) {
        this.highlightDataEs = highlightDataEs;
    }

    public String getHighlightDataRx() {
        return highlightDataRx;
    }

    public void setHighlightDataRx(String highlightDataRx) {
        this.highlightDataRx = highlightDataRx;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
}
