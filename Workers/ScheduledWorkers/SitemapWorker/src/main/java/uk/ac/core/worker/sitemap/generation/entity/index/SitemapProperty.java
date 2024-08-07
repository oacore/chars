package uk.ac.core.worker.sitemap.generation.entity.index;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class SitemapProperty {

    @JacksonXmlProperty(localName = "loc", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    private String location;

    @JacksonXmlProperty(localName = "lastmod", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    private String lastModificationTime;

    public SitemapProperty() {
    }

    public SitemapProperty(String location, String lastModificationTime) {
        this.location = location;
        this.lastModificationTime = lastModificationTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}