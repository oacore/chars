package uk.ac.core.supervisor.controller.tasks;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.HarvestTaskParameters;
import uk.ac.core.supervisor.controller.TaskController;
import uk.ac.core.supervisor.exceptions.RepositoryDoesNotExistException;
import uk.ac.core.supervisor.exceptions.RepositoryIsDisabledException;

/**
 *
 * @author samuel
 */
@RestController
@RequestMapping("/task/repository/harvest")
public class HarvestRepositoryController extends TaskController {

    // this is the pattern to be used in URLs Date parameters
    final static private String PATH_VARIABLE_DATE_PATTERN = "yyyy-MM-dd";

    @RequestMapping("/{repositoryId}")
    public TaskDescription execute(
            @PathVariable(value = "repositoryId") int repositoryId) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {

        return this.execute(repositoryId, 0, null);
    }

    @RequestMapping("/{repositoryId}/fromdate/{fromDate}")
    public TaskDescription execute(
            @PathVariable(value = "repositoryId") int repositoryId,
            @PathVariable(value = "fromDate") @DateTimeFormat(pattern = PATH_VARIABLE_DATE_PATTERN) Date fromDate) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {
        return this.execute(repositoryId, 0, fromDate);
    }

    @RequestMapping("/{repositoryId}/fromdate/{fromDate}/todate/{toDate}")
    public TaskDescription execute(
            @PathVariable(value = "repositoryId") int repositoryId,
            @PathVariable(value = "fromDate")@DateTimeFormat(pattern = PATH_VARIABLE_DATE_PATTERN) Date fromDate,
            @PathVariable(value = "toDate")@DateTimeFormat(pattern = PATH_VARIABLE_DATE_PATTERN) Date toDate)
                throws RepositoryDoesNotExistException, RepositoryIsDisabledException {
        return this.executeStartingFrom(TaskType.METADATA_DOWNLOAD.getName(), repositoryId, 0, fromDate, toDate);
    }

    @RequestMapping("/{repositoryId}/priority/{priority}/fromdate/{fromDate}")
    public TaskDescription execute(
            @PathVariable(value = "repositoryId") int repositoryId, @PathVariable(value = "priority") int priority, @PathVariable(value = "fromDate") @DateTimeFormat(pattern = PATH_VARIABLE_DATE_PATTERN) Date fromDate) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {

        return this.executeStartingFrom(TaskType.METADATA_DOWNLOAD.getName(), repositoryId, priority, fromDate);
    }

    @RequestMapping("/{repositoryId}/priority/{priority}")
    public TaskDescription execute(
            @PathVariable(value = "repositoryId") int repositoryId, 
            @PathVariable(value = "priority") int priority) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {

        return this.executeStartingFrom(TaskType.METADATA_DOWNLOAD.getName(), repositoryId, priority, null);
    }

    @RequestMapping("/start_from/{type}/{repositoryId}")
    public TaskDescription executeStartingFrom(
            @PathVariable(value = "type") String taskType, @PathVariable(value = "repositoryId") int repositoryId) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {

        return this.executeStartingFrom(taskType, repositoryId, 0, null);
    }

    
    @RequestMapping("/start_from/{type}/{repositoryId}/priority/{priority}/fromdate/{fromDate}")
    public TaskDescription executeStartingFrom(
            @PathVariable(value = "type") String taskType, @PathVariable(value = "repositoryId") int repositoryId, @PathVariable(value = "priority") int priority,
            @PathVariable(value = "fromDate") @DateTimeFormat(pattern = PATH_VARIABLE_DATE_PATTERN) Date fromDate)
            throws RepositoryDoesNotExistException, RepositoryIsDisabledException {
        HarvestTaskParameters harvestTaskParameters = new HarvestTaskParameters(repositoryId, fromDate);
        TaskType fromType = TaskType.fromString(taskType);
        TaskDescription taskDescription = this.getTaskDescriptionFactory().createWorkflowFrom(fromType, harvestTaskParameters, priority);
        this.getQueueService().publish(taskDescription);

        return taskDescription;
    }

    @RequestMapping("/start_from/{type}/{repositoryId}/priority/{priority}/fromdate/{fromDate}/toDate/{toDate}")
    public TaskDescription executeStartingFrom(
            @PathVariable(value = "type") String taskType, @PathVariable(value = "repositoryId") int repositoryId,
            @PathVariable(value = "priority") int priority,
            @PathVariable(value = "fromDate") @DateTimeFormat(pattern = PATH_VARIABLE_DATE_PATTERN) Date fromDate,
            @PathVariable(value = "toDate") @DateTimeFormat(pattern = PATH_VARIABLE_DATE_PATTERN) Date toDate)
            throws RepositoryDoesNotExistException, RepositoryIsDisabledException {

        HarvestTaskParameters harvestTaskParameters = new HarvestTaskParameters(repositoryId, fromDate, toDate);
        TaskType fromType = TaskType.fromString(taskType);
        TaskDescription taskDescription = this.getTaskDescriptionFactory().createWorkflowFrom(fromType, harvestTaskParameters, priority);
        this.getQueueService().publish(taskDescription);

        return taskDescription;
    }

}
