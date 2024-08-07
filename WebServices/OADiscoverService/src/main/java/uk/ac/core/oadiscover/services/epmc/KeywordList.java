
package uk.ac.core.oadiscover.services.epmc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Objects;

public class KeywordList {

    @SerializedName("keyword")
    @Expose
    private List<String> keyword = null;

    public List<String> getKeyword() {
        return keyword;
    }

    public void setKeyword(List<String> keyword) {
        this.keyword = keyword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeywordList that = (KeywordList) o;

        return Objects.equals(keyword, that.keyword);
    }

    @Override
    public int hashCode() {
        return keyword != null ? keyword.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "KeywordList{" +
                "keyword=" + keyword +
                '}';
    }
}
