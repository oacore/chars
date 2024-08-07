
package com.example;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UnpaywallResponse {

    @SerializedName("best_oa_location")
    @Expose
    private com.example.BestOaLocation bestOaLocation;
    @SerializedName("data_standard")
    @Expose
    private Integer dataStandard;
    @SerializedName("doi")
    @Expose
    private String doi;
    @SerializedName("doi_url")
    @Expose
    private String doiUrl;
    @SerializedName("genre")
    @Expose
    private String genre;
    @SerializedName("is_oa")
    @Expose
    private Boolean isOa;
    @SerializedName("journal_is_in_doaj")
    @Expose
    private Boolean journalIsInDoaj;
    @SerializedName("journal_is_oa")
    @Expose
    private Boolean journalIsOa;
    @SerializedName("journal_issns")
    @Expose
    private String journalIssns;
    @SerializedName("journal_name")
    @Expose
    private String journalName;
    @SerializedName("oa_locations")
    @Expose
    private List<com.example.OaLocation> oaLocations = null;
    @SerializedName("published_date")
    @Expose
    private String publishedDate;
    @SerializedName("publisher")
    @Expose
    private String publisher;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("updated")
    @Expose
    private String updated;
    @SerializedName("year")
    @Expose
    private Integer year;
    @SerializedName("z_authors")
    @Expose
    private List<com.example.ZAuthor> zAuthors = null;

    public com.example.BestOaLocation getBestOaLocation() {
        return bestOaLocation;
    }

    public void setBestOaLocation(com.example.BestOaLocation bestOaLocation) {
        this.bestOaLocation = bestOaLocation;
    }

    public Integer getDataStandard() {
        return dataStandard;
    }

    public void setDataStandard(Integer dataStandard) {
        this.dataStandard = dataStandard;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getDoiUrl() {
        return doiUrl;
    }

    public void setDoiUrl(String doiUrl) {
        this.doiUrl = doiUrl;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Boolean getIsOa() {
        return isOa;
    }

    public void setIsOa(Boolean isOa) {
        this.isOa = isOa;
    }

    public Boolean getJournalIsInDoaj() {
        return journalIsInDoaj;
    }

    public void setJournalIsInDoaj(Boolean journalIsInDoaj) {
        this.journalIsInDoaj = journalIsInDoaj;
    }

    public Boolean getJournalIsOa() {
        return journalIsOa;
    }

    public void setJournalIsOa(Boolean journalIsOa) {
        this.journalIsOa = journalIsOa;
    }

    public String getJournalIssns() {
        return journalIssns;
    }

    public void setJournalIssns(String journalIssns) {
        this.journalIssns = journalIssns;
    }

    public String getJournalName() {
        return journalName;
    }

    public void setJournalName(String journalName) {
        this.journalName = journalName;
    }

    public List<com.example.OaLocation> getOaLocations() {
        return oaLocations;
    }

    public void setOaLocations(List<com.example.OaLocation> oaLocations) {
        this.oaLocations = oaLocations;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<com.example.ZAuthor> getZAuthors() {
        return zAuthors;
    }

    public void setZAuthors(List<com.example.ZAuthor> zAuthors) {
        this.zAuthors = zAuthors;
    }

    @Override
    public String toString() {
        return "UnpaywallResponse{" + "bestOaLocation=" + bestOaLocation + ", dataStandard=" + dataStandard + ", doi=" + doi + ", doiUrl=" + doiUrl + ", genre=" + genre + ", isOa=" + isOa + ", journalIsInDoaj=" + journalIsInDoaj + ", journalIsOa=" + journalIsOa + ", journalIssns=" + journalIssns + ", journalName=" + journalName + ", oaLocations=" + oaLocations + ", publishedDate=" + publishedDate + ", publisher=" + publisher + ", title=" + title + ", updated=" + updated + ", year=" + year + ", zAuthors=" + zAuthors + '}';
    }
    
    

}
