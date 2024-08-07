package uk.ac.core.metadata_download_failure_diagnosis_tool.scenario.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.dataprovider.logic.dto.DataProviderBO;
import uk.ac.core.dataprovider.logic.entity.DataProviderNote;
import uk.ac.core.dataprovider.logic.entity.IdentifyResponse;
import uk.ac.core.dataprovider.logic.exception.DataProviderNotFoundException;
import uk.ac.core.dataprovider.logic.service.DataProviderNoteService;
import uk.ac.core.dataprovider.logic.service.oaipmhdiscovery.OaiPmhEndpointService;
import uk.ac.core.dataprovider.logic.service.origin.DataProviderService;
import uk.ac.core.metadata_download_failure_diagnosis_tool.model.FailureDiagnosisResolution;
import uk.ac.core.metadata_download_failure_diagnosis_tool.model.FailureDiagnosisTaskItem;
import uk.ac.core.metadata_download_failure_diagnosis_tool.scenario.FailureDiagnosisScenario;
import uk.ac.core.metadata_download_failure_diagnosis_tool.service.DiagnosisHistoryService;
import uk.ac.core.metadata_download_failure_diagnosis_tool.service.OaiPmhEndpointHealthService;
import uk.ac.core.supervisor.client.SupervisorClient;
import uk.ac.core.workermetrics.data.dao.scheduling.SchedulingRepositoryDAO;
import uk.ac.core.workermetrics.data.entity.ScheduledRepository;
import uk.ac.core.workermetrics.data.state.RepositoryPriority;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Component
public class MetadataDownloadFailureScenario implements FailureDiagnosisScenario {
    private static final Logger log = LoggerFactory.getLogger(MetadataDownloadFailureScenario.class);
    private static final int MAX_DAYS_SINCE_FIRST_ATTEMPT = 5;
    private static final String NOTE_FOR_SKIPPED_REPO = "This repository wasn't harvested successfully for " +
            "a long time; the repository was excluded from harvesting queue (the content is still available)";

    private final DataProviderService dataProviderService;
    private final OaiPmhEndpointHealthService oaiPmhEndpointHealthService;
    private final OaiPmhEndpointService oaiPmhEndpointService;
    private final SchedulingRepositoryDAO schedulingRepositoryDAO;
    private final DataProviderNoteService dataProviderNoteService;
    private final DiagnosisHistoryService diagnosisHistoryService;
    private final SupervisorClient supervisorClient;

    @Autowired
    public MetadataDownloadFailureScenario(DataProviderService dataProviderService, OaiPmhEndpointHealthService oaiPmhEndpointHealthService, OaiPmhEndpointService oaiPmhEndpointService, SchedulingRepositoryDAO schedulingRepositoryDAO, DataProviderNoteService dataProviderNoteService, DiagnosisHistoryService diagnosisHistoryService, SupervisorClient supervisorClient) {
        this.dataProviderService = dataProviderService;
        this.oaiPmhEndpointHealthService = oaiPmhEndpointHealthService;
        this.oaiPmhEndpointService = oaiPmhEndpointService;
        this.schedulingRepositoryDAO = schedulingRepositoryDAO;
        this.dataProviderNoteService = dataProviderNoteService;
        this.diagnosisHistoryService = diagnosisHistoryService;
        this.supervisorClient = supervisorClient;
    }

    @Override
    public FailureDiagnosisResolution run(TaskItem ti) {
        // set up everything
        FailureDiagnosisResolution resolution = FailureDiagnosisResolution.TICKET_FOR_OPS;
        FailureDiagnosisTaskItem taskItem = (FailureDiagnosisTaskItem) ti;
        ScheduledRepository sr = null;
        try {
            sr = this.schedulingRepositoryDAO.getScheduledRepositoryByRepositoryId(taskItem.getRepoId());
            if (sr == null) {
                throw new DataProviderNotFoundException(taskItem.getRepoId());
            }
            DataProviderBO dataProvider = this.dataProviderService.findById(taskItem.getRepoId());
            // check OAI-PMH endpoint health
            boolean isOaiPmhAlive = this.oaiPmhEndpointHealthService.isOaiPmhAlive(
                    dataProvider.getOaiPmhEndpoint(), taskItem.getMetadataFormat());
            // if OAI-PMH endpoint not responding, try finding correct OAI-PMH
            // otherwise, leave it for Ops team
            if (!isOaiPmhAlive) {
                String oaiPmh = dataProvider.getOaiPmhEndpoint();
                Optional<IdentifyResponse> responseOptional =
                        this.oaiPmhEndpointService.findOaiPmhEndpoint(oaiPmh);
                if (responseOptional.isPresent()) {
                    String newOaiPmh = responseOptional.get().getBaseUrl();
                    // check if new OAI-PMH is different
                    if (!newOaiPmh.equals(oaiPmh)) {
                        log.info("Found OAI-PMH endpoint -- {}", newOaiPmh);
                        dataProvider.setOaiPmhEndpoint(responseOptional.get().getBaseUrl());
                        this.dataProviderService.update(dataProvider);
                        this.supervisorClient.sendHarvestRepositoryRequest(taskItem.getRepoId());
                        resolution = FailureDiagnosisResolution.OAI_PMH_ENDPOINT_FIXED;
                    }
                } else {
                    log.info("Unable to find valid OAI-PMH endpoint for this repo");
                    log.info("Saving first attempt date for repository {}", taskItem.getRepoId());
                    this.diagnosisHistoryService.saveFirstAttemptDate(taskItem);
                    resolution = FailureDiagnosisResolution.OAI_PMH_ENDPOINT_UNAVAILABLE;
                }
            }
        } catch (DataProviderNotFoundException e) {
            log.error("Data provider doesn't exist", e);
            return resolution;
        } catch (IOException e) {
            log.error("IOException occurred while trying to find valid OAI-PMH endpoint", e);
            log.info("Saving first attempt date for repository {}", taskItem.getRepoId());
            this.diagnosisHistoryService.saveFirstAttemptDate(taskItem);
            resolution = FailureDiagnosisResolution.OAI_PMH_ENDPOINT_UNAVAILABLE;
        } catch (CHARSException e) {
            log.error("Exception while sending harvest request from SupervisorClient", e);
        }
        if (this.isRepositoryBroken(taskItem)) {
            log.info("Repository {} is broken", taskItem.getRepoId());
            this.setRepositorySkipped(taskItem);
            resolution = FailureDiagnosisResolution.REPOSITORY_BROKEN;
        }
        return resolution;
    }

    private boolean isRepositoryBroken(FailureDiagnosisTaskItem taskItem) {
        long daysSinceFirstAttempt = this.diagnosisHistoryService.daysSinceFirstAttempt(taskItem.getRepoId());
        return daysSinceFirstAttempt > MAX_DAYS_SINCE_FIRST_ATTEMPT;
    }

    private void setRepositorySkipped(FailureDiagnosisTaskItem taskItem) {
        log.info("Setting repository {} SKIPPED", taskItem.getRepoId());
        this.schedulingRepositoryDAO.updatePriority(taskItem.getRepoId(), RepositoryPriority.SKIP);
        log.info("Setting note for repository {}", taskItem.getRepoId());
        DataProviderNote dataProviderNote = new DataProviderNote();
        dataProviderNote.setNote(NOTE_FOR_SKIPPED_REPO);
        dataProviderNote.setIdRepository(Long.valueOf(taskItem.getRepoId()));
        dataProviderNote.setAdded(LocalDate.now());
        this.dataProviderNoteService.insert(dataProviderNote);
        log.info("Done");
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.METADATA_DOWNLOAD;
    }
}
