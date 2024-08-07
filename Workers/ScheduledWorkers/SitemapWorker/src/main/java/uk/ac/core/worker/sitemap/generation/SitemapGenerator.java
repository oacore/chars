package uk.ac.core.worker.sitemap.generation;

import uk.ac.core.worker.sitemap.generation.entity.sitemap.SitemapFile;
import java.io.IOException;

public interface SitemapGenerator {

    /**
     * Generates sitemaps into the given directory.
     *  @param xmlFile      xml sitemap file to be generated
     * @param directoryPath directory path
     */
    void generateOne(SitemapFile xmlFile, String directoryPath) throws IOException;

    /**
     * Generates sitemaps index file.
     *
     * @implNote This method require {@link SitemapGenerator#generateOne(SitemapFile, String)} to be called beforehand, though
     * it doesn't confirm that the sitemaps exist.
     * At the end of execution, it flushes info about generated sitemaps, meaning,
     * calling {@link SitemapGenerator#generateOne(SitemapFile, String)} will overwrite existing sitemaps, if the directory path
     * is the same.
     *
     * @throws IOException if {@link SitemapGenerator#generateOne(SitemapFile, String)} wasn't called beforehand.
     * @param directoryPath
     */
    void generateIndexFile(String directoryPath) throws IOException;
}