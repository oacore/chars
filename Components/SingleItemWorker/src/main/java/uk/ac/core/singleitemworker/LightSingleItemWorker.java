package uk.ac.core.singleitemworker;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.servlet.ServletCustomization;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.WorkerProgress;

/**
 *
 * @author lucasanastasiou
 */
public abstract class LightSingleItemWorker extends QueueWorker {

    protected Boolean pause = Boolean.FALSE;
    protected Boolean stop = Boolean.FALSE;
    protected final Object pauseLock = new Object();
    private static final Logger logger = LoggerFactory.getLogger(LightSingleItemWorker.class);

    @Autowired
    protected SingleItemWorkerStatus workerStatus;
    
    @Autowired
    WorkerProgress workerProgress;
    
    private String workerName;

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private ServletCustomization servletCustomization;

    @PostConstruct
    public void init() {
        this.workerName = this.servletCustomization.getNodeName();
        logger.info("Started worker with name: {0}", this.workerName);
        
        this.workerStatus.setCurrentTask(currentWorkingTask);
        
        this.workerProgress.setTimeStarted(new Date().getTime());
    }

    public void taskReceived(Object task, Channel channel,
            Long deliveryTag) {
        workerStatus.setChannel(channel);
        workerStatus.setDeliveryTag(deliveryTag);
        
        String taskString = new String((byte[]) task);
        this.currentWorkingTask = new Gson().fromJson(taskString, TaskDescription.class);
        
        this.currentWorkingTask.setStartTime(System.currentTimeMillis());
        
        workerStatus.setCurrentTask(this.currentWorkingTask);
        workerStatus.setRunning(true);
        boolean taskOverallSuccess = false;
        try {
            /**
             * Step one and only one : process!
             */

            long t0 = System.currentTimeMillis();
            
            TaskItemStatus taskItemStatus = this.process(currentWorkingTask);
            
            long t1 = System.currentTimeMillis();
            long itemDuration = t1-t0;
            this.workerProgress.incProcessedCount();
            this.workerProgress.addItemDurationTime(itemDuration);

        } catch (Exception e) {
            logger.info("Task finished with Exception", e);

        } finally {


        }
    }

    /**
     * this is THE function that differentiates operations of each worker
     *
     * @return
     */
    public abstract TaskItemStatus process(TaskDescription taskItemDescription);

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
}
