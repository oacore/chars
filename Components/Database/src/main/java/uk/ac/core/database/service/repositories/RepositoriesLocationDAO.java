/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.service.repositories;

import uk.ac.core.common.model.legacy.LegacyRepositoryLocation;

/**
 * No longer valid to perform operations on core repo locations,
 * replaced by {@literal RepositoryLocationRepository} for {@literal DataProvider}.
 */
@Deprecated
public interface RepositoriesLocationDAO {

    public void insertOrUpdate(LegacyRepositoryLocation repositoryLocation);
    
    public LegacyRepositoryLocation load(Integer repoId);    

}
