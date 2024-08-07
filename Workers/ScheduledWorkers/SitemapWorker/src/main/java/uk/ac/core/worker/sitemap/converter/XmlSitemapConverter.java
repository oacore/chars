package uk.ac.core.worker.sitemap.converter;

import uk.ac.core.worker.sitemap.factory.ArticleUrls;
import uk.ac.core.worker.sitemap.generation.entity.sitemap.SitemapFile;
import uk.ac.core.worker.sitemap.generation.entity.sitemap.SitemapUrlProperty;
import java.util.ArrayList;
import java.util.List;

public final class XmlSitemapConverter {

    private XmlSitemapConverter(){

    }

    public static SitemapFile toXmlFile(ArticleUrls articleUrls) {
        List<SitemapUrlProperty> urls = new ArrayList<>();
        for (String url : articleUrls.asList()) {
            urls.add(new SitemapUrlProperty(url));
        }
        return new SitemapFile(urls);
    }
}