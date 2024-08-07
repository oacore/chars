package uk.ac.core.elasticsearch.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.core.elasticsearch.entities.ElasticSearchArticleMetadata;
import uk.ac.core.elasticsearch.repositories.ArticleMetadataDAO;
import uk.ac.core.elasticsearch.repositories.ArticleMetadataRepository;
import uk.ac.core.elasticsearch.services.converter.ArticleConverter;
import uk.ac.core.elasticsearch.services.model.CompactArticleBO;
import uk.ac.core.elasticsearch.services.model.CompleteArticleBO;
import java.util.List;
import java.util.function.Consumer;

@Service
public class ArticleMetadataServiceImpl implements ArticleMetadataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleMetadataServiceImpl.class);

    private final ArticleMetadataDAO articleMetadataDAO;
    private final ArticleMetadataRepository articleMetadataRepository;

    public ArticleMetadataServiceImpl(ArticleMetadataDAO articleMetadataDAO, ArticleMetadataRepository articleMetadataRepository) {
        this.articleMetadataDAO = articleMetadataDAO;
        this.articleMetadataRepository = articleMetadataRepository;
    }

    @Override
    public void receiveEnabledArticles(Consumer<List<CompactArticleBO>> receiver) {
        LOGGER.debug("Scrolling of enabled articles started.");
        articleMetadataDAO.scrollEnabledArticles(receiver);
        LOGGER.debug("Scrolling of enabled articles has finished.");
    }

    @Override
    public CompleteArticleBO getArticleMetadata(String id) {
        return ArticleConverter.convertToCompleteArticleBO(articleMetadataRepository.findOneById(id));
    }


    @Override
    public Long countAllDownloadedArticles() {
        return articleMetadataDAO.countAllDownloadedDocuments();
    }
}
