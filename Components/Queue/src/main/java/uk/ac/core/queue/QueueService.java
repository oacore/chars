package uk.ac.core.queue;

import uk.ac.core.common.model.task.TaskDescription;

/**
 *
 * @author mc26486
 */
public interface QueueService {

    public void publish(TaskDescription taskDescription);
    
    public void subscribe(String topic);
}
