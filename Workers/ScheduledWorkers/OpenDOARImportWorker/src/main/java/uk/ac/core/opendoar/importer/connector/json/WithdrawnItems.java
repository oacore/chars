
package uk.ac.core.opendoar.importer.connector.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WithdrawnItems {

    @SerializedName("searchable")
    @Expose
    private String searchable;
    @SerializedName("url_retention")
    @Expose
    private String urlRetention;

    public String getSearchable() {
        return searchable;
    }

    public void setSearchable(String searchable) {
        this.searchable = searchable;
    }

    public String getUrlRetention() {
        return urlRetention;
    }

    public void setUrlRetention(String urlRetention) {
        this.urlRetention = urlRetention;
    }

}
