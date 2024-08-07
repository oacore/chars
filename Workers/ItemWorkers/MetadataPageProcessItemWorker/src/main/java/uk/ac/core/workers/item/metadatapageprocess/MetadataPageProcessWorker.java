package uk.ac.core.workers.item.metadatapageprocess;

import com.google.gson.Gson;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.singleitemworker.SingleItemWorker;
import com.rabbitmq.client.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;

/**
 *
 * @author Samuel Pearce
 */
public class MetadataPageProcessWorker extends SingleItemWorker {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MetadataPageProcessWorker.class);

    private final List<Integer> lastItems = new ArrayList<>(10);

    ExecutorService executor;

    public MetadataPageProcessWorker() {        
    }

    
    @Override
    public void taskReceived(Object task, @Header(AmqpHeaders.CHANNEL) Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) {
        workerStatus.setChannel(channel);
        workerStatus.setDeliveryTag(deliveryTag);
        super.taskReceived(task, channel, deliveryTag);        
    }

    @Override
    public TaskItemStatus process(TaskDescription taskDescription) {
        String params = taskDescription.getTaskParameters();
        SingleItemTaskParameters singleItemTaskParameters = new Gson().fromJson(params, SingleItemTaskParameters.class);
        final Integer articleId = singleItemTaskParameters.getArticle_id();
        boolean success = true;
        

        
        TaskItemStatus taskItemStatus = new TaskItemStatus();
        taskItemStatus.setSuccess(success);
        taskItemStatus.setTaskId(taskDescription.getUniqueId());
        return taskItemStatus;
    }

}
