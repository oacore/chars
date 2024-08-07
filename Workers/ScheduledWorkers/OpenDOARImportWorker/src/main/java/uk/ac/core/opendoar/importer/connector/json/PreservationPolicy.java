
package uk.ac.core.opendoar.importer.connector.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PreservationPolicy {

    @SerializedName("closure_policy")
    @Expose
    private String closurePolicy;
    @SerializedName("retention_period")
    @Expose
    private RetentionPeriod retentionPeriod;
    @SerializedName("withdrawal")
    @Expose
    private Withdrawal withdrawal;

    public String getClosurePolicy() {
        return closurePolicy;
    }

    public void setClosurePolicy(String closurePolicy) {
        this.closurePolicy = closurePolicy;
    }

    public RetentionPeriod getRetentionPeriod() {
        return retentionPeriod;
    }

    public void setRetentionPeriod(RetentionPeriod retentionPeriod) {
        this.retentionPeriod = retentionPeriod;
    }

    public Withdrawal getWithdrawal() {
        return withdrawal;
    }

    public void setWithdrawal(Withdrawal withdrawal) {
        this.withdrawal = withdrawal;
    }
}
