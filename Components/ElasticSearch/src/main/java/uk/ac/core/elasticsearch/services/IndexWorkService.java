package uk.ac.core.elasticsearch.services;

import uk.ac.core.elasticsearch.entities.ElasticSearchWorkMetadata;
import uk.ac.core.elasticsearch.exceptions.IndexException;

public interface IndexWorkService {

    void indexWork(String index, ElasticSearchWorkMetadata elasticSearchWorkMetadata) throws IndexException;
}
