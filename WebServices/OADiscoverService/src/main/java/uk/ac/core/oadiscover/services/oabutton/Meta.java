
package uk.ac.core.oadiscover.services.oabutton;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class Meta {

    @SerializedName("article")
    @Expose
    private Article article;

    @SerializedName("cache")
    @Expose
    private Boolean cache;
    @SerializedName("refresh")
    @Expose
    private Long refresh;

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public Long getRefresh() {
        return refresh;
    }

    public void setRefresh(Long refresh) {
        this.refresh = refresh;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Meta meta = (Meta) o;

        if (!Objects.equals(article, meta.article)) return false;
        if (!Objects.equals(cache, meta.cache)) return false;
        return Objects.equals(refresh, meta.refresh);
    }

    @Override
    public int hashCode() {
        int result = article != null ? article.hashCode() : 0;
        result = 31 * result + (cache != null ? cache.hashCode() : 0);
        result = 31 * result + (refresh != null ? refresh.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Meta{" +
                "article=" + article +
                ", cache=" + cache +
                ", refresh=" + refresh +
                '}';
    }
}
