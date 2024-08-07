package uk.ac.core.worker.configuration;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import uk.ac.core.worker.QueueWorker;

/**
 *
 * @author lucasanastasiou
 */
public class CHARSMessageListener extends MessageListenerAdapter implements ChannelAwareMessageListener {

    QueueWorker queueWorker;
    
    public CHARSMessageListener(QueueWorker queueWorker){
        this.queueWorker = queueWorker;
    }
    
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        queueWorker.taskReceived(message.getBody(),channel, message.getMessageProperties().getDeliveryTag());
        //channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
