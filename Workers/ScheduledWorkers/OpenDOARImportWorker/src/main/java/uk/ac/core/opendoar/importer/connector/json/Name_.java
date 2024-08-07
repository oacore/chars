
package uk.ac.core.opendoar.importer.connector.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Name_ {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("preferred")
    @Expose
    private String preferred;
    @SerializedName("language")
    @Expose
    private String language;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreferred() {
        return preferred;
    }

    public void setPreferred(String preferred) {
        this.preferred = preferred;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


}
