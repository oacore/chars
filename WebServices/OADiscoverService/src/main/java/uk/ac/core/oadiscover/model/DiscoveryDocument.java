package uk.ac.core.oadiscover.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "doi",
    "sources",
    "bestSourceIsValid"
})
@Document(indexName = "discovery", type = "documents")
public class DiscoveryDocument {

    @Id
    private String id;
    private String doi;
    private List<DiscoverySource> sources;
    private Boolean bestSourceIsValid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public List<DiscoverySource> getSources() {
        return sources;
    }

    public void setSources(List<DiscoverySource> sources) {
        this.sources = sources;
    }

    public Boolean getBestSourceIsValid() {
        return bestSourceIsValid;
    }

    public void setBestSourceIsValid(Boolean bestSourceIsValid) {
        this.bestSourceIsValid = bestSourceIsValid;
    }

    @Override
    public String toString() {
        return "DiscoveryDocument{" + "id=" + id + ", doi=" + doi + ", sources=" + sources + ", bestSourceIsValid=" + bestSourceIsValid + '}';
    }

}
