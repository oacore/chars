package uk.ac.core.queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import java.util.List;
import org.springframework.amqp.core.Message;

/**
 * Callback class for receiving message from queue. It peeks all the message in the queue and rejects it (which leads to be requeued)
 * 
 * @author Samuel Pearce
 */
public interface MultiPeekerListener {
   void act(GetResponse response, Channel channel, List<Message> messages) throws Exception;
}