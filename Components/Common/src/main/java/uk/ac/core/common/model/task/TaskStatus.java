package uk.ac.core.common.model.task;

import java.io.Serializable;

/**
 *
 * @author mc26486
 */
public class TaskStatus implements Serializable {

    private String taskId;
    private Integer numberOfItemsToProcess=0;
    private Integer processedCount = 0;
    private Integer successfulCount = 0;
    private boolean success;

    public void incProcessed() {
        this.processedCount++;
    }

    public void incSuccessful() {
        this.successfulCount++;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Integer getNumberOfItemsToProcess() {
        return numberOfItemsToProcess;
    }

    public void setNumberOfItemsToProcess(Integer numberOfItemsToProcess) {
        this.numberOfItemsToProcess = numberOfItemsToProcess;
    }
    public void incNumberOfItemsToProcess(Integer numberOfItemsToProcess) {
        this.numberOfItemsToProcess += numberOfItemsToProcess;
    }

    public Integer getProcessedCount() {
        return processedCount;
    }

    public void setProcessedCount(Integer processedCount) {
        this.processedCount = processedCount;
    }

    public Integer getSuccessfulCount() {
        return successfulCount;
    }

    public void setSuccessfulCount(Integer successfulCount) {
        this.successfulCount = successfulCount;
    }

    public Boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "TaskStatus{" + "taskId=" + taskId + ", numberOfItemsToProcess=" + numberOfItemsToProcess + ", processedCount=" + processedCount + ", successfulCount=" + successfulCount + ", success=" + success + '}';
    }

}
