
package uk.ac.core.oadiscover.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "affiliation",
    "given",
    "suffix",
    "family"
})
public class Author {

    @JsonProperty("affiliation")
    private List<Object> affiliation = null;
    @JsonProperty("given")
    private String given;
    @JsonProperty("suffix")
    private String suffix;
    @JsonProperty("family")
    private String family;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Author() {
    }

    /**
     * 
     * @param given
     * @param family
     * @param affiliation
     * @param suffix
     */
    public Author(List<Object> affiliation, String given, String suffix, String family) {
        super();
        this.affiliation = affiliation;
        this.given = given;
        this.suffix = suffix;
        this.family = family;
    }

    @JsonProperty("affiliation")
    public List<Object> getAffiliation() {
        return affiliation;
    }

    @JsonProperty("affiliation")
    public void setAffiliation(List<Object> affiliation) {
        this.affiliation = affiliation;
    }

    @JsonProperty("given")
    public String getGiven() {
        return given;
    }

    @JsonProperty("given")
    public void setGiven(String given) {
        this.given = given;
    }

    @JsonProperty("suffix")
    public String getSuffix() {
        return suffix;
    }

    @JsonProperty("suffix")
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @JsonProperty("family")
    public String getFamily() {
        return family;
    }

    @JsonProperty("family")
    public void setFamily(String family) {
        this.family = family;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
