package uk.ac.core.elasticsearch.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import uk.ac.core.elasticsearch.entities.ElasticSearchArticleMetadata;
import uk.ac.core.elasticsearch.entities.ElasticSearchWorkMetadata;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface WorksMetadataRepository extends ElasticsearchRepository<ElasticSearchWorkMetadata, Integer> {

    /**
     * Retrieve list of articles by the given oai Oai should be escaped, if it
     * contains :(colon) char creates problem in the query json See example of
     * how to escape in ArticleMetadataRepositoryTest.java
     *
     * @param oai
     * @return
     */
    List<ElasticSearchWorkMetadata> findListByOaiIds(String oai);

    ElasticSearchWorkMetadata findFirstById(Integer workId);

    List<ElasticSearchWorkMetadata> findListByIdentifiers(Set<String> possibleUrls);

    Optional<ElasticSearchWorkMetadata> findOneByCoreIds(String coreId);
}
