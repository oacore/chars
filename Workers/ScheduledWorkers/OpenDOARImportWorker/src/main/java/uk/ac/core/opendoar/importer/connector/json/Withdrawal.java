
package uk.ac.core.opendoar.importer.connector.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Withdrawal {

    @SerializedName("policy")
    @Expose
    private String policy;
    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("withdrawn_items")
    @Expose
    private WithdrawnItems withdrawnItems;

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public WithdrawnItems getWithdrawnItems() {
        return withdrawnItems;
    }

    public void setWithdrawnItems(WithdrawnItems withdrawnItems) {
        this.withdrawnItems = withdrawnItems;
    }
}
