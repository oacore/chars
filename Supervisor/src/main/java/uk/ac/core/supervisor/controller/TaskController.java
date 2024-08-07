package uk.ac.core.supervisor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.core.queue.QueueService;
import uk.ac.core.supervisor.factories.TaskDescriptionFactory;

public abstract class TaskController {

    QueueService queueService;

    TaskDescriptionFactory TaskDescriptionFactory;

    protected QueueService getQueueService() {
        return queueService;
    }

    @Autowired
    protected void setQueueService(QueueService queueService) {
        this.queueService = queueService;
    }

    protected TaskDescriptionFactory getTaskDescriptionFactory() {
        return TaskDescriptionFactory;
    }

    @Autowired
    protected void setTaskDescriptionFactory(TaskDescriptionFactory TaskDescriptionFactory) {
        this.TaskDescriptionFactory = TaskDescriptionFactory;
    }
}
