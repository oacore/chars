package uk.ac.core.elasticsearch.entities;

import java.util.List;

/**
 * @author mc26486
 */
public class ElasticSearchSimilarDocument {

    private String id;
    private String title;
    private String url;
    private String repositoryName;
    private String repositoryId;
    private String publisher;
    private String year;
    private List<String> authors;
    private List<String> urls;
    private String language;
    private Long simhash;
    private Double score;
    private String downloadUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public Long getSimhash() {
        return simhash;
    }

    public void setSimhash(Long simhash) {
        this.simhash = simhash;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public String toString() {
        return "ElasticSearchSimilarDocument{" + "id=" + id + ", title=" + title + ", url=" + url + ", repositoryName=" + repositoryName + ", repositoryId=" + repositoryId + ", publisher=" + publisher + ", year=" + year + ", authors=" + authors + ", urls=" + urls + ", language=" + language + ", simhash=" + simhash + ", score=" + score + '}';
    }

}
