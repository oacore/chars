package uk.ac.core.oadiscover.services;

import java.util.List;

/**
 *
 * @author lucasanastasiou
 */
public class OADiscoveryResult {

//"id", "title", "authors", "year", "urls","fullTextIdentifier"
    String id;
    String title;
    List<String> authors;
    String year;
    List<String> urls;
    String fullTextIdentifier;
    boolean hasFullText;
    Double score;

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public boolean hasFullText() {
        return hasFullText;
    }

    public void setHasFullText(boolean hasFullText) {
        this.hasFullText = hasFullText;
    }

    @Override
    public String toString() {
        return "OADiscoveryResult{" + "id=" + id + ", title=" + title + ", authors=" + authors + ", year=" + year + ", urls=" + urls + ", fullTextIdentifier=" + fullTextIdentifier + ", hasFullText=" + hasFullText + ", score=" + score + '}';
    }

    

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

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getFullTextIdentifier() {
        return fullTextIdentifier;
    }

    public void setFullTextIdentifier(String fullTextIdentifier) {
        this.fullTextIdentifier = fullTextIdentifier;
    }
    
}
