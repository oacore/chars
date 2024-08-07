
package uk.ac.core.opendoar.importer.connector.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("system_metadata")
    @Expose
    private SystemMetadata systemMetadata;
    @SerializedName("policies")
    @Expose
    private Policies policies;
    @SerializedName("repository_metadata")
    @Expose
    private RepositoryMetadata repositoryMetadata;
    @SerializedName("organisation")
    @Expose
    private Organisation organisation;

    public SystemMetadata getSystemMetadata() {
        return systemMetadata;
    }

    public void setSystemMetadata(SystemMetadata systemMetadata) {
        this.systemMetadata = systemMetadata;
    }

    public Policies getPolicies() {
        return policies;
    }

    public void setPolicies(Policies policies) {
        this.policies = policies;
    }

    public RepositoryMetadata getRepositoryMetadata() {
        return repositoryMetadata;
    }

    public void setRepositoryMetadata(RepositoryMetadata repositoryMetadata) {
        this.repositoryMetadata = repositoryMetadata;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

}
