
package uk.ac.core.opendoar.importer.connector.json;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubmissionPolicy {

    @SerializedName("content_embargo")
    @Expose
    private String contentEmbargo;
    @SerializedName("moderation_purposes")
    @Expose
    private List<String> moderationPurposes = null;
    @SerializedName("url")
    @Expose
    private List<String> url = null;
    @SerializedName("quality_control")
    @Expose
    private String qualityControl;
    @SerializedName("moderation")
    @Expose
    private String moderation;
    @SerializedName("rules")
    @Expose
    private List<String> rules = null;
    @SerializedName("depositors")
    @Expose
    private List<String> depositors = null;

    public String getContentEmbargo() {
        return contentEmbargo;
    }

    public void setContentEmbargo(String contentEmbargo) {
        this.contentEmbargo = contentEmbargo;
    }

    public List<String> getModerationPurposes() {
        return moderationPurposes;
    }

    public void setModerationPurposes(List<String> moderationPurposes) {
        this.moderationPurposes = moderationPurposes;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    public String getQualityControl() {
        return qualityControl;
    }

    public void setQualityControl(String qualityControl) {
        this.qualityControl = qualityControl;
    }

    public String getModeration() {
        return moderation;
    }

    public void setModeration(String moderation) {
        this.moderation = moderation;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    public List<String> getDepositors() {
        return depositors;
    }

    public void setDepositors(List<String> depositors) {
        this.depositors = depositors;
    }
}
