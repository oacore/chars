
package uk.ac.core.oadiscover.services.epmc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class FullTextUrl {

    @SerializedName("availability")
    @Expose
    private String availability;
    @SerializedName("availabilityCode")
    @Expose
    private String availabilityCode;
    @SerializedName("documentStyle")
    @Expose
    private String documentStyle;
    @SerializedName("site")
    @Expose
    private String site;
    @SerializedName("url")
    @Expose
    private String url;

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getAvailabilityCode() {
        return availabilityCode;
    }

    public void setAvailabilityCode(String availabilityCode) {
        this.availabilityCode = availabilityCode;
    }

    public String getDocumentStyle() {
        return documentStyle;
    }

    public void setDocumentStyle(String documentStyle) {
        this.documentStyle = documentStyle;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullTextUrl that = (FullTextUrl) o;

        if (!Objects.equals(availability, that.availability)) return false;
        if (!Objects.equals(availabilityCode, that.availabilityCode))
            return false;
        if (!Objects.equals(documentStyle, that.documentStyle))
            return false;
        if (!Objects.equals(site, that.site)) return false;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        int result = availability != null ? availability.hashCode() : 0;
        result = 31 * result + (availabilityCode != null ? availabilityCode.hashCode() : 0);
        result = 31 * result + (documentStyle != null ? documentStyle.hashCode() : 0);
        result = 31 * result + (site != null ? site.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FullTextUrl{" +
                "availability='" + availability + '\'' +
                ", availabilityCode='" + availabilityCode + '\'' +
                ", documentStyle='" + documentStyle + '\'' +
                ", site='" + site + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
