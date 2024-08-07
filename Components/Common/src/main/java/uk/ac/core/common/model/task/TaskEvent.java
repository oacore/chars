package uk.ac.core.common.model.task;

import java.io.Serializable;

/**
 *
 * @author mc26486
 */
public class TaskEvent implements Serializable{

    private String event;
    private TaskDescription taskDescription;
    private TaskStatus taskStatus;
    private Long time;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public TaskDescription getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(TaskDescription taskDescription) {
        this.taskDescription = taskDescription;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "TaskEvent{" + "event=" + event + ", taskDescription=" + taskDescription + ", taskStatus=" + taskStatus + ", time=" + time + '}';
    }

}
