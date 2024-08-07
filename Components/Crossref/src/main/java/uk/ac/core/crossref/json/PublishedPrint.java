
package uk.ac.core.crossref.json;

import com.fasterxml.jackson.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "date-parts"
})
public class PublishedPrint {

    @JsonProperty("date-parts")
    private List<List<Integer>> dateParts = null;
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

    public PublishedPrint withDateParts(List<List<Integer>> dateParts) {
        this.dateParts = dateParts;
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

    public PublishedPrint withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    public String toDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp timestamp = CrossRefDocument.datePartsToTimestamp(this.dateParts.get(0));
        return sdf.format(timestamp);
    }

    public int getYear() {
        return this.dateParts.get(0).get(0);
    }

}
