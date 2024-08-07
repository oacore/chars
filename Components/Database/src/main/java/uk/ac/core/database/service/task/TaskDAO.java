package uk.ac.core.database.service.task;

import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskStatus;

import java.util.List;

/**
 *
 * @author mc26486
 */
public interface TaskDAO {

    public TaskDescription loadTaskById(String uniqueId);

    public TaskStatus loadTaskStatusById(String uniqueId);

    public boolean saveTask(TaskDescription task);
    
    public boolean saveTaskHistory(TaskDescription task, TaskStatus taskStatus, String workerName);

    public List<TaskDescription> findSuccessfulTasksByPeriod(String type, int periodInDays);

    public boolean saveSingleItemTask(TaskDescription taskItemDescription, TaskItemStatus taskItemStatus, String workerName);
    
    public boolean saveTaskStatus(TaskStatus taskStatus);

}
