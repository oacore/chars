package uk.ac.core.supervisor.controller.tasks.item;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.ItemIndexParameters;
import uk.ac.core.supervisor.exceptions.RepositoryDoesNotExistException;
import uk.ac.core.supervisor.exceptions.RepositoryIsDisabledException;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/task/item/works-index")
public class ItemWorkIndexController extends ItemTaskController {

    @RequestMapping("/{articleId}")
    public TaskDescription execute(@Valid @PathVariable(value = "articleId") Integer articleId) {

        TaskDescription taskDescription = this.taskItemBuilder
                .buildItemTask(TaskType.WORKS_INDEX_ITEM, new ItemIndexParameters(articleId));

        this.queueItemService.publish(taskDescription);

        return taskDescription;
    }

    @RequestMapping("/{articleId}/deleted/{deletedStatus}")
    public TaskDescription execute(@Valid @PathVariable(value = "articleId") int articleId, @Valid @PathVariable(value = "deletedStatus") int deletedStatus) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {

        ItemIndexParameters indexParameters = new ItemIndexParameters(articleId, Optional.of(DeletedStatus.fromInteger(deletedStatus)));

        TaskDescription taskDescription = this.taskItemBuilder.buildItemTask(TaskType.WORKS_INDEX_ITEM, indexParameters);

        this.queueItemService.publish(taskDescription);

        return taskDescription;
    }
}
