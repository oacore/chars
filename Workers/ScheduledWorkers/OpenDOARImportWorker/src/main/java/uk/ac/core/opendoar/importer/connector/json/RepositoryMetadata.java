
package uk.ac.core.opendoar.importer.connector.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RepositoryMetadata {

    @SerializedName("full_text_record_count")
    @Expose
    private String fullTextRecordCount;
    @SerializedName("repository_status")
    @Expose
    private String repositoryStatus;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("oai_url")
    @Expose
    private String oaiUrl;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("metadata_record_count")
    @Expose
    private String metadataRecordCount;
    @SerializedName("content_types")
    @Expose
    private List<String> contentTypes = null;
    @SerializedName("software")
    @Expose
    private Software software;
    @SerializedName("content_languages")
    @Expose
    private List<String> contentLanguages = null;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("content_subjects")
    @Expose
    private List<String> contentSubjects = null;
    @SerializedName("name")
    @Expose
    private List<Name> name = null;
    @SerializedName("year_established")
    @Expose
    private String yearEstablished;

    public String getFullTextRecordCount() {
        return fullTextRecordCount;
    }

    public void setFullTextRecordCount(String fullTextRecordCount) {
        this.fullTextRecordCount = fullTextRecordCount;
    }

    public String getRepositoryStatus() {
        return repositoryStatus;
    }

    public void setRepositoryStatus(String repositoryStatus) {
        this.repositoryStatus = repositoryStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOaiUrl() {
        return oaiUrl;
    }

    public void setOaiUrl(String oaiUrl) {
        this.oaiUrl = oaiUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMetadataRecordCount() {
        return metadataRecordCount;
    }

    public void setMetadataRecordCount(String metadataRecordCount) {
        this.metadataRecordCount = metadataRecordCount;
    }

    public List<String> getContentTypes() {
        return contentTypes;
    }

    public void setContentTypes(List<String> contentTypes) {
        this.contentTypes = contentTypes;
    }

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public List<String> getContentLanguages() {
        return contentLanguages;
    }

    public void setContentLanguages(List<String> contentLanguages) {
        this.contentLanguages = contentLanguages;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getContentSubjects() {
        return contentSubjects;
    }

    public void setContentSubjects(List<String> contentSubjects) {
        this.contentSubjects = contentSubjects;
    }

    public List<Name> getName() {
        return name;
    }

    public void setName(List<Name> name) {
        this.name = name;
    }

    public String getYearEstablished() {
        return yearEstablished;
    }

    public void setYearEstablished(String yearEstablished) {
        this.yearEstablished = yearEstablished;
    }

}
