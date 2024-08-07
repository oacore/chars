package uk.ac.core.worker.sitemap.converter;

import uk.ac.core.elasticsearch.entities.ElasticSearchArticleMetadata;
import uk.ac.core.elasticsearch.services.model.CompactArticleBO;
import uk.ac.core.elasticsearch.services.model.CompleteArticleBO;
import uk.ac.core.worker.sitemap.collection.entity.ESDump;

public final class ArticleBOConverter {

    private ArticleBOConverter() {
    }

    public static CompactArticleBO convertToArticleBO(ESDump esDump) {
        CompactArticleBO articleBO = new CompactArticleBO();
        articleBO.setDocumentId(esDump.getId());
        articleBO.setFullText(esDump.getTextStatus());
        return articleBO;
    }


}