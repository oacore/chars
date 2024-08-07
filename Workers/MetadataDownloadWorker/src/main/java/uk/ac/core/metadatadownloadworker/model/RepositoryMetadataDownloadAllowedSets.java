/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.metadatadownloadworker.model;

import org.springframework.data.annotation.Id;

/**
 *
 * @author samuel
 */
public class RepositoryMetadataDownloadAllowedSets {
    
    @Id
    private Long id;
    
    private Integer id_repository;
    
    private String setSpec;
    
    private String setName;

    public RepositoryMetadataDownloadAllowedSets(Integer id_repository, String setSpec, String setName) {
        this.id_repository = id_repository;
        this.setSpec = setSpec;
        this.setName = setName;
    }

    public void setId(Long id) {
        this.id = id;
    }   
    
    public Long getId() {
        return id;
    }

    public Integer getId_repository() {
        return id_repository;
    }

    public void setId_repository(Integer id_repository) {
        this.id_repository = id_repository;
    }

    public String getSetSpec() {
        return setSpec;
    }

    public void setSetSpec(String setSpec) {
        this.setSpec = setSpec;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }
    
    
}
