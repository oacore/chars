/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.dataprovider.logic.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.ac.core.dataprovider.logic.entity.DataProviderLocation;

/**
 *
 * @author samuel
 */
@Repository
public interface DataProviderLocationRepository extends CrudRepository<DataProviderLocation, Long> {

}
