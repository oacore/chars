package uk.ac.core.metadata_download_failure_diagnosis_tool.model;

import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.workermetrics.data.entity.taskhistory.TaskHistory;

import java.util.Date;

public class FailureDiagnosisTaskItem implements TaskItem {
    private Integer repoId;
    private Integer freshness;
    private Date lastUpdate;
    private String metadataFormat;
    private TaskHistory taskHistory;

    public FailureDiagnosisTaskItem() {
    }

    public TaskHistory getTaskHistory() {
        return taskHistory;
    }

    public void setTaskHistory(TaskHistory taskHistory) {
        this.taskHistory = taskHistory;
    }

    public Integer getRepoId() {
        return repoId;
    }

    public void setRepoId(Integer repoId) {
        this.repoId = repoId;
    }

    public Integer getFreshness() {
        return freshness;
    }

    public void setFreshness(Integer freshness) {
        this.freshness = freshness;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getMetadataFormat() {
        return metadataFormat;
    }

    public void setMetadataFormat(String metadataFormat) {
        this.metadataFormat = metadataFormat;
    }
}
