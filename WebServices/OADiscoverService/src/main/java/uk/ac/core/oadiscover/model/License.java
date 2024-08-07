
package uk.ac.core.oadiscover.model;

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
    "delay-in-days",
    "start",
    "content-version",
    "URL"
})
public class License {

    @JsonProperty("delay-in-days")
    private long delayInDays;
    @JsonProperty("start")
    private Start start;
    @JsonProperty("content-version")
    private String contentVersion;
    @JsonProperty("URL")
    private String uRL;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public License() {
    }

    /**
     * 
     * @param delayInDays
     * @param start
     * @param uRL
     * @param contentVersion
     */
    public License(long delayInDays, Start start, String contentVersion, String uRL) {
        super();
        this.delayInDays = delayInDays;
        this.start = start;
        this.contentVersion = contentVersion;
        this.uRL = uRL;
    }

    @JsonProperty("delay-in-days")
    public long getDelayInDays() {
        return delayInDays;
    }

    @JsonProperty("delay-in-days")
    public void setDelayInDays(long delayInDays) {
        this.delayInDays = delayInDays;
    }

    @JsonProperty("start")
    public Start getStart() {
        return start;
    }

    @JsonProperty("start")
    public void setStart(Start start) {
        this.start = start;
    }

    @JsonProperty("content-version")
    public String getContentVersion() {
        return contentVersion;
    }

    @JsonProperty("content-version")
    public void setContentVersion(String contentVersion) {
        this.contentVersion = contentVersion;
    }

    @JsonProperty("URL")
    public String getURL() {
        return uRL;
    }

    @JsonProperty("URL")
    public void setURL(String uRL) {
        this.uRL = uRL;
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
