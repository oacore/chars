/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.model;

import javax.persistence.Table;
import org.hibernate.annotations.Entity;

/**
 *
 * @author samuel
 */
@Table(name = "repository_source_statistics")
public class RepositorySourceStatistics {

    private Integer idRepository;
    private Integer metadataCount;
    private Integer metadataNonDeletedCount;
    private Integer metadataWithAttachmentCount;
    private Integer fulltextCount;

    public RepositorySourceStatistics(Integer idRepository) {
        this.idRepository = idRepository;
    }
    
    public Integer getIdRepository() {
        return idRepository;
    }

    public void setIdRepository(Integer idRepository) {
        this.idRepository = idRepository;
    }

    public Integer getMetadataCount() {
        return metadataCount;
    }

    public void setMetadataCount(Integer metadataCount) {
        this.metadataCount = metadataCount;
    }

    public Integer getMetadataNonDeletedCount() {
        return metadataNonDeletedCount;
    }

    public void setMetadataNonDeletedCount(Integer metadataNonDeletedCount) {
        this.metadataNonDeletedCount = metadataNonDeletedCount;
    }

    public Integer getMetadataWithAttachmentCount() {
        return metadataWithAttachmentCount;
    }

    public void setMetadataWithAttachmentCount(Integer metadataWithAttachmentCount) {
        this.metadataWithAttachmentCount = metadataWithAttachmentCount;
    }

    public Integer getFulltextCount() {
        return fulltextCount;
    }

    public void setFulltextCount(Integer fulltextCount) {
        this.fulltextCount = fulltextCount;
    }
    
    
}
