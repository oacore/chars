package uk.ac.core.workermetrics.data.entity.taskhistory;

import uk.ac.core.common.model.task.TaskType;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Task history.
 */
@Entity
@Table(name = "task_history")
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column(name = "unique_id", nullable = false)
    private String uniqueId;

    @Column(name = "worker_name", nullable = false)
    private String workerName;

    @Column(name = "task_type")
    private TaskType taskType;

    @Column(name = "task_parameters")
    private String taskParameters;

    @Column
    private int priority;

    @Column(name = "routing_key")
    private TaskType routingKey;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "from_date", nullable = true)
    private LocalDateTime harvestingFromDate;

    @Column(name = "to_date", nullable = true)
    private LocalDateTime harvestingToDate;

    @Column(name = "number_of_items_to_process")
    private int numberOfItemsToProcess;

    @Column
    private int processed;

    @Column
    private int successful;

    @Column
    private boolean success;

    @Column(name = "id_repository")
    private int repositoryId;

    private TaskHistory() {
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getTaskParameters() {
        return taskParameters;
    }

    public void setTaskParameters(String taskParameters) {
        this.taskParameters = taskParameters;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public TaskType getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(TaskType routingKey) {
        this.routingKey = routingKey;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getHarvestingFromDate() {
        return harvestingFromDate;
    }

    public void setHarvestingFromDate(LocalDateTime harvestingFromDate) {
        this.harvestingFromDate = harvestingFromDate;
    }

    public LocalDateTime getHarvestingToDate() {
        return harvestingToDate;
    }

    public void setHarvestingToDate(LocalDateTime harvestingToDate) {
        this.harvestingToDate = harvestingToDate;
    }

    public int getNumberOfItemsToProcess() {
        return numberOfItemsToProcess;
    }

    public void setNumberOfItemsToProcess(int numberOfItemsToProcess) {
        this.numberOfItemsToProcess = numberOfItemsToProcess;
    }

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }

    public int getSuccessful() {
        return successful;
    }

    public void setSuccessful(int successful) {
        this.successful = successful;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


    public int getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(int repositoryId) {
        this.repositoryId = repositoryId;
    }
}
