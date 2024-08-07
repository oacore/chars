package uk.ac.core.dto;

import java.util.List;

public class ExcludeDto {
    private Integer excludedDocumentId;

    private List<Integer> documentIds;

    private String indexName;

    public ExcludeDto() {
    }

    public Integer getExcludedDocumentId() {
        return excludedDocumentId;
    }

    public void setExcludedDocumentId(Integer excludedDocumentId) {
        this.excludedDocumentId = excludedDocumentId;
    }

    public List<Integer> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<Integer> documentIds) {
        this.documentIds = documentIds;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
}
