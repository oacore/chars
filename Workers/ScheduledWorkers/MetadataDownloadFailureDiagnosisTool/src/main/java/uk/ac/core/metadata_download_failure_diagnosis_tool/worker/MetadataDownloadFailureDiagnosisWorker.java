package uk.ac.core.metadata_download_failure_diagnosis_tool.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.dataprovider.logic.dto.DataProviderBO;
import uk.ac.core.dataprovider.logic.entity.DataProviderNote;
import uk.ac.core.dataprovider.logic.entity.IdentifyResponse;
import uk.ac.core.dataprovider.logic.exception.DataProviderNotFoundException;
import uk.ac.core.dataprovider.logic.service.DataProviderNoteService;
import uk.ac.core.dataprovider.logic.service.oaipmhdiscovery.OaiPmhEndpointService;
import uk.ac.core.dataprovider.logic.service.origin.DataProviderService;
import uk.ac.core.metadata_download_failure_diagnosis_tool.model.CollectedFailedMetadataDownloadTaskItem;
import uk.ac.core.metadata_download_failure_diagnosis_tool.model.MetadataDownloadFailureWorkerResultTaskItem;
import uk.ac.core.worker.ScheduledWorker;
import uk.ac.core.workermetrics.service.taskhistory.TaskService;
import uk.ac.core.workermetrics.service.taskhistory.model.TaskHistoryBO;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static uk.ac.core.metadata_download_failure_diagnosis_tool.model.MetadataDownloadFailureWorkerResultTaskItem.*;
import static uk.ac.core.metadata_download_failure_diagnosis_tool.model.MetadataDownloadFailureWorkerResultTaskItem.Status.*;

@Deprecated
public class MetadataDownloadFailureDiagnosisWorker extends ScheduledWorker {

    private final TaskService taskService;
    private final DataProviderService dataProviderService;
    private final DataProviderNoteService dataProviderNoteService;
    private final OaiPmhEndpointService oaiPmhEndpointService;

    private static final String WORKER_FAILED_EMAIL_MSG = "Metadata download failure diagnostics has failed due to an internal server error.";
    private static final String WORKER_SUCCEEDED_MSG = "Metadata download failure diagnostics was completed successfully.";
    private static final String DATAPROVIDER_DISABLED_MSG = "The dataprovider #%d was disabled automatically due to consistent fails during metadata download.";
    private static final int DAY_LIMIT_FOR_AUTOMATIC_RECOVERY = 10;

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataDownloadFailureDiagnosisWorker.class);

    private static final TaskType TASK_TYPE = TaskType.METADATA_DOWNLOAD_FAILURE_DIAGNOSTICS;

    public MetadataDownloadFailureDiagnosisWorker(TaskService taskService,
                                                  DataProviderService dataProviderService,
                                                  DataProviderNoteService dataProviderNoteService,
                                                  OaiPmhEndpointService oaiPmhEndpointService) {
        this.taskService = taskService;
        this.dataProviderService = dataProviderService;
        this.dataProviderNoteService = dataProviderNoteService;
        this.oaiPmhEndpointService = oaiPmhEndpointService;
    }

    @Override
    // execute every day at 1:00 a.m.
//    @Scheduled(cron = "0 0 1 * * *")
    public void scheduledStart() {
        this.start();
    }

    final int[] SKIP_REPOSITORIES = {
            143,// DOAJ old
            144,// arxiv
            145,// CiteSeerX
            645,// DOAJ new
            153,// Repec
            150,// PubMed
            4786// Crossref
    };

    @Override
    public List<TaskItem> collectData() {

        LOGGER.info("Metadata download failure diagnostics has been started.");

        List<TaskItem> collectedFailedMetadataDownloadTaskItems = new ArrayList<>();
        for (TaskHistoryBO taskHistoryBO : taskService.getFailedMetadataDownloadTasksFromYesterday()) {
            if (IntStream.of(SKIP_REPOSITORIES).noneMatch(x -> x == taskHistoryBO.getRepositoryId())) {
                collectedFailedMetadataDownloadTaskItems.add(new CollectedFailedMetadataDownloadTaskItem(taskHistoryBO));
            }
        }

        LOGGER.debug("Metadata download failure diagnostics has collected info about metadata downloads.");
        return collectedFailedMetadataDownloadTaskItems;
    }

    @Override
    public List<TaskItemStatus> process(List<TaskItem> taskItems) {

        List<TaskItemStatus> results = new ArrayList<>();

        for (TaskItem taskItem : taskItems) {

            CollectedFailedMetadataDownloadTaskItem task = (CollectedFailedMetadataDownloadTaskItem) taskItem;
            TaskHistoryBO failedMetadataDownloadTask = task.getResult();

            DataProviderBO dataProviderBO;
            try {
                dataProviderBO = dataProviderService.findById(failedMetadataDownloadTask.getRepositoryId());

                if (dataProviderBO.getOaiPmhEndpoint() == null || dataProviderBO.isDisabled()) continue;

                Optional<IdentifyResponse> response = oaiPmhEndpointService.findOaiPmhEndpoint(dataProviderBO.getOaiPmhEndpoint());

                //id can be null only if the id is passed for the save, not retrieved
                long dataProviderId = dataProviderBO.getId();

                if (response.isPresent()) {

                    dataProviderBO.setOaiPmhEndpoint(response.get().getBaseUrl());
                    dataProviderService.update(dataProviderBO);

                    results.add(new MetadataDownloadFailureWorkerResultTaskItem(dataProviderId, FIXED));

                } else if (failedMetadataDownloadTask.getDaysPassedAfterTheFirstTry() > DAY_LIMIT_FOR_AUTOMATIC_RECOVERY) {

                    dataProviderBO.setDisabled(true);
                    dataProviderService.update(dataProviderBO);
                    dataProviderNoteService.insert(createNote(dataProviderId));

                    results.add(new MetadataDownloadFailureWorkerResultTaskItem(dataProviderBO.getId(), DEAD));

                } else {
                    results.add(new MetadataDownloadFailureWorkerResultTaskItem(dataProviderBO.getId(), BROKEN));
                }

            } catch (DataProviderNotFoundException e) {
                LOGGER.warn(String.format("The data provider #%d wasn't found.", failedMetadataDownloadTask.getRepositoryId()));
            } catch (IOException e) {
                LOGGER.warn(String.format("The data provider #%d url can't be reached.", failedMetadataDownloadTask.getRepositoryId()));
            }

        }

        LOGGER.info(WORKER_SUCCEEDED_MSG);
        return results;
    }

    private DataProviderNote createNote(long id) {
        return new DataProviderNote(id, String.format(DATAPROVIDER_DISABLED_MSG, id), LocalDate.now());
    }

    @Override
    public String generateReport(List<TaskItemStatus> results, boolean taskOverallSuccess) {

        if (!taskOverallSuccess) return WORKER_FAILED_EMAIL_MSG;

        StringBuilder fixedRepositoriesLineDelimitedWithCommas = new StringBuilder();
        StringBuilder brokenRepositoriesLineDelimitedWithCommas = new StringBuilder();
        StringBuilder deadRepositoriesLineDelimitedWithCommas = new StringBuilder();

        for (TaskItemStatus result : results) {
            MetadataDownloadFailureWorkerResultTaskItem taskItem = (MetadataDownloadFailureWorkerResultTaskItem) result;

            long dataProviderId = taskItem.getDataProviderId();
            Status status = taskItem.getStatus();

            if (status == FIXED) {
                fixedRepositoriesLineDelimitedWithCommas.append(formatWithHtmlDataProviderString(dataProviderId));
            } else if (status == BROKEN) {
                brokenRepositoriesLineDelimitedWithCommas.append(formatWithHtmlDataProviderString(dataProviderId));
            } else {
                deadRepositoriesLineDelimitedWithCommas.append(formatWithHtmlDataProviderString(dataProviderId));
            }

        }

        return composeReportEmail(fixedRepositoriesLineDelimitedWithCommas.toString(),
                brokenRepositoriesLineDelimitedWithCommas.toString(),
                deadRepositoriesLineDelimitedWithCommas.toString());
    }

    private String formatWithHtmlDataProviderString(long dataProviderId) {
        return "<u>#" + dataProviderId + "</u> ";
    }

    private String composeReportEmail(String delimitedFixedDataProvidersIdsLine,
                                      String delimitedBrokenDataProvidersIdsLine,
                                      String delimitedDeadDataProvidersIdsLine) {

        String reportEmailBuilder = "<h2>Diagnosis of failed metadata downloads has finished successfully.</h2>" +
                String.format("Fixed<sup></sup> data providers: %s<br>", delimitedFixedDataProvidersIdsLine.isEmpty() ? "NONE" : delimitedFixedDataProvidersIdsLine) +
                String.format("Broken<sup>*</sup> data providers: %s<br>", delimitedBrokenDataProvidersIdsLine.isEmpty() ? "NONE" : delimitedBrokenDataProvidersIdsLine) +
                String.format("Dead<sup>**</sup> data providers: %s<br>", delimitedDeadDataProvidersIdsLine.isEmpty() ? "NONE" : delimitedDeadDataProvidersIdsLine) +
                "<sub><h2>Notes</h2><br>" +
                "* - metadata download failed consistently for these data providers, \n" +
                "but within the allowed period of time(10 days) for the self recovery.<br>" +
                "** - metadata download failed consistently for these data providers, \n" +
                "and exceeded the allowed period of time(10 days) for the self recovery.</sub><br>";
        return reportEmailBuilder;
    }

    @Override
    public TaskType getTaskType() {
        return TASK_TYPE;
    }

    @Override
    public TaskDescription generateTaskDescription() {
        TaskDescription task = new TaskDescription();
        task.setCreationTime(System.currentTimeMillis());
        task.setStartTime(System.currentTimeMillis());
        task.setType(TASK_TYPE);
        return task;
    }

    @Override
    public void collectStatistics(List<TaskItemStatus> results) {

    }
}
