package uk.ac.core.supervisor.factories;

import com.google.gson.Gson;
import java.util.List;
import java.util.UUID;

import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.HarvestWorkflow;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.HarvestTaskParameters;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.common.model.task.parameters.TaskParameters;
import uk.ac.core.supervisor.exceptions.RepositoryDoesNotExistException;
import uk.ac.core.supervisor.exceptions.RepositoryIsDisabledException;
import uk.ac.core.supervisor.validation.RepositoryValidator;

/**
 *
 * @author samuel
 */
@Service
public class TaskDescriptionFactory {

    @Autowired
    RepositoryValidator repositoryValidator;

    /**
     *
     */
    public TaskDescriptionFactory() {
    }

    /**
     * Builds a TaskDescription based on the taskParameters.
     *
     * @param taskName the name of the task
     * @param taskParameters the payload that will be passed to the worker with the TaskDescription
     * @return a taskDescription which can then be put into a queue
     */
    public TaskDescription create(TaskType taskType, TaskParameters taskParameters) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {

        RepositoryTaskParameters repositoryTaskParameters = (RepositoryTaskParameters) taskParameters;
        repositoryValidator.validate(repositoryTaskParameters.getRepositoryId());

        TaskDescription taskDescription = new TaskDescription();
        taskDescription.setUniqueId(UUID.randomUUID().toString());
        taskDescription.setType(taskType);
        taskDescription.setTaskParameters(new Gson().toJson(taskParameters));
        taskDescription.setRoutingKey(taskType.getName());

        return taskDescription;
    }
    
    public TaskDescription create(TaskType taskType, TaskParameters taskParameters,int priority) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {
        TaskDescription taskDescription = this.create(taskType, taskParameters);
        taskDescription.setPriority(priority);
        return taskDescription;
    }

    public TaskDescription createWorkflowFrom(TaskType fromType, HarvestTaskParameters harvestTaskParameters, Integer priority) throws RepositoryDoesNotExistException, RepositoryIsDisabledException {
        RepositoryTaskParameters repositoryTaskParameters = (RepositoryTaskParameters) harvestTaskParameters;
        repositoryValidator.validate(repositoryTaskParameters.getRepositoryId());

        TaskDescription taskDescription = new TaskDescription();
        taskDescription.setUniqueId(UUID.randomUUID().toString());
        taskDescription.setType(fromType);
        taskDescription.setTaskParameters(new GsonBuilder().setDateFormat("yyyy-MM-dd").create().toJson(harvestTaskParameters));
        taskDescription.setRoutingKey(fromType.getName());
        taskDescription.setPriority(priority);

        List<TaskType> taskList = new HarvestWorkflow().getWorkflowFrom(fromType);
        taskDescription.setTaskList(taskList);

        return taskDescription;
    }
}
