package uk.ac.core.worker.sitemap.generation.entity.index;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;

@JacksonXmlRootElement(localName = "sitemapindex", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
public class SitemapIndexFile {

    @JacksonXmlProperty(namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<SitemapProperty> sitemap;

    public SitemapIndexFile() {
    }

    public SitemapIndexFile(List<SitemapProperty> sitemap) {
        this.sitemap = sitemap;
    }

    public List<SitemapProperty> getSitemap() {
        return sitemap;
    }

    public void setSitemap(List<SitemapProperty> sitemap) {
        this.sitemap = sitemap;
    }
}
