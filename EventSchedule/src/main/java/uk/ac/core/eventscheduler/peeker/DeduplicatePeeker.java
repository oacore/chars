/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.eventscheduler.peeker;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.queue.MultiPeekerListener;

/**
 *
 * @author samuel
 */
public class DeduplicatePeeker implements MultiPeekerListener {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(DeduplicatePeeker.class);
    
    final MessagePropertiesConverter propertiesConverter = new DefaultMessagePropertiesConverter();
    
    Map<String, Integer> list = new HashMap<>();
    
    @Override
    public void act(GetResponse response, Channel channel, List<Message> messages) throws Exception {
        Message message = new Message(response.getBody(), propertiesConverter.toMessageProperties(
                response.getProps(), response.getEnvelope(), "UTF-8"));
        
        String messageTaskString = new String(message.getBody());
        TaskDescription currentWorkingTask = new Gson().fromJson(messageTaskString, TaskDescription.class);
        String id = currentWorkingTask.getType().toString() + currentWorkingTask.getTaskParameters();
        if (this.list.containsKey(id)) {
            int count = this.list.get(id);
            logger.debug(currentWorkingTask.getTaskParameters() + "(" + currentWorkingTask.getType().toString() + ") is already in the queue: " + count + " times");
            this.list.put(id, count + 1);
            channel.basicAck(response.getEnvelope().getDeliveryTag(), false);
        } else {
            channel.basicNack(response.getEnvelope().getDeliveryTag(), true, true);
            this.list.put(id, 1);
        }
    }
    
}
