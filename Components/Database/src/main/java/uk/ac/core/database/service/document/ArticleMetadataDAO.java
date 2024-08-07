package uk.ac.core.database.service.document;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import uk.ac.core.common.model.legacy.ArticleMetadata;

/**
 *
 * @author lucasanastasiou
 */
public interface ArticleMetadataDAO {

    ArticleMetadata getFullArticleMetadata(Integer articleId);

    /**
     * Retrieve from database ArticleMetadata
     *
     * @param idDocument
     * @return
     */
    ArticleMetadata getArticleMetadata(Integer articleId);
    
    String getArticleTitle(Integer articleId);

    Optional<String> getArticleLanguage(Integer articleId);

    Timestamp getDepositedDateStamp(Integer articleId);
    
    void addArticleMetadata(ArticleMetadata articleMetadata);

    Boolean addRawArticleMetadata(ArticleMetadata articleMetadata);

    void flushArticles();

    void flushRawMetadata();
}
