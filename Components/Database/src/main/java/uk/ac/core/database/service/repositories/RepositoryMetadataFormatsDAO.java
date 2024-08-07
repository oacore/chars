/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.service.repositories;

/**
 *
 * @author samuel
 */
public interface RepositoryMetadataFormatsDAO {

    void insertMetadataFormat(String prefix, String schema, String namespace);

    void insertOrUpdateRepositoryMetadataFormat(Integer repositoryId, String metadataFormatPrefix);

    void removeAllMetadataFormats(Integer repositoryId);
    
}
