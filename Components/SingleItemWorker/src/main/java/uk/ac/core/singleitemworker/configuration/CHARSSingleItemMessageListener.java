package uk.ac.core.singleitemworker.configuration;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import uk.ac.core.singleitemworker.SingleItemWorker;
/**
 *
 * @author lucasanastasiou
 */
public class CHARSSingleItemMessageListener extends MessageListenerAdapter implements ChannelAwareMessageListener {

    SingleItemWorker queueWorker;
    
    public CHARSSingleItemMessageListener(SingleItemWorker queueWorker){
        this.queueWorker = queueWorker;
    }
    
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        queueWorker.taskReceived(message.getBody(),channel, message.getMessageProperties().getDeliveryTag());
    }

}
