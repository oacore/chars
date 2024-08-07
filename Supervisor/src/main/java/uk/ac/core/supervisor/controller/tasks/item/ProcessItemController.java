package uk.ac.core.supervisor.controller.tasks.item;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;

/**
 *
 * @author lucasanastasiou
 */
@RestController
@RequestMapping("/task/item/process")
public class ProcessItemController extends ItemTaskController {
@RequestMapping("/{articleId}")
    public TaskDescription execute(@Valid @PathVariable(value = "articleId") Integer articleId) {

        TaskDescription taskDescription = this.taskItemBuilder.buildSingleItemWorkflow(articleId);
        this.queueItemService.publish(taskDescription);

        return taskDescription;
    }
}