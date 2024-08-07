/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.common.model.task.parameters;


/**
 *
 * @author samuel
 */
public class PurgeDocumentParameters extends SingleItemTaskParameters {

    private int repositoryId;

    /**
     *
     * @param articleId The Article ID of the item to index
     */
    public PurgeDocumentParameters(int articleId, int repositoryId) {
        super(articleId);
        this.repositoryId = repositoryId;
    }

    public int getRepositoryId() {
        return repositoryId;
    }
}
