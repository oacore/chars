package uk.ac.core.singleitemworker;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.item.TaskItemEvent;
import uk.ac.core.queue.QueueEventService;
import uk.ac.core.database.service.task.TaskDAO;
import uk.ac.core.database.service.updates.TaskUpdatesDAO;
import uk.ac.core.common.servlet.ServletCustomization;
import uk.ac.core.worker.QueueWorker;

/**
 *
 * @author lucasanastasiou
 */
public abstract class SingleItemWorker extends QueueWorker {

    protected Boolean pause = Boolean.FALSE;
    protected Boolean stop = Boolean.FALSE;
    protected final Object pauseLock = new Object();
    private static final Logger logger = LoggerFactory.getLogger(SingleItemWorker.class);

    @Autowired
    protected QueueEventService queueEventService;

    @Autowired
    protected SingleItemWorkerStatus workerStatus;

    @Autowired
    protected TaskDAO taskDAO;

    @Autowired
    protected TaskUpdatesDAO taskUpdatesDAO;

    private String workerName;

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private ServletCustomization servletCustomization;

    @PostConstruct
    public void init() {
        this.workerName = this.servletCustomization.getNodeName();
        logger.info("Started worker with name: {0}", this.workerName);
    }

    public void taskReceived(Object task, Channel channel,
            Long deliveryTag) {
        workerStatus.setChannel(channel);
        workerStatus.setDeliveryTag(deliveryTag);

        String taskString = new String((byte[]) task);
        this.currentWorkingTask = new Gson().fromJson(taskString, TaskDescription.class);

        //setupLogging(currentWorkingTask.getUniqueId());
        this.currentWorkingTask.setStartTime(System.currentTimeMillis());

        TaskItemStatus taskItemStatus = new TaskItemStatus();
        taskItemStatus.setTaskId(currentWorkingTask.getUniqueId());

        workerStatus.setTaskStatus(taskItemStatus);
        workerStatus.setCurrentTask(this.currentWorkingTask);
        workerStatus.setRunning(true);
        boolean taskOverallSuccess = false;
        try {
            /**
             * Step 1 notify the event queue
             */
            notifyStartTask(currentWorkingTask, taskItemStatus);
            /**
             * Step 2 process item
             */

            taskItemStatus = this.process(currentWorkingTask);

        } catch (Exception e) {
            logger.info("Task finished with Exception", e);

        } finally {

            finalEventNotification(taskItemStatus);

        }
    }

    @Override
    public List<TaskItem> collectData() {
        throw new UnsupportedOperationException("If you are using a single item worker you should never get here!!!");
    }

    @Override
    public void collectStatistics(List<TaskItemStatus> results) {
        throw new UnsupportedOperationException("If you are using a single item worker you should never get here!!!");
    }

    @Override
    public List<TaskItemStatus> process(List<TaskItem> taskItems) {
        throw new UnsupportedOperationException("If you are using a single item worker you should never get here!!!");
    }

    /**
     * this is THE function that differentiates operations of each worker
     *
     * @return
     */
    public abstract TaskItemStatus process(TaskDescription taskItemDescription);

    private void finalEventNotification(TaskItemStatus taskItemStatus) {
        /**
         * Step 3 notify the event queue with FINISH
         */
        this.currentWorkingTask.setEndTime(System.currentTimeMillis());
        this.workerStatus.setRunning(false);
        this.notifyEndTask(currentWorkingTask, taskItemStatus);
        this.recordTaskUpdates(taskItemStatus);
    }

    protected void notifyEndTask(TaskDescription taskDescription, TaskItemStatus taskItemStatus) {
        notifyItemQueue(taskDescription, taskItemStatus, "FINISH");
    }

    protected void notifyStartTask(TaskDescription taskDescription, TaskItemStatus taskItemStatus) {
        notifyItemQueue(taskDescription, taskItemStatus, "START");
    }

    protected void recordTaskUpdates(TaskItemStatus taskItemStatus) {
        this.taskDAO.saveSingleItemTask(currentWorkingTask, taskItemStatus, workerName);
        this.taskUpdatesDAO.saveSingleItemTaskUpdate(currentWorkingTask, taskItemStatus);
    }

    @Override
    public void setupLogging(String uniqueId) {

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);

        MDC.put("taskUniqueId", uniqueId);
        StatusPrinter.print(lc);

        // print logback's internal status
    }

}
