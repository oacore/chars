package uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.elastic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.validation.ValidationReport;

/**
 *
 * @author mc26486
 */
@Service
public class ComplianceStorageServiceV2 {

    @Autowired
    private ElasticSearchValidationReportRepositoryV2 elasticSearchValidationReportRepository;
    @Autowired
    private ElasticSearchComplianceAggregateReportRepositoryV2 elasticSearchComplianceAggregateReportRepository;

    public void addReport(ValidationReport validationReport) {
        this.elasticSearchValidationReportRepository.save(validationReport);
    }

    public void storeAggregate(ComplianceAggregatedReportV2 complianceAggregatedReport) {
        this.elasticSearchComplianceAggregateReportRepository.save(complianceAggregatedReport);
    }

}
