package uk.ac.core.metadata_download_failure_diagnosis_tool.model;

import uk.ac.core.common.model.task.TaskItemStatus;

public class FailureDiagnosisTaskItemStatus extends TaskItemStatus {
    private FailureDiagnosisResolution resolution;
    private Integer repoId;

    public FailureDiagnosisTaskItemStatus() {
    }

    public FailureDiagnosisResolution getResolution() {
        return resolution;
    }

    public void setResolution(FailureDiagnosisResolution resolution) {
        this.resolution = resolution;
    }

    public Integer getRepoId() {
        return repoId;
    }

    public void setRepoId(Integer repoId) {
        this.repoId = repoId;
    }
}
