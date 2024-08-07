package uk.ac.core.dataprovider.api.controller.rioxx;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.ComplianceCheckerV3;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.ComplianceCheckerV2;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.validation.ValidationReport;

import java.util.*;


@RestController
public class ValidationController {
    private static final Logger logger = LoggerFactory.getLogger(ValidationController.class);

    private final ComplianceCheckerV3 complianceCheckerV3;

    private final ComplianceCheckerV2 complianceCheckerV2;

    @Autowired
    public ValidationController() {
        this.complianceCheckerV3 = new ComplianceCheckerV3(null);
        this.complianceCheckerV2 = new ComplianceCheckerV2(null);
    }

    @PostMapping("/rioxx/validate")
    public String processRecord(
            @RequestParam(name = "record", required = true)
                    String record
    ) {
        if (record.contains("rioxx.net/schema/v2.0/rioxx/")) {
            return processRecordV2(record);
        }
        ValidationReport validationReport = this.complianceCheckerV3.check(record, null);
        return new Gson().toJson(validationReport);
    }

    @PostMapping("/rioxx/validate/v2")
    public String processRecordV2(

            @RequestParam(name = "record", required = true)
                    String record
    ) {
        uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.validation.ValidationReport validationReport = this.complianceCheckerV2.check(record, null);

        ValidationReport validationReportUpdated = this.convertValidationReportToNewVersion(validationReport);

        return new Gson().toJson(validationReportUpdated);
    }

    private ValidationReport convertValidationReportToNewVersion(uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.validation.ValidationReport validationReport) {
        ValidationReport validationReportUpdated = new ValidationReport();
        validationReportUpdated.setParseFailed(validationReport.isParseFailed());
        Map<String, List<String>> missingRequiredData = new HashMap<>();
        for (String field : validationReport.getMissingRequiredFieldFull()) {
            missingRequiredData.put(field, Collections.singletonList("Field " + field + " is missing from the record."));
        }
        validationReportUpdated.setMissingRequiredData(missingRequiredData);
        validationReportUpdated.setMissingOptionalData(Collections.emptyMap());
        validationReportUpdated.setRioxxVersion(validationReport.getRioxxVersion());
        return validationReportUpdated;
    }
}
