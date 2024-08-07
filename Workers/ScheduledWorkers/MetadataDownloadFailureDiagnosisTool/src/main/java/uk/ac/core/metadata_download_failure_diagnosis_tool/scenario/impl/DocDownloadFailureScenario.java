package uk.ac.core.metadata_download_failure_diagnosis_tool.scenario.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.metadata_download_failure_diagnosis_tool.model.FailureDiagnosisResolution;
import uk.ac.core.metadata_download_failure_diagnosis_tool.model.FailureDiagnosisTaskItem;
import uk.ac.core.metadata_download_failure_diagnosis_tool.scenario.FailureDiagnosisScenario;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

@Component
public class DocDownloadFailureScenario implements FailureDiagnosisScenario {
    private static final Logger log = LoggerFactory.getLogger(DocDownloadFailureScenario.class);
    private static final String LOG_FILES_PATH_PREFIX = "/data/remote/core/logs/tasks/";

    @Override
    public FailureDiagnosisResolution run(TaskItem ti) {
        FailureDiagnosisTaskItem taskItem = (FailureDiagnosisTaskItem) ti;
        File logFile = this.findLogFile(taskItem.getTaskHistory().getUniqueId());
        boolean dbIssue = this.isDatabaseIssue(logFile);
        return dbIssue ? FailureDiagnosisResolution.DB_ISSUE : FailureDiagnosisResolution.TICKET_FOR_OPS;
    }

    private File findLogFile(String uniqueId) {
        File file = new File(LOG_FILES_PATH_PREFIX + uniqueId + ".log");
        return file.exists() ? file : null;
    }

    private boolean isDatabaseIssue(File logFile) {
        try {
            if (logFile == null) {
                return false;
            }
            List<String> keyWords = Arrays.asList(
                    "Connection timed out",
                    "Read timed out" // todo: extend the list
            );
            String logs = new String(Files.readAllBytes(logFile.toPath()));
            return keyWords.stream()
                    .anyMatch(logs::contains);
        } catch (IOException e) {
            log.info("Unable to read log file {}", logFile.getPath());
            return false;
        }
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.DOCUMENT_DOWNLOAD;
    }
}
