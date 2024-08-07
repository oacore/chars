
package uk.ac.core.oadiscover.services.oabutton;

import java.util.List;
import java.util.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("match")
    @Expose
    private String match;
    @SerializedName("availability")
    @Expose
    private List<Availability> availability = null;
    @SerializedName("requests")
    @Expose
    private List<Object> requests = null;
    @SerializedName("accepts")
    @Expose
    private List<Object> accepts = null;
    @SerializedName("meta")
    @Expose
    private Meta meta;

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public List<Availability> getAvailability() {
        return availability;
    }

    public void setAvailability(List<Availability> availability) {
        this.availability = availability;
    }

    public List<Object> getRequests() {
        return requests;
    }

    public void setRequests(List<Object> requests) {
        this.requests = requests;
    }

    public List<Object> getAccepts() {
        return accepts;
    }

    public void setAccepts(List<Object> accepts) {
        this.accepts = accepts;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Data data = (Data) o;

        if (!Objects.equals(match, data.match)) return false;
        if (!Objects.equals(availability, data.availability)) return false;
        if (!Objects.equals(requests, data.requests)) return false;
        if (!Objects.equals(accepts, data.accepts)) return false;
        return Objects.equals(meta, data.meta);
    }

    @Override
    public int hashCode() {
        int result = match != null ? match.hashCode() : 0;
        result = 31 * result + (availability != null ? availability.hashCode() : 0);
        result = 31 * result + (requests != null ? requests.hashCode() : 0);
        result = 31 * result + (accepts != null ? accepts.hashCode() : 0);
        result = 31 * result + (meta != null ? meta.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Data{" +
                "match='" + match + '\'' +
                ", availability=" + availability +
                ", requests=" + requests +
                ", accepts=" + accepts +
                ", meta=" + meta +
                '}';
    }
}
