package uk.ac.core.elasticsearch.caching;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class RecommendationCachingService {

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;
    
    final long MAX_AGE = 2592000000L;//a month

    public RecommendationCachedObject fetch(String key) {

        GetQuery getQuery = new GetQuery();
        getQuery.setId(key);

        RecommendationCachedObject cachedObject = elasticsearchTemplate.queryForObject(getQuery, RecommendationCachedObject.class);
        
        if (cachedObject != null) {
            long ttl = System.currentTimeMillis() - cachedObject.getTime();
            /**
             * Check if cache entry is older than one month
             */
            if (ttl > MAX_AGE) {
                return null;
            }
        }
        return cachedObject;
    }

    public void save(String key, String source_url, String data) {
        long time = System.currentTimeMillis();

        RecommendationCachedObject cachedObject = new RecommendationCachedObject(key, data, time, source_url);

        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setIndexName("caching-recommendation");
        indexQuery.setType("_doc");
        indexQuery.setId(key);
        indexQuery.setObject(cachedObject);

        elasticsearchTemplate.index(indexQuery);
    }
    
    public void invalidate(String source_url) {    
        
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setIndex("caching-recommendation");
        deleteQuery.setType("_doc");
        QueryBuilder qb = QueryBuilders.termQuery("source_url", source_url);
        deleteQuery.setQuery(qb);
        System.out.println("delete query:  = " + qb.toString());
        elasticsearchTemplate.delete(deleteQuery);
    }
}
