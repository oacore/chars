package uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticSearchComplianceAggregateReportRepositoryV3 extends ElasticsearchRepository<ComplianceAggregatedReportV3, String> {
}
