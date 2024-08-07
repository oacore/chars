package uk.ac.core.elasticsearch.repositories;

import uk.ac.core.elasticsearch.services.model.CompactArticleBO;
import java.util.List;
import java.util.function.Consumer;

/**
 * DAO for cases, which are not supported by
 * {@link org.springframework.data.elasticsearch.repository.ElasticsearchRepository}.
 */
public interface ArticleMetadataDAO {

    /**
     * Counts all documents(regardless of the status).
     *
     * @return number of documents
     */
    Long countAllDownloadedDocuments();

    /**
     * Scrolls through the all enabled articles and submits every chunk of fetched data to the given callback handler.
     *
     * @param handler handler
     *
     * @implNote the usage of closeable iterator implementation(https://docs.spring.io/spring-data/elasticsearch/docs/3.1.x/reference/html/#elasticsearch.scroll) for scroll is preferable,
     * but it's impossible in that case to set expiry time for search context, which expires during the scroll.
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html#scroll-search-context,
     * https://stackoverflow.com/questions/51336462/elasticsearch-javaapi-searchscroll-search-context-missing-exception-reason
     */
    void scrollEnabledArticles(Consumer<List<CompactArticleBO>> handler);
}