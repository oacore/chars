package uk.ac.core.queue;

import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.TaskDescription;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class QueueItemServiceImpl extends AbstractQueueEvent implements QueueItemService{

    @Override
    public void publish(TaskDescription taskDescription) {
        this.convertAndSend("core-tasks-exchange", taskDescription.getType().getName(), taskDescription, 0);
    }

}
