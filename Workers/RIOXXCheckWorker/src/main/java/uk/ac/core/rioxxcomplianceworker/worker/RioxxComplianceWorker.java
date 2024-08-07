package uk.ac.core.rioxxcomplianceworker.worker;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.rioxxvalidation.rioxx.ComplianceCheckerListener;
import uk.ac.core.rioxxvalidation.rioxx.RioxxRecordSAXHandler;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.ComplianceCheckerV2;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.RioxxComplianceTaskItemStatusV2;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.elastic.ComplianceAggregatedReportV2;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.elastic.ComplianceStorageServiceV2;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v2.validation.ValidationReport;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.ComplianceCheckerV3;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.elastic.ComplianceAggregatedReportV3;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.elastic.ComplianceStorageServiceV3;
import uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.entity.RioxxComplianceTaskItemStatusV3;
import uk.ac.core.worker.QueueWorker;

import uk.ac.core.worker.QueueWorker;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author mc26486
 */
@Slf4j
public class RioxxComplianceWorker extends QueueWorker implements ComplianceCheckerListener {

    @Autowired
    private FilesystemDAO filesystemDAO;
    @Autowired
    private ComplianceStorageServiceV2 complianceStorageServiceV2;
    @Autowired
    private ComplianceStorageServiceV3 complianceStorageServiceV3;

    private List<ValidationReport> validationReportsV2 = new ArrayList<>();
    private List<uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.validation.ValidationReport> validationReportsV3 = new ArrayList<>();
    private RepositoryTaskParameters repositoryTaskParameters;
    private Integer repositoryId;
    private RioxxRecordSAXHandler rioxxRecordSAXHandler;
    private String metadataPath;
    private SAXParser saxParser;
    private RioxxComplianceTaskItemStatusV2 rioxxComplianceTaskItemStatusV2;
    private RioxxComplianceTaskItemStatusV3 rioxxComplianceTaskItemStatusV3;
    private ComplianceCheckerListener complianceCheckerListener;

    @Override
    public List<TaskItem> collectData() {
        this.repositoryTaskParameters = new Gson().fromJson(this.currentWorkingTask.getTaskParameters(), RepositoryTaskParameters.class);
        this.repositoryId = repositoryTaskParameters.getRepositoryId();
        this.metadataPath = this.filesystemDAO.getMetadataPath(repositoryId);
        try {
            this.saxParser = SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException | SAXException ex) {
            log.error("Error while creating sax parser", ex);
        }
        return Collections.emptyList();
    }

    @Override
    public List<TaskItemStatus> process(List<TaskItem> taskItems) {
        rioxxComplianceTaskItemStatusV2 = new RioxxComplianceTaskItemStatusV2();
        rioxxComplianceTaskItemStatusV3 = new RioxxComplianceTaskItemStatusV3();
        this.workerStatus.setTaskStatus(rioxxComplianceTaskItemStatusV2);
        if (this.complianceCheckerListener==null){
            rioxxRecordSAXHandler = new RioxxRecordSAXHandler(new ComplianceCheckerV2(this),
                    new ComplianceCheckerV3(this));
        }
        else {
            rioxxRecordSAXHandler = new RioxxRecordSAXHandler(new ComplianceCheckerV2(this.complianceCheckerListener),
                    new ComplianceCheckerV3(this.complianceCheckerListener));
        }

        try {
            saxParser.parse(metadataPath, rioxxRecordSAXHandler);
        } catch (SAXException | IOException ex) {
            logger.error("Error while parsing metadata", ex);
        }
        return Collections.singletonList(rioxxComplianceTaskItemStatusV2);
    }

    @Override
    public void collectStatistics(List<TaskItemStatus> results) {
        if (rioxxComplianceTaskItemStatusV2.getProcessedCount() > 0) {
            rioxxComplianceTaskItemStatusV2 = (RioxxComplianceTaskItemStatusV2) results.get(0);
            Integer total = rioxxComplianceTaskItemStatusV2.getProcessedCount();
            Integer validBasic = rioxxComplianceTaskItemStatusV2.getValidRecordsBasic().get();
            Integer validFull = rioxxComplianceTaskItemStatusV2.getValidRecordsFull().get();
            Integer invalid = validationReportsV2.size();
            Integer broken = total - invalid - rioxxComplianceTaskItemStatusV2.getSuccessfulCount();
            complianceStorageServiceV2.storeAggregate(new ComplianceAggregatedReportV2(String.valueOf(repositoryId), total,
                    validBasic, validFull, invalid, broken));
            rioxxComplianceTaskItemStatusV2.setSuccess(total > 0);
        }

        if (rioxxComplianceTaskItemStatusV3.getProcessedCount() > 0) {
            rioxxComplianceTaskItemStatusV3 = (RioxxComplianceTaskItemStatusV3) results.get(0);
            Integer total = rioxxComplianceTaskItemStatusV3.getProcessedCount();
            Integer validRequired = rioxxComplianceTaskItemStatusV3.getValidRecordsRequiredData().get();
            Integer validOptional = rioxxComplianceTaskItemStatusV3.getValidRecordsOptionalData().get();
            Integer invalid = validationReportsV3.size();
            Integer broken = total - invalid - rioxxComplianceTaskItemStatusV3.getSuccessfulCount();
            complianceStorageServiceV3.storeAggregate(new ComplianceAggregatedReportV3(String.valueOf(repositoryId), total,
                    invalid, broken, validRequired, validOptional));
            rioxxComplianceTaskItemStatusV3.setSuccess(total > 0);
        }
    }

    @Override
    public void updateCompliance(ValidationReport validationReport) {
        rioxxComplianceTaskItemStatusV2.incProcessed();

        if (!validationReport.isAValidRecord()) {
            validationReportsV2.add(validationReport);
            validationReport.setRepositoryId(repositoryId);
            complianceStorageServiceV2.addReport(validationReport);
            if (validationReport.isAValidRecordBasic()) {
                rioxxComplianceTaskItemStatusV2.getValidRecordsBasic().incrementAndGet();
            }
            if (validationReport.isAValidRecordFull()) {
                rioxxComplianceTaskItemStatusV2.getValidRecordsFull().incrementAndGet();
            }
        } else {
            rioxxComplianceTaskItemStatusV2.incSuccessful();
        }
        logger.info("Processed: " + rioxxComplianceTaskItemStatusV2.getProcessedCount() + " Success: "
                + rioxxComplianceTaskItemStatusV2.getSuccessfulCount() + " Failures: " + validationReportsV2.size());
    }

    @Override
    public void updateCompliance(uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.validation.ValidationReport validationReport) {
        rioxxComplianceTaskItemStatusV3.incProcessed();

        if (!validationReport.isAValidRecord()) {
            validationReportsV3.add(validationReport);
            validationReport.setRepositoryId(repositoryId);
            complianceStorageServiceV3.addReport(validationReport);
            if (validationReport.isValidDataRequiredData()) {
                rioxxComplianceTaskItemStatusV3.getValidRecordsRequiredData().incrementAndGet();
            }

            if (validationReport.isValidDataOptionalData()) {
                rioxxComplianceTaskItemStatusV3.getValidRecordsOptionalData().incrementAndGet();
            }
        } else {
            rioxxComplianceTaskItemStatusV3.incSuccessful();
        }
        log.info("Processed: " + rioxxComplianceTaskItemStatusV3.getProcessedCount() + " Success: "
                + rioxxComplianceTaskItemStatusV3.getSuccessfulCount() + " Failures: " + validationReportsV3.size());
    }

    public void setMetadataPath(String metadataPath) {
        this.metadataPath = metadataPath;
    }

    public void setSaxParser(SAXParser saxParser) {
        this.saxParser = saxParser;
    }

    public void setComplianceCheckerListener(ComplianceCheckerListener complianceCheckerListener) {
        this.complianceCheckerListener = complianceCheckerListener;
    }
}
