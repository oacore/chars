
package uk.ac.core.oadiscover.services.epmc;

import java.util.List;
import java.util.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PubTypeList {

    @SerializedName("pubType")
    @Expose
    private List<String> pubType = null;

    public List<String> getPubType() {
        return pubType;
    }

    public void setPubType(List<String> pubType) {
        this.pubType = pubType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PubTypeList that = (PubTypeList) o;

        return Objects.equals(pubType, that.pubType);
    }

    @Override
    public int hashCode() {
        return pubType != null ? pubType.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PubTypeList{" +
                "pubType=" + pubType +
                '}';
    }
}
