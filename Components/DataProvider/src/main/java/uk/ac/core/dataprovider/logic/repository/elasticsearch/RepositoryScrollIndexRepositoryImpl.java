package uk.ac.core.dataprovider.logic.repository.elasticsearch;

import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ScrolledPage;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;
import uk.ac.core.dataprovider.logic.entity.IndexedDataProvider;
import uk.ac.core.dataprovider.logic.repository.RepositoryScrollIndexService;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Repository
public class RepositoryScrollIndexRepositoryImpl implements RepositoryScrollIndexService {

    private static final String INDEX = "repositories";
    private static final String TYPE_NAME = "repository";

    private static final Logger LOG = LoggerFactory.getLogger(RepositoryScrollIndexRepositoryImpl.class);

    private final ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    public RepositoryScrollIndexRepositoryImpl(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }


    /***
     *
     * @TODO Refactor to use elasticsearchTemplate.stream() instead of Observer Pattern
     * @param consumer
     * @return
     */
    @Override
    public int findAllViaConsumer(Consumer<IndexedDataProvider> consumer) {
        ScrolledPage<IndexedDataProvider> page = this.elasticsearchTemplate.startScroll(100000, createMatchAllQuery(), IndexedDataProvider.class);
        AtomicInteger count = new AtomicInteger();
        while (page.hasContent()) {
            page.forEach(indexedDataProvider -> consumer.accept(indexedDataProvider));
            count.addAndGet((int) page.stream().count());
            page = elasticsearchTemplate.continueScroll(page.getScrollId(),100000, IndexedDataProvider.class);
        }
        return count.get();
    }

    private SearchQuery createMatchAllQuery(){
        return new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery()).build();
    }

    @Override
    public void deleteByIdIsNull() {
        // Where the inner field is null or non-existent
        String searchTerm = "!_exists_:id";
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setIndex(INDEX);
        deleteQuery.setType(TYPE_NAME);
        deleteQuery.setQuery(QueryBuilders.queryStringQuery(searchTerm));
        this.elasticsearchTemplate.delete(deleteQuery);
    }

    @Override
    public void refresh() {
        this.elasticsearchTemplate.refresh(INDEX);
    }
}
