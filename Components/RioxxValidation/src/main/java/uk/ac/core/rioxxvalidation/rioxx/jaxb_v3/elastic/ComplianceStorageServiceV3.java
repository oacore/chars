package uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.elastic;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.validation.ValidationReport;

@Service
public class ComplianceStorageServiceV3 {

    @Autowired
    private ElasticSearchValidationReportRepositoryV3 elasticSearchValidationReportRepository;
    @Autowired
    private ElasticSearchComplianceAggregateReportRepositoryV3 elasticSearchComplianceAggregateReportRepository;

    public void addReport(ValidationReport validationReport) {
        this.elasticSearchValidationReportRepository.save(validationReport);
    }

    public void storeAggregate(ComplianceAggregatedReportV3 complianceAggregatedReport) {
        this.elasticSearchComplianceAggregateReportRepository.save(complianceAggregatedReport);
    }
}
