
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
    "timestamp",
    "date-time",
    "date-parts"
})
public class Start {

    @JsonProperty("timestamp")
    private long timestamp;
    @JsonProperty("date-time")
    private String dateTime;
    @JsonProperty("date-parts")
    private List<List<Long>> dateParts = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Start() {
    }

    /**
     * 
     * @param dateParts
     * @param timestamp
     * @param dateTime
     */
    public Start(long timestamp, String dateTime, List<List<Long>> dateParts) {
        super();
        this.timestamp = timestamp;
        this.dateTime = dateTime;
        this.dateParts = dateParts;
    }

    @JsonProperty("timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("date-time")
    public String getDateTime() {
        return dateTime;
    }

    @JsonProperty("date-time")
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
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
