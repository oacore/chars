package uk.ac.core.worker.sitemap.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.core.worker.sitemap.collection.ESDumpDeserializer;
import uk.ac.core.worker.sitemap.converter.XmlSitemapConverter;
import uk.ac.core.worker.sitemap.exception.SitemapException;
import uk.ac.core.worker.sitemap.factory.ArticleUrls;
import uk.ac.core.worker.sitemap.generation.SitemapGenerator;
import uk.ac.core.worker.sitemap.generation.entity.sitemap.SitemapFile;
import java.io.IOException;
import java.util.function.Consumer;

public class SitemapServiceImpl implements SitemapService {

    private static final String sitemapAbsolutePath = "/data/remote/core/filesystem/sitemaps/";
    private static final String esDumpFileAbsolutePath = "/data/remote/dumps/sitemaps/articles_data.json";

    private static final Logger LOGGER = LoggerFactory.getLogger(SitemapServiceImpl.class);

    private final SitemapGenerator sitemapGenerator;
    private final ESDumpDeserializer esDumpDeserializer;

    public SitemapServiceImpl(ESDumpDeserializer esDumpDeserializer, SitemapGenerator sitemapGenerator) {
        this.sitemapGenerator = sitemapGenerator;
        this.esDumpDeserializer = esDumpDeserializer;
    }

    @Override
    public void generateSitemaps() throws SitemapException {
        try {

            esDumpDeserializer.deserialize(esDumpFileAbsolutePath, persistSitemaps());
            sitemapGenerator.generateIndexFile(sitemapAbsolutePath);

        } catch (IOException ex) {
            throw new SitemapException(ex.getMessage());
        }
    }
    
    private Consumer<ArticleUrls> persistSitemaps() {
        return articleUrls -> {
            try {
                sitemapGenerator.generateOne(prepareSitemapsForGeneration(articleUrls), sitemapAbsolutePath);
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new RuntimeException("An error occurred during the writing of sitemaps.");
            }
        };

    }

    private SitemapFile prepareSitemapsForGeneration(ArticleUrls urls) {
        return XmlSitemapConverter.toXmlFile(urls);
    }
}