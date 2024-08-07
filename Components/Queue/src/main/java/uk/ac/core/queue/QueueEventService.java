package uk.ac.core.queue;

import uk.ac.core.common.model.task.TaskEvent;
import uk.ac.core.common.model.task.item.TaskItemEvent;

/**
 *
 * @author mc26486
 */
public interface QueueEventService {
    public void publishTaskEvent(TaskEvent taskEvent);

    public void publishTaskItemEvent(TaskItemEvent taskItemEvent);
}
