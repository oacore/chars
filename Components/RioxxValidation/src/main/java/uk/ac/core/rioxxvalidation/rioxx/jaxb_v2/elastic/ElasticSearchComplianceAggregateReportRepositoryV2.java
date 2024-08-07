/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.elastic.ComplianceAggregatedReportV2;

/**
 *
 * @author mc26486
 */
public interface ElasticSearchComplianceAggregateReportRepositoryV2 extends ElasticsearchRepository<ComplianceAggregatedReportV2, String> {

}
