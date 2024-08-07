package uk.ac.core.metadata_download_failure_diagnosis_tool.model;

import uk.ac.core.common.model.task.TaskItemStatus;

@Deprecated
public class MetadataDownloadFailureWorkerResultTaskItem extends TaskItemStatus {

    private long dataProviderId;
    private Status status;

    public MetadataDownloadFailureWorkerResultTaskItem(long dataProviderId, Status status) {
        this.dataProviderId = dataProviderId;
        this.status = status;
    }

    public long getDataProviderId() {
        return dataProviderId;
    }

    public void setDataProviderId(long dataProviderId) {
        this.dataProviderId = dataProviderId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        BROKEN, DEAD, FIXED
   }

}
