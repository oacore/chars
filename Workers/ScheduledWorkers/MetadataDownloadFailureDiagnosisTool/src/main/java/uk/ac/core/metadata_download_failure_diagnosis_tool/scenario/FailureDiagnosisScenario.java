package uk.ac.core.metadata_download_failure_diagnosis_tool.scenario;

import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.metadata_download_failure_diagnosis_tool.model.FailureDiagnosisResolution;

public interface FailureDiagnosisScenario {
    FailureDiagnosisResolution run(TaskItem taskItem);
    TaskType getTaskType();
}
