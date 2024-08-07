package uk.ac.core.elasticsearch.entities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import uk.ac.core.common.model.legacy.Citation;

/**
 *
 * @author la4227 <lucas.anastasiou@open.ac.uk>
 */
public class ElasticSearchCitation {

    @Field(type = FieldType.Long)
    private Integer id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private List<String> authors;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd")
    private String date;

    @Field(type = FieldType.Keyword)
    private String doi;

    @Field(type = FieldType.Text)
    private String raw;

    @Field(type = FieldType.Text)
    private List<Integer> cites;

    public ElasticSearchCitation() {
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

    public static List<ElasticSearchCitation> convertFromCitation(List<Citation> amCitations) {
        List<ElasticSearchCitation> elasticSearchCitations = new ArrayList<>();
        for (Citation cit : amCitations) {
            ElasticSearchCitation esc = new ElasticSearchCitation();
            esc.setAuthors(cit.getAuthors());
            esc.setCites(null);
            esc.setDate(cit.getDate());
            esc.setDoi(cit.getDoi());
            esc.setId(cit.getIdCitation());
            esc.setRaw(cit.getRawString());
            esc.setTitle(cit.getTitle());
            elasticSearchCitations.add(esc);
        }
        return elasticSearchCitations;
    }

}
