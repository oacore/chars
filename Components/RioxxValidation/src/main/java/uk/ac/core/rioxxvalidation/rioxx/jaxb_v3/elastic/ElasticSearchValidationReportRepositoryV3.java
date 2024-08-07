package uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.validation.ValidationReport;

public interface ElasticSearchValidationReportRepositoryV3 extends ElasticsearchRepository<ValidationReport, String> {
}
