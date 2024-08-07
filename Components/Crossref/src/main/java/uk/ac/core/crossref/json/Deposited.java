
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
    "date-parts",
    "date-time",
    "timestamp"
})
public class Deposited {

    @JsonProperty("date-parts")
    private List<List<Integer>> dateParts = null;
    @JsonProperty("date-time")
    private String dateTime;
    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("date-parts")
    public List<List<Integer>> getDateParts() {
        return dateParts;
    }

    @JsonProperty("date-parts")
    public void setDateParts(List<List<Integer>> dateParts) {
        this.dateParts = dateParts;
    }

    public Deposited withDateParts(List<List<Integer>> dateParts) {
        this.dateParts = dateParts;
        return this;
    }

    @JsonProperty("date-time")
    public String getDateTime() {
        return dateTime;
    }

    @JsonProperty("date-time")
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Deposited withDateTime(String dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    @JsonProperty("timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Deposited withTimestamp(Long timestamp) {
        this.timestamp = timestamp;
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

    public Deposited withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
