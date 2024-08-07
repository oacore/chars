
package uk.ac.core.opendoar.importer.connector.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Policies {

    @SerializedName("metadata_policy")
    @Expose
    private MetadataPolicy metadataPolicy;
    @SerializedName("content_policy")
    @Expose
    private ContentPolicy contentPolicy;
    @SerializedName("data_policy")
    @Expose
    private DataPolicy dataPolicy;
    @SerializedName("submission_policy")
    @Expose
    private SubmissionPolicy submissionPolicy;
    @SerializedName("preservation_policy")
    @Expose
    private PreservationPolicy preservationPolicy;

    public MetadataPolicy getMetadataPolicy() {
        return metadataPolicy;
    }

    public void setMetadataPolicy(MetadataPolicy metadataPolicy) {
        this.metadataPolicy = metadataPolicy;
    }

    public ContentPolicy getContentPolicy() {
        return contentPolicy;
    }

    public void setContentPolicy(ContentPolicy contentPolicy) {
        this.contentPolicy = contentPolicy;
    }

    public DataPolicy getDataPolicy() {
        return dataPolicy;
    }

    public void setDataPolicy(DataPolicy dataPolicy) {
        this.dataPolicy = dataPolicy;
    }

    public SubmissionPolicy getSubmissionPolicy() {
        return submissionPolicy;
    }

    public void setSubmissionPolicy(SubmissionPolicy submissionPolicy) {
        this.submissionPolicy = submissionPolicy;
    }

    public PreservationPolicy getPreservationPolicy() {
        return preservationPolicy;
    }

    public void setPreservationPolicy(PreservationPolicy preservationPolicy) {
        this.preservationPolicy = preservationPolicy;
    }

}
