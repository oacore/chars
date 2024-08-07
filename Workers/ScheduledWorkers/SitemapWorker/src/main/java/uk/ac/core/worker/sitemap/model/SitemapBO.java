package uk.ac.core.worker.sitemap.model;

import java.util.ArrayList;
import java.util.List;

public final class SitemapBO {

    private final List<String> urls = new ArrayList<>();

    public SitemapBO(String articleUrls) {
        this.urls.add(articleUrls);
    }

    public void addArticleUrls(String articleUrls) {
        urls.add(articleUrls);
    }

    int getArticleUrlsAmount() {
        return urls.size();
    }

    boolean isEmpty() {
        return urls.isEmpty();
    }

    public List<String> getUrls() {
        return urls;
    }
}