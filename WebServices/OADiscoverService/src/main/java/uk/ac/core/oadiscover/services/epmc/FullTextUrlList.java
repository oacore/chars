
package uk.ac.core.oadiscover.services.epmc;

import java.util.List;
import java.util.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FullTextUrlList {

    @SerializedName("fullTextUrl")
    @Expose
    private List<FullTextUrl> fullTextUrl = null;

    public List<FullTextUrl> getFullTextUrl() {
        return fullTextUrl;
    }

    public void setFullTextUrl(List<FullTextUrl> fullTextUrl) {
        this.fullTextUrl = fullTextUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullTextUrlList that = (FullTextUrlList) o;

        return Objects.equals(fullTextUrl, that.fullTextUrl);
    }

    @Override
    public int hashCode() {
        return fullTextUrl != null ? fullTextUrl.hashCode() : 0;
    }
}
