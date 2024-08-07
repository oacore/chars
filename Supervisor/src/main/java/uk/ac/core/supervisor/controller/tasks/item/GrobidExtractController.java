package uk.ac.core.supervisor.controller.tasks.item;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;

/**
 *
 * @author lucasanastasiou
 */
@RestController
@RequestMapping("/task/item/grobid-extract")
public class GrobidExtractController extends ItemTaskController {

    @RequestMapping("/{articleId}")
    public TaskDescription execute(@Valid @PathVariable(value = "articleId") Integer articleId) {

        TaskDescription taskDescription = this.taskItemBuilder.buildSingleItemTask(TaskType.GROBID_EXTRACTION_ITEM, articleId);

        this.queueItemService.publish(taskDescription);

        return taskDescription;
    }
}