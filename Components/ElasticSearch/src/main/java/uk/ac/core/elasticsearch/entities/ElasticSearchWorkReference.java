package uk.ac.core.elasticsearch.entities;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

public class ElasticSearchWorkReference {
    @Field(type = FieldType.Long)
    private Integer id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private List<String> authors;

    @Field(type = FieldType.Date)
    private String date;

    @Field(type = FieldType.Keyword)
    private String doi;

    @Field(type = FieldType.Text)
    private String raw;

    @Field(type = FieldType.Text)
    private List<Integer> cites;

    public ElasticSearchWorkReference() {
    }

    public ElasticSearchWorkReference(ElasticSearchCitation citation) {
        this.id = citation.getId();
        this.title = citation.getTitle();
        this.authors = citation.getAuthors();
        this.date = citation.getDate();
        this.doi = citation.getDoi();
        this.raw = citation.getRaw();
        this.cites = citation.getCites();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public List<Integer> getCites() {
        return cites;
    }

    public void setCites(List<Integer> cites) {
        this.cites = cites;
    }
}
