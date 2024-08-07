package uk.ac.core.queue;

import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.TaskEvent;
import uk.ac.core.common.model.task.item.TaskItemEvent;

/**
 *
 * @author mc26486
 */
@Service
public class QueueEventServiceImpl extends AbstractQueueEvent implements QueueEventService {

    @Override
    public void publishTaskEvent(TaskEvent taskEvent) {
        this.convertAndSend("events-exchange","task-events", taskEvent, 0);
    }

    @Override
    public void publishTaskItemEvent(TaskItemEvent taskItemEvent) {
        System.out.println("taskItemEvent = " + taskItemEvent.toString());
        this.convertAndSend("events-exchange", "item-events", taskItemEvent, 0);
    }
}
