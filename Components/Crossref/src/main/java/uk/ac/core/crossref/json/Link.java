
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
    "content-type",
    "content-version",
    "intended-application"
})
public class Link {

    @JsonProperty("URL")
    private String uRL;
    @JsonProperty("content-type")
    private String contentType;
    @JsonProperty("content-version")
    private String contentVersion;
    @JsonProperty("intended-application")
    private String intendedApplication;
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

    public Link withURL(String uRL) {
        this.uRL = uRL;
        return this;
    }

    @JsonProperty("content-type")
    public String getContentType() {
        return contentType;
    }

    @JsonProperty("content-type")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Link withContentType(String contentType) {
        this.contentType = contentType;
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

    public Link withContentVersion(String contentVersion) {
        this.contentVersion = contentVersion;
        return this;
    }

    @JsonProperty("intended-application")
    public String getIntendedApplication() {
        return intendedApplication;
    }

    @JsonProperty("intended-application")
    public void setIntendedApplication(String intendedApplication) {
        this.intendedApplication = intendedApplication;
    }

    public Link withIntendedApplication(String intendedApplication) {
        this.intendedApplication = intendedApplication;
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

    public Link withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
