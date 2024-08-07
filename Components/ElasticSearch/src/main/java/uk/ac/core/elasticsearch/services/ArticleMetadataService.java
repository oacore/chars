package uk.ac.core.elasticsearch.services;

import uk.ac.core.elasticsearch.services.model.CompactArticleBO;
import uk.ac.core.elasticsearch.services.model.CompleteArticleBO;
import java.util.List;
import java.util.function.Consumer;

/**
 * Article Metadata Service.
 */
public interface ArticleMetadataService {

    /**
     * Receives all enabled articles split in chunks via given callback receiver.
     * @param receiver callback, which receives the partitioned data
     *
     */
    void receiveEnabledArticles(Consumer<List<CompactArticleBO>> receiver);

    CompleteArticleBO getArticleMetadata(String id);

    Long countAllDownloadedArticles();
}
