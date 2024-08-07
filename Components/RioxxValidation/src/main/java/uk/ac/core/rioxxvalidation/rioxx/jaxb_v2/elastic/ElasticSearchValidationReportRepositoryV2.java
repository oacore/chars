package uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.validation.ValidationReport;

/**
 *
 * @author mc26486
 */
public interface ElasticSearchValidationReportRepositoryV2 extends ElasticsearchRepository<ValidationReport, String> {

}
