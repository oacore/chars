/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.common.model.task.parameters;

import java.util.Optional;
import uk.ac.core.common.model.article.DeletedStatus;


/**
 *
 * @author samuel
 */
public class ItemIndexParameters extends SingleItemTaskParameters {

    private Optional<DeletedStatus> deletedStatus;

    private String indexName;
    
    /**
     * Creates a Item Index Task     
     * @param articleId The Article ID of the item to index
     */
    public ItemIndexParameters(int articleId) {
        this(articleId, Optional.empty());
    }
    
    /**
     * 
     * @param articleId The Article ID of the item to index     
     * @param status The DeletedStatus of the article. 
     */
    public ItemIndexParameters(int articleId, Optional<DeletedStatus> status) {
        super(articleId);
        this.deletedStatus = status;
    }

    public Optional<DeletedStatus> getDeletedStatus() {
        // Patch legacy Json object where getDeletedStatus does not exist        
        return deletedStatus==null ? Optional.empty() : deletedStatus;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
}
