
package uk.ac.core.crossref.json;

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
    "domain",
    "crossmark-restriction"
})
public class ContentDomain {

    @JsonProperty("domain")
    private List<Object> domain = null;
    @JsonProperty("crossmark-restriction")
    private Boolean crossmarkRestriction;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("domain")
    public List<Object> getDomain() {
        return domain;
    }

    @JsonProperty("domain")
    public void setDomain(List<Object> domain) {
        this.domain = domain;
    }

    public ContentDomain withDomain(List<Object> domain) {
        this.domain = domain;
        return this;
    }

    @JsonProperty("crossmark-restriction")
    public Boolean getCrossmarkRestriction() {
        return crossmarkRestriction;
    }

    @JsonProperty("crossmark-restriction")
    public void setCrossmarkRestriction(Boolean crossmarkRestriction) {
        this.crossmarkRestriction = crossmarkRestriction;
    }

    public ContentDomain withCrossmarkRestriction(Boolean crossmarkRestriction) {
        this.crossmarkRestriction = crossmarkRestriction;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public ContentDomain withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
