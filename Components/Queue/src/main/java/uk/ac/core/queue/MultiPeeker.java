package uk.ac.core.queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;

/**
 * Callback class for receiving message from queue. It peeks all the message in the queue and rejects it (which leads to be requeued)
 * 
 * @author lucasanastasiou 
 */
class MultiPeeker implements ChannelCallback<List<Message>> {

    final String queue;
    
    final MultiPeekerListener listener;

    public MultiPeeker(String queue, MultiPeekerListener listener) {
        this.queue = queue;
        this.listener = listener;
    }

    @Override
    public List<Message> doInRabbit(Channel channel) throws Exception {
        GetResponse result = null;
        List<GetResponse> responses = new ArrayList<>();
        List<Message> messages = new ArrayList<>();
        while (true) {
            result = channel.basicGet(this.queue, false);

            if (result == null) {
                break;
            }
            responses.add(result);

        }

        for (GetResponse response : responses) {
            this.listener.act(response, channel, messages);
        }
        return messages;

    }
}
