/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.dataprovider.logic.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.ac.core.dataprovider.logic.entity.RepositoryHarvestProperties;

/**
 *
 * @author samuel
 */
@Repository
public interface RepositoryHarvestPropertiesRepository extends CrudRepository<RepositoryHarvestProperties, Long> {

    @Modifying
    @Query("update RepositoryHarvestProperties p set p.disabled = true where p.id = :id")
    void disableRepository(@Param(value = "id") Long repositoryId);
}
