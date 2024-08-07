package uk.ac.core.worker.sitemap.generation.entity.sitemap;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;

@JacksonXmlRootElement(localName = "urlset", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
public class SitemapFile {

    @JacksonXmlProperty(localName = "url", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<SitemapUrlProperty> urls;

    public SitemapFile() {
    }

    public SitemapFile(List<SitemapUrlProperty> urls) {
        this.urls = urls;
    }

    public List<SitemapUrlProperty> getUrls() {
        return urls;
    }

    public void setUrls(List<SitemapUrlProperty> urls) {
        this.urls = urls;
    }
}