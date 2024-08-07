/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.dataprovider.logic.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import uk.ac.core.dataprovider.logic.entity.IndexedDataProvider;

/**
 *
 * @author mc26486
 */
public interface IndexDataProviderRepository extends ElasticsearchRepository<IndexedDataProvider, Long> {

}
