package uk.ac.core.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author mc26486
 */
public class AbstractQueueEvent {
    
    @Autowired
    RabbitTemplate rabbitTemplate;
        
    protected void convertAndSend(String exchange, String routingKey, Object object, final int priority) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
        String taskString = gson.toJson(object);

        rabbitTemplate.convertAndSend(exchange, routingKey, taskString, new MessagePostProcessor() {

            @Override
            public Message postProcessMessage(Message msg) throws AmqpException {
                msg.getMessageProperties().setPriority(priority);
                return msg;
            }
        });

        System.out.println(" [x] Sent '" + taskString + "'");
   
    }
}
