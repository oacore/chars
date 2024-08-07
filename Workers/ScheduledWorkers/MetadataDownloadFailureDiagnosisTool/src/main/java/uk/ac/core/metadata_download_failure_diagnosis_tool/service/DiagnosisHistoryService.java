package uk.ac.core.metadata_download_failure_diagnosis_tool.service;

import uk.ac.core.common.model.task.TaskItem;

public interface DiagnosisHistoryService {
    void saveFirstAttemptDate(TaskItem taskItem);
    long daysSinceFirstAttempt(int repoId);
}
