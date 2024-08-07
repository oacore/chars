
package uk.ac.core.opendoar.importer.connector.json;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MetadataPolicy {

    @SerializedName("access")
    @Expose
    private String access;
    @SerializedName("non_profit_reuse_conditions")
    @Expose
    private List<String> nonProfitReuseConditions = null;
    @SerializedName("non_profit_reuse")
    @Expose
    private String nonProfitReuse;
    @SerializedName("url")
    @Expose
    private List<String> url = null;

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public List<String> getNonProfitReuseConditions() {
        return nonProfitReuseConditions;
    }

    public void setNonProfitReuseConditions(List<String> nonProfitReuseConditions) {
        this.nonProfitReuseConditions = nonProfitReuseConditions;
    }

    public String getNonProfitReuse() {
        return nonProfitReuse;
    }

    public void setNonProfitReuse(String nonProfitReuse) {
        this.nonProfitReuse = nonProfitReuse;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

}
