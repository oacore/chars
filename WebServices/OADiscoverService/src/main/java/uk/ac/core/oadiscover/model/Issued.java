
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
    "date-parts"
})
public class Issued {

    @JsonProperty("date-parts")
    private List<List<Long>> dateParts = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Issued() {
    }

    /**
     * 
     * @param dateParts
     */
    public Issued(List<List<Long>> dateParts) {
        super();
        this.dateParts = dateParts;
    }

    @JsonProperty("date-parts")
    public List<List<Long>> getDateParts() {
        return dateParts;
    }

    @JsonProperty("date-parts")
    public void setDateParts(List<List<Long>> dateParts) {
        this.dateParts = dateParts;
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
