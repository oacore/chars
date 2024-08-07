package uk.ac.core.elasticsearch.services.legacy;

import uk.ac.core.common.model.legacy.ArticleMetadata;
import uk.ac.core.elasticsearch.entities.ElasticSearchArticleMetadata;
import uk.ac.core.elasticsearch.exceptions.IndexException;

public interface IndexLegacyService {

    void indexArticle(String index, ElasticSearchArticleMetadata elasticSearchArticleMetadata) throws IndexException;

    void indexArticle(String index, Integer coreInternalId) throws IndexException;
    
    void indexArticle(String index, ArticleMetadata articleMetadata) throws IndexException;
}
