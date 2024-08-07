
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
    "intended-application",
    "URL",
    "content-version",
    "content-type"
})
public class Link {

    @JsonProperty("intended-application")
    private String intendedApplication;
    @JsonProperty("URL")
    private String uRL;
    @JsonProperty("content-version")
    private String contentVersion;
    @JsonProperty("content-type")
    private String contentType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Link() {
    }

    /**
     * 
     * @param uRL
     * @param intendedApplication
     * @param contentVersion
     * @param contentType
     */
    public Link(String intendedApplication, String uRL, String contentVersion, String contentType) {
        super();
        this.intendedApplication = intendedApplication;
        this.uRL = uRL;
        this.contentVersion = contentVersion;
        this.contentType = contentType;
    }

    @JsonProperty("intended-application")
    public String getIntendedApplication() {
        return intendedApplication;
    }

    @JsonProperty("intended-application")
    public void setIntendedApplication(String intendedApplication) {
        this.intendedApplication = intendedApplication;
    }

    @JsonProperty("URL")
    public String getURL() {
        return uRL;
    }

    @JsonProperty("URL")
    public void setURL(String uRL) {
        this.uRL = uRL;
    }

    @JsonProperty("content-version")
    public String getContentVersion() {
        return contentVersion;
    }

    @JsonProperty("content-version")
    public void setContentVersion(String contentVersion) {
        this.contentVersion = contentVersion;
    }

    @JsonProperty("content-type")
    public String getContentType() {
        return contentType;
    }

    @JsonProperty("content-type")
    public void setContentType(String contentType) {
        this.contentType = contentType;
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
