package uk.ac.core.elasticsearch.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import uk.ac.core.elasticsearch.entities.ElasticSearchArticleMetadata;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author lucasanastasiou
 */
public interface ArticleMetadataRepository extends ElasticsearchRepository<ElasticSearchArticleMetadata, String> {

    /**
     * Retrieve article by the given oai Oai should be escaped, if it contains
     * :(colon) char creates problem in the query json See example of how to
     * escape in ArticleMetadataRepositoryTest.java
     *
     * @param oai
     * @return
     */
    ElasticSearchArticleMetadata findByOai(String oai);


    /**
     * Retrieve list of articles by the given oai Oai should be escaped, if it
     * contains :(colon) char creates problem in the query json See example of
     * how to escape in ArticleMetadataRepositoryTest.java
     *
     * @param oai
     * @return
     */
    List<ElasticSearchArticleMetadata> findListByOai(String oai);

    /**
     * Retrieve article by the given url. Url should be escaped, :(colon) and
     * /(forward slash) cause a problem in the query json See example of how to
     * escape in ArticleMetadataRepositoryTest.java
     *
     * @param url
     * @return
     */
    List<ElasticSearchArticleMetadata> findByUrls(String url);

    List<ElasticSearchArticleMetadata> findByUrlsIn(Collection<String> urls);

    Long countByFullText(String value);

    Long countByDeleted(String value);
    
    ElasticSearchArticleMetadata findOneById(String id);

    List<ElasticSearchArticleMetadata> findByDoi(String doi);
}