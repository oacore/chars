package uk.ac.core.supervisor.controller.tasks;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.supervisor.controller.TaskController;
import uk.ac.core.supervisor.exceptions.RepositoryDoesNotExistException;
import uk.ac.core.supervisor.exceptions.RepositoryIsDisabledException;
import uk.ac.core.common.model.task.parameters.IndexParameters;
/**
 *
 * @author samuel
 */
@RestController
@RequestMapping("/task/repository/index")
public class IndexController extends TaskController {

    @RequestMapping("/{repositoryId}")
    public TaskDescription execute(@Valid @PathVariable(value = "repositoryId") int repositoryId) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {

        IndexParameters indexParameters = new IndexParameters(repositoryId);

        TaskDescription taskDescription = this.getTaskDescriptionFactory().create(TaskType.INDEX, indexParameters);

        this.getQueueService().publish(taskDescription);

        return taskDescription;
    }    
}
