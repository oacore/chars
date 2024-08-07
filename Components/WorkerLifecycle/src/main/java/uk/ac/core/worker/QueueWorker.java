package uk.ac.core.worker;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import uk.ac.core.common.model.task.*;
import uk.ac.core.common.model.task.item.TaskItemEvent;
import uk.ac.core.common.servlet.ServletCustomization;
import uk.ac.core.common.util.DisconnectionListener;
import uk.ac.core.database.service.task.TaskDAO;
import uk.ac.core.database.service.updates.TaskUpdatesDAO;
import uk.ac.core.queue.QueueEventService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author mc26486
 */
public abstract class QueueWorker extends Worker {

    protected Boolean pause = Boolean.FALSE;
    protected Boolean stop = Boolean.FALSE;
    protected final Object pauseLock = new Object();
    @Autowired
    protected QueueEventService queueEventService;

    @Autowired
    protected WorkerStatus workerStatus;

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
        logger.info("Started worker with name: {}", this.workerName);
    }

    public void taskReceived(Object task, Channel channel,
            Long deliveryTag) {
        workerStatus.setChannel(channel);
        workerStatus.setDeliveryTag(deliveryTag);

        try {
            validToContinue();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // Fail fast, preventing any database corruption or other failures
            System.exit(2);
        }

        String taskString = new String((byte[]) task);
        System.out.println("taskString = " + taskString);
        this.currentWorkingTask = new Gson().fromJson(taskString, TaskDescription.class);
        setupLogging(currentWorkingTask.getUniqueId());
        this.currentWorkingTask.setStartTime(System.currentTimeMillis());
        System.out.println("taskDescription = " + currentWorkingTask);
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setTaskId(currentWorkingTask.getUniqueId());
        workerStatus.setTaskStatus(taskStatus);
        workerStatus.setCurrentTask(this.currentWorkingTask);
        workerStatus.setRunning(true);
        boolean taskOverallSuccess = false;
        try {
            /**
             * Step 1 indicate task is starting
             */
            prepare();
            /**
             * Step 2 notify the event queue
             */
            notifyStartTask(currentWorkingTask, taskStatus);
            /**
             * Step 3 retrieve items needed for run the task
             */
            List<TaskItem> taskItems = this.collectData();
            workerStatus.getTaskStatus().setNumberOfItemsToProcess(taskItems.size());
            /**
             * Step 4 process the items
             */
            List<TaskItemStatus> results = this.process(taskItems);
            /**
             * Step 5 collect final statistics
             */
            this.collectStatistics(results);

            /**
             * Step 6 evaluate task result
             */
            taskOverallSuccess = this.evaluate(results, taskItems);

        } catch (IllegalStateException e) {
            DisconnectionListener.halt(e);
        } catch (Exception e) {
            logger.error("Task finished with Exception: " + e.getMessage(), e);
        } finally {
            this.endPreEventNotification(taskStatus, taskOverallSuccess);
            finalEventNotification(taskStatus, taskOverallSuccess);
        }
    }

    public void prepare() {        
    }
    
    /**
     * Always called, regardless of task success, before the finalEventNotificaton is called.
     * 
     * @param taskStatus
     * @param taskOverallSuccess 
     */
    public void endPreEventNotification(TaskStatus taskStatus, boolean taskOverallSuccess) {
    }
    
    public void finalEventNotification(TaskStatus taskStatus, boolean taskOverallSuccess) {
        /**
         * Step 5 notify the event queue with FINISH
         */
        taskStatus.setSuccess(taskOverallSuccess);
        this.currentWorkingTask.setEndTime(System.currentTimeMillis());
        this.workerStatus.setRunning(false);
        this.notifyEndTask(currentWorkingTask, taskStatus);
        this.collectTaskStatistics();

        logger.info("I reached the very end , i should now ACK");
    }

    public Boolean getPause() {
        return pause;
    }

    public Boolean getStop() {
        return stop;
    }

    public String getWorkerName() {
        if (workerName == null) {
            workerName = this.servletCustomization.getNodeName();

        }
        return workerName;
    }

    protected void notifyEndTask(TaskDescription taskDescription, TaskStatus taskStatus) {
        notifyQueue(taskDescription, taskStatus, "FINISH");
    }

    protected void notifyQueue(TaskDescription taskDescription, TaskStatus taskStatus, String event) {
        TaskEvent taskEvent = new TaskEvent();
        taskEvent.setTaskDescription(taskDescription);
        taskEvent.setTime(System.currentTimeMillis());
        taskEvent.setTaskStatus(taskStatus);
        taskEvent.setEvent(event);
        queueEventService.publishTaskEvent(taskEvent);
    }

    public void notifyItemQueue(TaskDescription taskItemDescription, TaskItemStatus taskItemStatus, String event) {

        TaskItemEvent taskItemEvent = new TaskItemEvent();
        taskItemEvent.setTaskItemDescription(taskItemDescription);
        taskItemEvent.setTime(System.currentTimeMillis());
        taskItemEvent.setTaskItemStatus(taskItemStatus);
        taskItemEvent.setEvent(event);

        queueEventService.publishTaskItemEvent(taskItemEvent);

    }

    protected void notifyStartTask(TaskDescription taskDescription, TaskStatus taskStatus) {
        notifyQueue(taskDescription, taskStatus, "START");
    }

    @Override
    public void pause() {
        workerStatus.setStatus("pausing");
        this.pause = true;
    }

    public void setPause(Boolean pause) {
        this.pause = pause;
    }

    public void setStop(Boolean stop) {
        this.stop = stop;
    }

    @Override
    public void start() {
        synchronized (pauseLock) {
            this.pause = false;
            this.pause.notify();
        }
    }

    @Override
    public void stop() {
        workerStatus.setStatus("stopping");
    }

    @Override
    public void drop() {
        workerStatus.setStatus("dropping");
        try {
            workerStatus.getChannel().basicAck(workerStatus.getDeliveryTag(), false);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(QueueWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        finalEventNotification(workerStatus.getTaskStatus(), false);
        this.stop();
    }

    protected void waitOnPause() {
        synchronized (pauseLock) {
            try {
                this.pause.wait();
            } catch (InterruptedException ex) {

            }
        }
    }

    @Override
    public boolean evaluate(List<TaskItemStatus> results, List<TaskItem> taskItems) {
        return workerStatus.getTaskStatus().getSuccessfulCount() > 0;
    }

    public QueueEventService getQueueEventService() {
        return queueEventService;
    }

    public void setQueueEventService(QueueEventService queueEventService) {
        this.queueEventService = queueEventService;
    }

    public WorkerStatus getWorkerStatus() {
        return workerStatus;
    }

    public void setWorkerStatus(WorkerStatus workerStatus) {
        this.workerStatus = workerStatus;
    }

    private void collectTaskStatistics() {
        this.taskDAO.saveTaskHistory(currentWorkingTask, workerStatus.getTaskStatus(), this.workerName);

        this.taskUpdatesDAO.saveTaskUpdate(workerStatus.getTaskStatus(), currentWorkingTask);
    }

    /**
     * Validates the system is correct (mountpoints are valid, database connection etc)
     *
     * todo: consider other checks such as database or elasticsearch validation
     * @return true if the system checks pass
     */
    private boolean validToContinue() throws Exception {
        if (!new File("/data/remote/tmp").exists()) {
            throw new Exception("/data/remote/tmp is not readable. Possible /data/remote mount failure");
        }
        return true;
    }

}
