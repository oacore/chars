package uk.ac.core.metadata_download_failure_diagnosis_tool.model;

import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.workermetrics.service.taskhistory.model.TaskHistoryBO;

@Deprecated
public final class CollectedFailedMetadataDownloadTaskItem implements TaskItem {

    private TaskHistoryBO result;

    public CollectedFailedMetadataDownloadTaskItem(TaskHistoryBO result) {
        this.result = result;
    }

    public TaskHistoryBO getResult() {
        return result;
    }

    public void setResult(TaskHistoryBO result) {
        this.result = result;
    }
}