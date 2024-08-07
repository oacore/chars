package uk.ac.core.supervisor.controller.tasks;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.supervisor.controller.TaskController;
import uk.ac.core.supervisor.exceptions.RepositoryDoesNotExistException;
import uk.ac.core.supervisor.exceptions.RepositoryIsDisabledException;

@RestController
@RequestMapping("/task/repository/extended-metadata-process")
public class ExtendedMetadataProcessController  extends TaskController {
    @RequestMapping("/{repositoryId}")
    public TaskDescription execute(
            @PathVariable(value = "repositoryId") int repositoryId) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {

        RepositoryTaskParameters repositoryTaskParameters = new RepositoryTaskParameters(repositoryId);

        TaskDescription taskDescription = this.getTaskDescriptionFactory().create(TaskType.EXTENDED_METADATA_PROCESS, repositoryTaskParameters);

        this.getQueueService().publish(taskDescription);

        return taskDescription;
    }
}
