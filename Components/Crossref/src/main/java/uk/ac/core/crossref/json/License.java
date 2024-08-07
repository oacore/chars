
package uk.ac.core.crossref.json;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "URL",
    "start",
    "delay-in-days",
    "content-version"
})
public class License {

    @JsonProperty("URL")
    private String uRL;
    @JsonProperty("start")
    private Start start;
    @JsonProperty("delay-in-days")
    private Integer delayInDays;
    @JsonProperty("content-version")
    private String contentVersion;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("URL")
    public String getURL() {
        return uRL;
    }

    @JsonProperty("URL")
    public void setURL(String uRL) {
        this.uRL = uRL;
    }

    public License withURL(String uRL) {
        this.uRL = uRL;
        return this;
    }

    @JsonProperty("start")
    public Start getStart() {
        return start;
    }

    @JsonProperty("start")
    public void setStart(Start start) {
        this.start = start;
    }

    public License withStart(Start start) {
        this.start = start;
        return this;
    }

    @JsonProperty("delay-in-days")
    public Integer getDelayInDays() {
        return delayInDays;
    }

    @JsonProperty("delay-in-days")
    public void setDelayInDays(Integer delayInDays) {
        this.delayInDays = delayInDays;
    }

    public License withDelayInDays(Integer delayInDays) {
        this.delayInDays = delayInDays;
        return this;
    }

    @JsonProperty("content-version")
    public String getContentVersion() {
        return contentVersion;
    }

    @JsonProperty("content-version")
    public void setContentVersion(String contentVersion) {
        this.contentVersion = contentVersion;
    }

    public License withContentVersion(String contentVersion) {
        this.contentVersion = contentVersion;
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

    public License withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
