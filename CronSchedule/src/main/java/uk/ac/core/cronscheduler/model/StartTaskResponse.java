package uk.ac.core.cronscheduler.model;

import uk.ac.core.common.model.task.TaskType;

/**
 * Started task response.
 */
public final class StartTaskResponse {

    private final String message;

    public StartTaskResponse(TaskType workerType) {
        this.message = String.format("%s was started.", workerType.getName());
    }

    public String getMessage() {
        return message;
    }
}
