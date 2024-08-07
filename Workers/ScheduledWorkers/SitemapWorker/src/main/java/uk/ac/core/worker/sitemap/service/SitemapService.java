package uk.ac.core.worker.sitemap.service;

import uk.ac.core.worker.sitemap.exception.SitemapException;

public interface SitemapService {
    void generateSitemaps() throws SitemapException;
}