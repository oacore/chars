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
public class DocumentDownloadParameters extends RepositoryTaskParameters {

    private Long size;
    private boolean singleItem;
    private boolean slownessCheck;

    public DocumentDownloadParameters(int repositoryId) {
        super(repositoryId);
        this.singleItem = false;
        this.slownessCheck = true;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public boolean isSingleItem() {
        return singleItem;
    }

    public void setSingleItem(boolean singleItem) {
        this.singleItem = singleItem;
    }

    public boolean isSlownessCheck() {
        return slownessCheck;
    }

    public void setSlownessCheck(boolean slownessCheck) {
        this.slownessCheck = slownessCheck;
    }
}
