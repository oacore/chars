package uk.ac.core.workermetrics.data.state;

import uk.ac.core.common.model.task.TaskType;

/**
 *
 * @author lucasanastasiou
 */
public enum ScheduledState {

    PENDING(0),
    SCHEDULED(1),
    IN_DOWNLOAD_METADATA_QUEUE(2),
    DOWNLOAD_METADATA(3),
    IN_EXTRACT_METADATA_QUEUE(4),
    EXTRACT_METADATA(5),
    IN_DOCUMENT_DOWNLOAD_QUEUE(6),
    DOCUMENT_DOWNLOAD(7);

    int workflowStep;

    ScheduledState(int workflowStep) {
        this.workflowStep = workflowStep;
    }

    public static ScheduledState fromTaskType(TaskType taskType) {
        if (taskType == TaskType.METADATA_DOWNLOAD) {
            return ScheduledState.DOWNLOAD_METADATA;
        } else if (taskType == TaskType.EXTRACT_METADATA) {
            return ScheduledState.EXTRACT_METADATA;
        } else if (taskType == TaskType.DOCUMENT_DOWNLOAD) {
            return ScheduledState.DOCUMENT_DOWNLOAD;
        } else {
            return null;
        }
    }

    public static ScheduledState fromNextTaskType(TaskType nextTaskType) {
        if (nextTaskType == TaskType.METADATA_DOWNLOAD) {
            return ScheduledState.IN_DOWNLOAD_METADATA_QUEUE;
        } else if (nextTaskType == TaskType.EXTRACT_METADATA) {
            return ScheduledState.IN_EXTRACT_METADATA_QUEUE;
        } else if (nextTaskType == TaskType.DOCUMENT_DOWNLOAD) {
            return ScheduledState.IN_DOCUMENT_DOWNLOAD_QUEUE;
        } else {
            return null;
        }
    }

    public static ScheduledState queueStateFromProcessingState(ScheduledState processingState) {
        if (processingState.equals(ScheduledState.DOWNLOAD_METADATA)) {
            return ScheduledState.IN_DOWNLOAD_METADATA_QUEUE;
        } else if (processingState.equals(ScheduledState.EXTRACT_METADATA)) {
            return ScheduledState.IN_EXTRACT_METADATA_QUEUE;
        } else if (processingState.equals(ScheduledState.DOCUMENT_DOWNLOAD)) {
            return ScheduledState.IN_DOCUMENT_DOWNLOAD_QUEUE;
        } else {
            return null;
        }
    }

}
