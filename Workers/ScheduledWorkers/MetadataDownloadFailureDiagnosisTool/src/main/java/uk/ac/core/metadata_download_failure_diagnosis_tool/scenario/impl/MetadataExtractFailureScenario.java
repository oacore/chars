package uk.ac.core.metadata_download_failure_diagnosis_tool.scenario.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.dataprovider.logic.dto.DataProviderBO;
import uk.ac.core.dataprovider.logic.exception.DataProviderNotFoundException;
import uk.ac.core.dataprovider.logic.service.origin.DataProviderService;
import uk.ac.core.metadata_download_failure_diagnosis_tool.model.FailureDiagnosisResolution;
import uk.ac.core.metadata_download_failure_diagnosis_tool.model.FailureDiagnosisTaskItem;
import uk.ac.core.metadata_download_failure_diagnosis_tool.scenario.FailureDiagnosisScenario;
import uk.ac.core.metadata_download_failure_diagnosis_tool.service.OaiPmhEndpointHealthService;

@Component
public class MetadataExtractFailureScenario implements FailureDiagnosisScenario {
    private static final Logger log = LoggerFactory.getLogger(MetadataExtractFailureScenario.class);

    private final OaiPmhEndpointHealthService oaiPmhEndpointHealthService;
    private final DataProviderService dataProviderService;

    @Autowired
    public MetadataExtractFailureScenario(OaiPmhEndpointHealthService oaiPmhEndpointHealthService, DataProviderService dataProviderService) {
        this.oaiPmhEndpointHealthService = oaiPmhEndpointHealthService;
        this.dataProviderService = dataProviderService;
    }

    @Override
    public FailureDiagnosisResolution run(TaskItem ti) {
        FailureDiagnosisResolution resolution = FailureDiagnosisResolution.TICKET_FOR_OPS;
        FailureDiagnosisTaskItem taskItem = (FailureDiagnosisTaskItem) ti;
        try {
            DataProviderBO dataProvider = this.dataProviderService.findById(taskItem.getRepoId());
            boolean isOaiPmhEmpty = this.oaiPmhEndpointHealthService.isOaiPmhEmpty(
                    dataProvider.getOaiPmhEndpoint(), taskItem.getMetadataFormat());
            resolution = isOaiPmhEmpty
                    ? FailureDiagnosisResolution.OAI_PMH_ENDPOINT_EMPTY
                    : FailureDiagnosisResolution.TICKET_FOR_OPS;
        } catch (DataProviderNotFoundException e) {
            log.error("Data provider doesn't exist", e);
            return resolution;
        }
        return resolution;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EXTRACT_METADATA;
    }
}
