package uk.ac.core.supervisor.controller.tasks.item;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.ItemIndexParameters;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;

import javax.validation.Valid;

@RestController
@RequestMapping("/task/item/pdf-decorate")
public class PdfDecorateItemController extends ItemTaskController {

    @RequestMapping("/{articleId}")
    public TaskDescription execute(@Valid @PathVariable(value = "articleId") Integer articleId) {

        TaskDescription taskDescription = this.taskItemBuilder
                .buildItemTask(TaskType.PDF_DECORATE_ITEM, new SingleItemTaskParameters(articleId));

        this.queueItemService.publish(taskDescription);

        return taskDescription;
    }
}
