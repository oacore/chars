package uk.ac.core.supervisor.controller.tasks.item;

import java.util.Optional;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.ItemIndexParameters;
import uk.ac.core.supervisor.exceptions.RepositoryDoesNotExistException;
import uk.ac.core.supervisor.exceptions.RepositoryIsDisabledException;

/**
 *
 * @author lucasanastasiou
 */
@RestController
@RequestMapping("/task/item/index")
public class ItemIndexController extends ItemTaskController {

    @RequestMapping("/{articleId}")
    public TaskDescription execute(@Valid @PathVariable(value = "articleId") Integer articleId) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {

        ItemIndexParameters indexParameters = new ItemIndexParameters(articleId);

        TaskDescription taskDescription = this.taskItemBuilder.buildItemTask(TaskType.INDEX_ITEM, indexParameters);

        this.queueItemService.publish(taskDescription);
        
        return taskDescription;
    }
    
    @RequestMapping("/{articleId}/deleted/{deletedStatus}")
    public TaskDescription execute(@Valid @PathVariable(value = "articleId") int articleId, @Valid @PathVariable(value = "deletedStatus") int deletedStatus) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {

        ItemIndexParameters indexParameters = new ItemIndexParameters(articleId, Optional.of(DeletedStatus.fromInteger(deletedStatus)));

        TaskDescription taskDescription = this.taskItemBuilder.buildItemTask(TaskType.INDEX_ITEM, indexParameters);

        this.queueItemService.publish(taskDescription);

        return taskDescription;
    }
}
