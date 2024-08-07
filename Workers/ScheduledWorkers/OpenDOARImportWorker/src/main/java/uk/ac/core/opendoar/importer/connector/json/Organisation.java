
package uk.ac.core.opendoar.importer.connector.json;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Organisation {

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("name")
    @Expose
    private List<Name_> name = null;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Name_> getName() {
        return name;
    }

    public void setName(List<Name_> name) {
        this.name = name;
    }


}
