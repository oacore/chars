package uk.ac.core.queue;

import uk.ac.core.common.model.task.TaskDescription;


/**
 *
 * @author lucasanastasiou
 */
public interface QueueItemService {
    public void publish(TaskDescription taskDescription);
}
