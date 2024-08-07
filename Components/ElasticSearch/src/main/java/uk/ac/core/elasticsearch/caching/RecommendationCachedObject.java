package uk.ac.core.elasticsearch.caching;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

/**
 *
 * @author lucasanastasiou
 */
@Document(indexName = "caching-recommendation", type = "_doc",createIndex = false)
//@Mapping(mappingPath = "/elasticsearch/mappings/recommendation.json")
public class RecommendationCachedObject extends CachedObject{

    @Field(type=FieldType.Keyword)
    private String source_url;

    public RecommendationCachedObject(String id, String data, long time, String source_url) {
        super(id, data, time);
        this.source_url = source_url;        
    }

    public RecommendationCachedObject() {
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }
    
    
    
    
}
