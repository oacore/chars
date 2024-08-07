package uk.ac.core.common.model.task.parameters;

/**
 *
 * @author lucasanastasiou
 */
public class ReindexItemTaskParameters extends SingleItemTaskParameters {

    private String indexName;

    public ReindexItemTaskParameters(String indexName, Integer article_id) {
        super(article_id);
        this.indexName = indexName;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
    
}
