package uk.ac.core.common.model.task.parameters;

/**
 *
 * @author lucasanastasiou
 */
public class SingleItemTaskParameters extends TaskParameters {

    private Integer article_id;

    public SingleItemTaskParameters() {
    }

    public SingleItemTaskParameters(Integer article_id) {
        this.article_id = article_id;
    }

    public Integer getArticle_id() {
        return article_id;
    }

    public void setArticle_id(Integer article_id) {
        this.article_id = article_id;
    }

}
