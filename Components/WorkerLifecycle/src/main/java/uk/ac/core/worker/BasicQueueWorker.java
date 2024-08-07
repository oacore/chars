package uk.ac.core.worker;

import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.util.DisconnectionListener;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mc26486
 */
public abstract class BasicQueueWorker extends QueueWorker {

    @Override
    public abstract List<TaskItem> collectData();

    @Override
    public List<TaskItemStatus> process(List<TaskItem> taskItems) {
        List<TaskItemStatus> taskItemStatuses = new ArrayList<TaskItemStatus>();
        /**
         * Step 3 process the item singularly
         */
        for (TaskItem item : taskItems) {
            TaskItemStatus taskItemStatus;
            try {
                taskItemStatus = processSingleItem(item);
                taskItemStatuses.add(taskItemStatus);
            } catch (Exception e) {
                if (e instanceof IllegalStateException) {
                    DisconnectionListener.halt(e);
                }
                logger.error(e.getMessage(), e);
                taskItemStatus = new TaskItemStatus();
                taskItemStatus.setSuccess(false);               
                taskItemStatuses.add(taskItemStatus);
            }

            workerStatus.getTaskStatus().incProcessed();
            if (taskItemStatus.isSuccess()) {
                workerStatus.getTaskStatus().incSuccessful();
            }
            /**
             * Step 3ex: handling of start, pause, stop
             */
            if (this.getPause().equals(Boolean.TRUE)) {
                waitOnPause();
            }
            if (this.getStop().equals(Boolean.TRUE)) {
                break;
            }
        }
        return taskItemStatuses;
    }

    public abstract TaskItemStatus processSingleItem(TaskItem item);

    @Override
    public abstract void collectStatistics(List<TaskItemStatus> results);

    @Override
    public boolean evaluate(List<TaskItemStatus> results, List<TaskItem> taskItems) {
        return super.evaluate(results, taskItems);
    }

}
