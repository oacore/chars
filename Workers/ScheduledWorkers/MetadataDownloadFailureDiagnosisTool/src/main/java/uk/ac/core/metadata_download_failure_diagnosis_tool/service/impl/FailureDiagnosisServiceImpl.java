package uk.ac.core.metadata_download_failure_diagnosis_tool.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.database.service.updates.TaskUpdatesDAO;
import uk.ac.core.metadata_download_failure_diagnosis_tool.model.FailureDiagnosisResolution;
import uk.ac.core.metadata_download_failure_diagnosis_tool.model.FailureDiagnosisTaskItem;
import uk.ac.core.metadata_download_failure_diagnosis_tool.model.FailureDiagnosisTaskItemStatus;
import uk.ac.core.metadata_download_failure_diagnosis_tool.scenario.FailureDiagnosisScenario;
import uk.ac.core.metadata_download_failure_diagnosis_tool.service.FailureDiagnosisService;
import uk.ac.core.workermetrics.data.repo.TaskHistoryRepository;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

@Service
public class FailureDiagnosisServiceImpl implements FailureDiagnosisService {
    private static final Logger log = LoggerFactory.getLogger(FailureDiagnosisServiceImpl.class);
    private static final String APPLE_CORE_LINK_PREFIX = "https://apple.core.ac.uk/dataproviders/";
    private static final String WORKER_FAILED_EMAIL_MSG =
            "Metadata download failure diagnostics has failed due to an internal server error.";

    private final TaskHistoryRepository taskHistoryRepository;
    private final List<FailureDiagnosisScenario> scenarios;
    private final TaskUpdatesDAO taskUpdatesDAO;

    @Autowired
    public FailureDiagnosisServiceImpl(TaskHistoryRepository taskHistoryRepository, List<FailureDiagnosisScenario> scenarios, TaskUpdatesDAO taskUpdatesDAO) {
        this.taskHistoryRepository = taskHistoryRepository;
        this.scenarios = scenarios;
        this.taskUpdatesDAO = taskUpdatesDAO;
    }

    @Override
    public List<TaskItem> collectTaskItems() {
        long start = System.currentTimeMillis(), end;
        log.info("Collecting data for task items ...");
        List<TaskItem> taskItems = this.convertToTaskItems(this.taskUpdatesDAO.getRepositoriesWithBadFreshness());
        log.info("Got {} task items", taskItems.size());
        end = System.currentTimeMillis();
        log.info("Done in {} ms", end - start);
        return taskItems;
    }

    private List<TaskItem> convertToTaskItems(List<Map<String, Object>> taskUpdates) {
        List<TaskItem> taskItems = new ArrayList<>();
        for (Map<String, Object> tu: taskUpdates) {
            FailureDiagnosisTaskItem taskItem = new FailureDiagnosisTaskItem();
            taskItem.setRepoId((Integer) tu.get("id_repository"));
            taskItem.setFreshness((Integer) tu.get("freshness"));
            taskItem.setLastUpdate((Date) tu.get("last_update"));
            taskItem.setMetadataFormat((String) tu.get("metadata_format"));
            taskItem.setTaskHistory(
                    this.taskHistoryRepository.findTopByRepositoryIdOrderByStartTimeDesc(taskItem.getRepoId()));
            taskItems.add(taskItem);
        }
        return taskItems;
    }

    @Override
    public TaskItemStatus processSingleItem(TaskItem ti) {
        long start = System.currentTimeMillis(), end;
        log.info("Start processing single item ...");
        FailureDiagnosisTaskItem taskItem = (FailureDiagnosisTaskItem) ti;

        FailureDiagnosisTaskItemStatus status = new FailureDiagnosisTaskItemStatus();
        status.setTaskId("repo_diag_" + taskItem.getRepoId() + "_" + System.currentTimeMillis());
        status.setNumberOfItemsToProcess(1);

        FailureDiagnosisResolution resolution = this.checkRepository(taskItem);

        status.setSuccess(resolution != null);
        status.setResolution(resolution);
        status.setRepoId(taskItem.getRepoId());
        status.setProcessedCount(1);

        if (status.isSuccess()) {
            status.setSuccessfulCount(1);
        } else {
            status.setSuccessfulCount(0);
        }
        end = System.currentTimeMillis();
        log.info("Done in {} ms", end - start);
        return status;
    }

    private FailureDiagnosisResolution checkRepository(FailureDiagnosisTaskItem taskItem) {
        log.info("Start repository {} check ...", taskItem.getRepoId());
        FailureDiagnosisResolution resolution;
        FailureDiagnosisScenario scenario = this.findRelevantScenario(taskItem).orElse(null);
        if (scenario != null) {
            resolution = scenario.run(taskItem);
        } else {
            log.info("Unable to find scenario for this case, unknown task type: {}",
                    taskItem.getTaskHistory().getTaskType());
            resolution = FailureDiagnosisResolution.TICKET_FOR_OPS;
        }
        return resolution;
    }

    private Optional<FailureDiagnosisScenario> findRelevantScenario(FailureDiagnosisTaskItem taskItem) {
        return this.scenarios.stream()
                .filter(s -> taskItem.getTaskHistory().getTaskType().equals(s.getTaskType()))
                .findFirst();
    }

    @Override
    public String generateReport(List<TaskItemStatus> results, boolean taskOverallSuccess) {
        if (!taskOverallSuccess) {
            return WORKER_FAILED_EMAIL_MSG;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>Diagnosis of failed metadata downloads has finished successfully.</h2>");
        sb.append("<ul>");
        for (TaskItemStatus tis: results) {
            FailureDiagnosisTaskItemStatus taskItemStatus = (FailureDiagnosisTaskItemStatus) tis;
            sb.append(this.composeBulletPoint(taskItemStatus));
        }
        sb.append("</ul>");

//        this.writeReportToTheFile(sb.toString());

        return sb.toString();
    }

    private String composeBulletPoint(FailureDiagnosisTaskItemStatus taskItemStatus) {
        String adminPageUrl = APPLE_CORE_LINK_PREFIX + taskItemStatus.getRepoId();
        String separator = " - ";
        return "<li>" +
                this.composeHtmlLinkTagFromUrl(adminPageUrl, taskItemStatus.getRepoId()) +
                separator +
                taskItemStatus.getResolution().toHtml() +
                separator +
                taskItemStatus.getResolution().getDescription() +
                "</li>";
    }

    private String composeHtmlLinkTagFromUrl(String adminPageUrl, int repoId) {
        return "<a " +
                "href=\"" +
                adminPageUrl +
                "\">" +
                "Repository " + repoId +
                "</a>";
    }

    private static final String RESULTS_FOLDER_PATH = "/data/repo-diagnosis/reports/";
    private static final String RESULTS_FILE_NAME_PREFIX = "repo_diagnosis_report_";

    private void writeReportToTheFile(String report) {
        try {
            File folder = new File(RESULTS_FOLDER_PATH);
            folder.mkdirs();

            String fileName =
                    RESULTS_FOLDER_PATH + RESULTS_FILE_NAME_PREFIX + System.currentTimeMillis() + ".html";
            File file = new File(fileName);
            file.createNewFile();

            Files.write(file.toPath(), report.getBytes());
            log.info("Results saved to {}", fileName);
        } catch (Exception e) {
            log.error("Exception while collectStatistics()", e);
        }
    }
}
