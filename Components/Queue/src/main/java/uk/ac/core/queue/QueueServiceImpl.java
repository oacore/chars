package uk.ac.core.queue;

import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.TaskDescription;

@Service
public class QueueServiceImpl extends AbstractQueueEvent implements QueueService {

    public QueueServiceImpl() {
    }

    @Override
    public void publish(TaskDescription taskDescription) {
        String routingKey = taskDescription.getRoutingKey();
        this.convertAndSend("core-tasks-exchange", routingKey, taskDescription, taskDescription.getPriority());
    }

    @Override
    public void subscribe(String topic) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
