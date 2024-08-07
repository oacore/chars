
package uk.ac.core.opendoar.importer.connector.json;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataPolicy {

    @SerializedName("reuse")
    @Expose
    private String reuse;
    @SerializedName("url")
    @Expose
    private List<String> url = null;
    @SerializedName("harvesting")
    @Expose
    private List<String> harvesting = null;
    @SerializedName("reuse_permitted_purposes")
    @Expose
    private List<String> reusePermittedPurposes = null;
    @SerializedName("reuse_conditions")
    @Expose
    private List<String> reuseConditions = null;
    @SerializedName("reuse_requirements")
    @Expose
    private List<String> reuseRequirements = null;
    @SerializedName("reuse_permitted_actions")
    @Expose
    private List<String> reusePermittedActions = null;
    @SerializedName("access")
    @Expose
    private String access;

    public String getReuse() {
        return reuse;
    }

    public void setReuse(String reuse) {
        this.reuse = reuse;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    public List<String> getHarvesting() {
        return harvesting;
    }

    public void setHarvesting(List<String> harvesting) {
        this.harvesting = harvesting;
    }

    public List<String> getReusePermittedPurposes() {
        return reusePermittedPurposes;
    }

    public void setReusePermittedPurposes(List<String> reusePermittedPurposes) {
        this.reusePermittedPurposes = reusePermittedPurposes;
    }

    public List<String> getReuseConditions() {
        return reuseConditions;
    }

    public void setReuseConditions(List<String> reuseConditions) {
        this.reuseConditions = reuseConditions;
    }

    public List<String> getReuseRequirements() {
        return reuseRequirements;
    }

    public void setReuseRequirements(List<String> reuseRequirements) {
        this.reuseRequirements = reuseRequirements;
    }

    public List<String> getReusePermittedActions() {
        return reusePermittedActions;
    }

    public void setReusePermittedActions(List<String> reusePermittedActions) {
        this.reusePermittedActions = reusePermittedActions;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

}
