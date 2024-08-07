package uk.ac.core.common.model.task.item;

import uk.ac.core.common.model.task.TaskItemStatus;
import java.io.Serializable;
import uk.ac.core.common.model.task.TaskDescription;

/**
 *
 * @author lucas
 */
public class TaskItemEvent implements Serializable{

    private String event;
    private TaskDescription taskItemDescription;
    private TaskItemStatus taskItemStatus;
    private Long time;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public TaskDescription getTaskItemDescription() {
        return taskItemDescription;
    }

    public void setTaskItemDescription(TaskDescription taskItemDescription) {
        this.taskItemDescription = taskItemDescription;
    }

    public TaskItemStatus getTaskItemStatus() {
        return taskItemStatus;
    }

    public void setTaskItemStatus(TaskItemStatus taskItemStatus) {
        this.taskItemStatus = taskItemStatus;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "TaskItemEvent{" + "event=" + event + ", taskItemDescription=" + taskItemDescription + ", taskItemStatus=" + taskItemStatus + ", time=" + time + '}';
    }
}
