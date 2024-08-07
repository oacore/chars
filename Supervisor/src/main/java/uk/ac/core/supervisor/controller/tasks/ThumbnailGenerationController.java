package uk.ac.core.supervisor.controller.tasks;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.ThumbnailGenerationParameters;
import uk.ac.core.supervisor.controller.TaskController;
import uk.ac.core.supervisor.exceptions.RepositoryDoesNotExistException;
import uk.ac.core.supervisor.exceptions.RepositoryIsDisabledException;

/**
 *
 * @author samuel
 */
@RestController
@RequestMapping("/task/repository/thumbnail-generation")
public class ThumbnailGenerationController extends TaskController {

    @RequestMapping("/{repositoryId}")
    public TaskDescription execute(
            @PathVariable(value = "repositoryId") int repositoryId) throws RepositoryDoesNotExistException, RepositoryIsDisabledException{
        
        ThumbnailGenerationParameters thumbnailPreviewParameters = new ThumbnailGenerationParameters(repositoryId);
        
        TaskDescription taskDescription = this.getTaskDescriptionFactory().create(TaskType.THUMBNAIL_GENERATION,thumbnailPreviewParameters);
        
        this.getQueueService().publish(taskDescription);
        
        return taskDescription;
    }
    
}
