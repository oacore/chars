package uk.ac.core.supervisor.controller.tasks.item;

import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.core.common.model.task.item.TaskItemBuilder;
import uk.ac.core.queue.QueueItemService;
import uk.ac.core.supervisor.factories.TaskDescriptionFactory;

/**
 *
 * @author lucasanastasiou
 */
public abstract class ItemTaskController {

    @Autowired
    QueueItemService queueItemService;
    
    @Autowired
    TaskItemBuilder taskItemBuilder;
    
}
