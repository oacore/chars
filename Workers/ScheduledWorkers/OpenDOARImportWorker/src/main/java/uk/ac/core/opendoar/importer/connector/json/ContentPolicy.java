package uk.ac.core.opendoar.importer.connector.json;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContentPolicy {

    @SerializedName("url")
    @Expose
    private List<String> url = null;
    @SerializedName("versions")
    @Expose
    private List<String> versions = null;
    @SerializedName("metadata")
    @Expose
    private List<String> metadata = null;
    @SerializedName("languages")
    @Expose
    private List<String> languages = null;
    @SerializedName("repository_type")
    @Expose
    private String repositoryType;
    @SerializedName("types_included")
    @Expose
    private Object typesIncluded;

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    public List<String> getVersions() {
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    public List<String> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<String> metadata) {
        this.metadata = metadata;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getRepositoryType() {
        return repositoryType;
    }

    public void setRepositoryType(String repositoryType) {
        this.repositoryType = repositoryType;
    }

    public Object getTypesIncluded() {
        return typesIncluded;
    }

    public void setTypesIncluded(Object typesIncluded) {
        this.typesIncluded = typesIncluded;
    }

}
