package uk.ac.core.worker.sitemap.factory;

import java.util.ArrayList;
import java.util.List;

public final class ArticleUrls {

    private List<String> articleUrls = new ArrayList<>();

    public ArticleUrls(List<String> articleUrls) {
        this.articleUrls = articleUrls;
    }

    public ArticleUrls() {
    }

    public List<String> asList() {
        return articleUrls;
    }

    public void setArticleUrls(List<String> articleUrls) {
        this.articleUrls = articleUrls;
    }

    public int size() {
        return articleUrls.size();
    }

    public void clear() {
        this.articleUrls.clear();
    }

    public void add(String url) {
        this.articleUrls.add(url);
    }

    public void addAll(ArticleUrls urls) {
        this.articleUrls.addAll(urls.asList());
    }
}