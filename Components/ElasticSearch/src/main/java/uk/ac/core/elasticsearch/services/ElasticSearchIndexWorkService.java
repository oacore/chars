package uk.ac.core.elasticsearch.services;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;
import uk.ac.core.elasticsearch.entities.ElasticSearchWorkMetadata;
import uk.ac.core.elasticsearch.exceptions.IndexException;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class ElasticSearchIndexWorkService implements IndexWorkService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ElasticSearchIndexWorkService.class);

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @PostConstruct
    public void init() {
        ElasticsearchPersistentEntity persistentEntityFor = elasticsearchTemplate.getPersistentEntityFor(ElasticSearchWorkMetadata.class);
        logger.info("Creating mapping for ElasticSearchWorkMetadata. Index name: {}, type={}",
                persistentEntityFor.getIndexName(), persistentEntityFor.getIndexType());

        if(!elasticsearchTemplate.indexExists(persistentEntityFor.getIndexName())) {
            elasticsearchTemplate.createIndex(persistentEntityFor.getIndexName());
        }

        elasticsearchTemplate.putMapping(ElasticSearchWorkMetadata.class);
        logger.info("Mapping for ElasticSearchWorkMetadata successfully created: {}",
                elasticsearchTemplate.getPersistentEntityFor(ElasticSearchWorkMetadata.class).getIndexName());
    }


    @Override
    public void indexWork(String index, ElasticSearchWorkMetadata elasticSearchWorkMetadata) throws IndexException {
        if (index != null && elasticSearchWorkMetadata != null) {
            IndexQuery indexQuery = new IndexQuery();
            indexQuery.setIndexName(index);
            indexQuery.setType("works");
            indexQuery.setObject(elasticSearchWorkMetadata);
            indexQuery.setId("" + elasticSearchWorkMetadata.getId());
            elasticsearchTemplate.index(indexQuery);
        } else {
            throw new IndexException("Indexname or ElasticSearchWorkMetadata is null");
        }
    }
}
