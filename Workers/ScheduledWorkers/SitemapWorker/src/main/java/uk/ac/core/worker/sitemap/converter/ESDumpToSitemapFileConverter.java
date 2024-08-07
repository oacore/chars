package uk.ac.core.worker.sitemap.converter;

import uk.ac.core.elasticsearch.services.model.CompactArticleBO;
import uk.ac.core.worker.sitemap.collection.entity.ESDump;
import uk.ac.core.worker.sitemap.factory.ArticleUrls;
import uk.ac.core.worker.sitemap.factory.ArticleUrlsFactory;

public class ESDumpToSitemapFileConverter {

    public ESDumpToSitemapFileConverter() {
    }

    public ArticleUrls toArticleUrls(ESDump esDump) {
        return ArticleUrlsFactory.newInstance(toArticleBO(esDump));
    }

    private CompactArticleBO toArticleBO(ESDump esDump) {
        return ArticleBOConverter.convertToArticleBO(esDump);
    }
}