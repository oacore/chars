package uk.ac.core.worker;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;

/**
 *  
 * @author mc26486
 */
public abstract class Worker {

    protected static final Logger logger = LoggerFactory.getLogger(QueueWorker.class);
    public TaskDescription currentWorkingTask;

    /**
     * THIS IS THE LIFECYCLE OF A TASK collectData processSingleItem
     * collectStatistics
     *
     * @return
     */
    public abstract List<TaskItem> collectData();

    public abstract void collectStatistics(List<TaskItemStatus> results);

    public abstract List<TaskItemStatus> process(List<TaskItem> taskItems);

    protected void setupLogging(String uniqueId) {
        MDC.put("taskUniqueId", uniqueId);
    }

    public abstract boolean evaluate(List<TaskItemStatus> results, List<TaskItem> taskItems);

    public abstract void drop();

    public abstract void pause();

    public abstract void start();

    public abstract void stop();

    public TaskDescription getCurrentWorkingTask() {
        return currentWorkingTask;
    }

    public void setCurrentWorkingTask(TaskDescription currentWorkingTask) {
        this.currentWorkingTask = currentWorkingTask;
    }
           

}
