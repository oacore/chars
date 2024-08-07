package uk.ac.core.worker.sitemap.generation.entity.sitemap;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class SitemapUrlProperty {

    @JacksonXmlProperty(localName = "loc", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    private String location;

    public SitemapUrlProperty() {
    }

    public SitemapUrlProperty(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}