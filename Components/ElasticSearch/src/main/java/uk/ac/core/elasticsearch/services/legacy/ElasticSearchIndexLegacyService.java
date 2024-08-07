package uk.ac.core.elasticsearch.services.legacy;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.legacy.ArticleMetadata;
import uk.ac.core.database.service.document.ArticleMetadataDAO;
import uk.ac.core.database.service.document.DocumentTypeDAO;
import uk.ac.core.elasticsearch.entities.ElasticSearchArticleMetadata;
import uk.ac.core.elasticsearch.exceptions.IndexException;
import uk.ac.core.elasticsearch.services.ArticleMetadataService;
import uk.ac.core.elasticsearch.services.util.ElasticSearchArticleMetadataBuilder;
import uk.ac.core.enrichments.CleanAuthorName;

/**
 * All new elasticsearch related business logic should be put in {@link ArticleMetadataService }
 */
@Service
public class ElasticSearchIndexLegacyService implements IndexLegacyService {

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    ArticleMetadataDAO articleMetadataDAO;

    @Autowired
    ElasticSearchArticleMetadataBuilder elasticSearchArticleMetadataBuilder;

    @Autowired
    DocumentTypeDAO documentTypeDAO;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ElasticSearchIndexLegacyService.class);

    public ElasticSearchIndexLegacyService() {

    }

    public ElasticSearchIndexLegacyService(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public void indexArticle(final String index, final ElasticSearchArticleMetadata elasticSearchArticleMetadata) throws IndexException {

        if (index == null || elasticSearchArticleMetadata == null) {
            throw new IndexException("indexname or elasticsearchArticleMetadata is null");
        }

        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setIndexName(index);
        indexQuery.setType("article");
        indexQuery.setObject(elasticSearchArticleMetadata);
        indexQuery.setId("" + elasticSearchArticleMetadata.getId());

        long startTime = System.currentTimeMillis();
        elasticsearchTemplate.index(indexQuery);
        long timeElapsed = System.currentTimeMillis() - startTime;
        logger.info("Index for # {} took {}", elasticSearchArticleMetadata.getId(), timeElapsed);        
    }

    @Override
    public void indexArticle(final String index, final Integer coreInternalId) throws IndexException {
        logger.info("Indexing #" + coreInternalId + " in " + index);
        long startTime = System.currentTimeMillis();
        ArticleMetadata articleMetadata = articleMetadataDAO.getFullArticleMetadata(coreInternalId);
        long timeElapsed = System.currentTimeMillis() - startTime;
        logger.info("DB Query for # {} took {}", coreInternalId, timeElapsed);

        // Check if ArticleMetadata is null. We get a list of document ID's from the document table. This list
        // we use to get the xml from document_metadata. Is is possible that a document exists in
        // the document table and not in document_metadata. This may be because the document was
        // removed before we had a chance to extract it.
        if (articleMetadata == null) {
            logger.warn("Could not find document's ID in document_metadata - file may be been removed from repository/ Doc ID: " + coreInternalId);
            throw new IndexException("Cannot retrieve article metadata" + coreInternalId);
        }
        this.indexArticle(index, articleMetadata);

    }

    @Override
    public void indexArticle(String index, ArticleMetadata articleMetadata) throws IndexException {
        if (articleMetadata == null) {
            throw new IndexException("articleMetadata is null");
        }

        // and set the document type
        long startTime1 = System.currentTimeMillis();
        articleMetadata.setDocumentType(documentTypeDAO.getDocumentType(articleMetadata.getId()));
        articleMetadata.setDocumentTypeConfidence(documentTypeDAO.getDocumentTypeConfidence(articleMetadata.getId()));
        long timeElapsed1 = System.currentTimeMillis() - startTime1;
        logger.info("DB query takes {}", timeElapsed1);

        // Clean title for public enrichment CORE-1534
        // TODO: Place enrichment/cleaners in a seperate enrichment worker
        articleMetadata.setAuthors(new CleanAuthorName(articleMetadata).cleanAuthors());
        long startTime = System.currentTimeMillis();
        ElasticSearchArticleMetadata elasticSearchArticleMetadata = elasticSearchArticleMetadataBuilder.convertArticleMetadataToElasticDocument(articleMetadata);
        long timeElapsed = System.currentTimeMillis() - startTime;
        logger.info("Converting article mtadata to elastic document takes {}", timeElapsed);
        this.indexArticle(index, elasticSearchArticleMetadata);
    }
}
