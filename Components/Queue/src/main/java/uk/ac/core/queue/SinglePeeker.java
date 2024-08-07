package uk.ac.core.queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;

/**
 * Callback class for receiving message from queue. It peeks the message and rejects it (which leads
 * to be re-queued)
 *
 * @author lucasanastasiou
 */
class SinglePeeker implements ChannelCallback<Message> {

    final MessagePropertiesConverter propertiesConverter = new DefaultMessagePropertiesConverter();

    final String queue;

    public SinglePeeker(String queue) {
        this.queue = queue;
    }

    @Override
    public Message doInRabbit(Channel channel) throws Exception {
        GetResponse result = channel.basicGet(this.queue, false);
        if (result == null) {
            return null;
        }
        channel.basicReject(result.getEnvelope().getDeliveryTag(), true);
        return new Message(result.getBody(), propertiesConverter.toMessageProperties(
                result.getProps(), result.getEnvelope(), "UTF-8"));
    }
}
