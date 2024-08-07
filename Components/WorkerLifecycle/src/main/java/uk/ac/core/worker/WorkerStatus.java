package uk.ac.core.worker;

import com.rabbitmq.client.Channel;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskStatus;
import uk.ac.core.common.servlet.NodeStatus;

/**
 *
 * @author mc26486
 */
public class WorkerStatus extends NodeStatus {

    TaskDescription currentTask;
    private String status;
    TaskStatus taskStatus;

    Long deliveryTag;
    transient Channel channel;

    public TaskDescription getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(TaskDescription currentTask) {
        this.currentTask = currentTask;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Long getDeliveryTag() {
        return deliveryTag;
    }

    public void setDeliveryTag(Long deliveryTag) {
        this.deliveryTag = deliveryTag;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

}
