package uk.ac.core.elasticsearch.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "#{@indexName}", type = "article", useServerConfiguration = true, createIndex = false)
@Setting(settingPath = "/elasticsearch/mappings/settings.json")
@Mapping(mappingPath = "/elasticsearch/mappings/articles.json")
public class BasicArticleMetadata {

    @Id
    private String id;

    private ElasticSearchRepositoryDocument repositoryDocument;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ElasticSearchRepositoryDocument getRepositoryDocument() {
        return repositoryDocument;
    }

    public void setRepositoryDocument(ElasticSearchRepositoryDocument repositoryDocument) {
        this.repositoryDocument = repositoryDocument;
    }

    public boolean hasText() {
        return repositoryDocument.getTextStatus() == 1;
    }
}