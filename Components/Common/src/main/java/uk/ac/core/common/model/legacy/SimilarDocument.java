package uk.ac.core.common.model.legacy;

import java.util.List;

/**
 *
 * @author lucasanastasiou
 */
public class SimilarDocument implements Comparable {

    private int id;
    private String title;
    private List<String> authors;
    private Integer year = null;
    private List<String> relations;
    private Float score;

    public SimilarDocument() {
    }

    public SimilarDocument(int id, String title, Float score) {
        this.id = id;
        this.title = title;
        this.score = score;
    }

    public SimilarDocument(int id, String title, List<String> authors, Integer year, float score) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.year = year;
        this.score = score;
    }
    
    public SimilarDocument(int id, String title, List<String> authors, Integer year, List<String> relations, float score) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.year = year;
        this.relations = relations;
        this.score = score;
    }

    public SimilarDocument(int id, float score) {
        this.id = id;
        this.title = "";
        this.score = score;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
    
    public List<String> getRelations() {
        return relations;
    }

    public void setRelations(List<String> relations) {
        this.relations = relations;
    }

    @Override
    public int compareTo(Object o) {
        SimilarDocument otherDoc = (SimilarDocument) o;
        return -this.getScore().compareTo(otherDoc.getScore());
    }
}
