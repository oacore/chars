package uk.ac.core.itemeventscheduler.broker;

import com.google.gson.Gson;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.item.TaskItemEvent;
import uk.ac.core.queue.QueueItemService;

/**
 * @author lucasanastasiou
 */
public class ItemEventSchedulerQueueBroker {

    private final QueueItemService queueItemService;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ItemEventSchedulerQueueBroker.class);

    public ItemEventSchedulerQueueBroker(QueueItemService queueItemService) {
        this.queueItemService = queueItemService;
    }

    /**
     * Event message received from event queue (coming from workers)
     *
     * @param message
     */
    public void eventReceived(Object message) {
        try {

            String messageString = (String) message;

            logger.info("    [x] Received event : " + messageString);

            // deserialise either to TaskEvent or TaskItemEvent
            TaskItemEvent taskItemEvent = new Gson().fromJson(messageString, TaskItemEvent.class);
            this.itemEventReceived(taskItemEvent);
        } catch (Exception e) {
            logger.error("Error in itemEventReceived", e);
        }

    }

    public void itemEventReceived(TaskItemEvent taskItemEvent) throws Exception {

        if (taskItemEvent.getEvent().equals("SCHEDULE")) {
            TaskDescription taskDescription = taskItemEvent.getTaskItemDescription();
            queueItemService.publish(taskDescription);
        }

        if (taskItemEvent.getEvent().equals("FINISH")) {
            List<TaskType> taskList = taskItemEvent.getTaskItemDescription().getTaskList();
            if (taskList != null && !taskList.isEmpty()) {

                TaskType nextTaskType = null;

                for (int i = 0; i < taskList.size() - 1; i++) {
                    if (taskList.get(i) == taskItemEvent.getTaskItemDescription().getType()) {
                        // found
                        nextTaskType = taskList.get(i + 1);// no need to check for arrayindexoutofbounds since i am iterating until the pre-last element
                    }
                }

                if (nextTaskType != null) {
                    taskItemEvent.getTaskItemDescription().setType(nextTaskType);

                    queueItemService.publish(taskItemEvent.getTaskItemDescription());

                }
            }
        }
    }

}
