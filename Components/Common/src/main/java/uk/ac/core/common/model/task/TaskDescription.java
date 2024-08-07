package uk.ac.core.common.model.task;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author mc26486
 */
public class TaskDescription implements Serializable {

    private String uniqueId;
    private TaskType type;
    private String taskParameters;
    private int priority;
    private List<TaskType> taskList;
    private String routingKey;
    private Long creationTime;
    private Long startTime;
    private Long endTime;
    private boolean forceWorksReindex;

    public TaskDescription() {
        this.uniqueId = UUID.randomUUID().toString();
        this.creationTime = System.currentTimeMillis();
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<TaskType> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskType> taskList) {
        this.taskList = taskList;
    }

    public String getTaskParameters() {
        return taskParameters;
    }

    public void setTaskParameters(String taskParameters) {
        this.taskParameters = taskParameters;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    @Override
    public String toString() {
        return "TaskDescription{" + "uniqueId=" + uniqueId + ", type=" + type + ", taskParameters=" + taskParameters + ", priority=" + priority + ", taskList=" + taskList + ", creationTime=" + creationTime + ", startTime=" + startTime + ", endTime=" + endTime + '}';
    }

    public boolean isForceWorksReindex() {
        return forceWorksReindex;
    }

    public void setForceWorksReindex(boolean forceWorksReindex) {
        this.forceWorksReindex = forceWorksReindex;
    }
}
