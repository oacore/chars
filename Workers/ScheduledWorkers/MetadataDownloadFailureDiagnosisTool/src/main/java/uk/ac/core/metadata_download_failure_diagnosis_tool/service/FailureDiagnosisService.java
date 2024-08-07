package uk.ac.core.metadata_download_failure_diagnosis_tool.service;

import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;

import java.util.List;

public interface FailureDiagnosisService {
    List<TaskItem> collectTaskItems();
    TaskItemStatus processSingleItem(TaskItem taskItem);
    String generateReport(List<TaskItemStatus> results, boolean taskOverallSuccess);
}
