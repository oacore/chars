package uk.ac.core.queue;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.core.queue.json.model.Consumer;


/**
 * Service for basic info of queue
 *
 * @author lucasanastasiou
 */
@Component
public class QueueInfoService {

    @Autowired
    RabbitAdmin rabbitAdmin;

    /**
     * Returns number of messages in queue
     *
     * @param queueName
     * @return
     */
    public Integer getCountMessages(String queueName) {
        return Integer.parseInt(rabbitAdmin.getQueueProperties(queueName).get("QUEUE_MESSAGE_COUNT").toString());
    }

    /**
     * Returns the number of consumers registered to a queue
     *
     * @param queueName
     * @return
     */
    public Integer getConsumerCount(String queueName) {
        return Integer.parseInt(rabbitAdmin.getQueueProperties(queueName).get("QUEUE_CONSUMER_COUNT").toString());
    }

    /**
     * Looks without consuming the first message in the given queue. To be used
     * with caution as message will remain in queue but may be re-queued in the
     * end
     *
     * @param queueName The name of the queue to peek
     */
    public Message lookFirstMessageInQueue(String queueName) {
        SinglePeeker peeker = new SinglePeeker(queueName);
        Message peekedMessage = rabbitAdmin.getRabbitTemplate().execute(peeker);
        return peekedMessage;
    }

    /**
     * Get all the messages in a queue and then re-queue them (by NACK-ing
     * them). To be used with extra caution as it may lead to race-conditions or
     * order of messages may change after invoking this
     *
     * @param queueName
     * @return
     */
    public List<Message> lookMessagesInQueue(String queueName) {
        final MessagePropertiesConverter propertiesConverter = new DefaultMessagePropertiesConverter();
        MultiPeeker peeker = new MultiPeeker(queueName, new MultiPeekerListener() {
            @Override
            public void act(GetResponse response, Channel channel, List<Message> messages) throws Exception {
                channel.basicNack(response.getEnvelope().getDeliveryTag(), true, true);
                messages.add(new Message(response.getBody(), propertiesConverter.toMessageProperties(
                response.getProps(), response.getEnvelope(), "UTF-8")));
            }
        });
        List<Message> messages = rabbitAdmin.getRabbitTemplate().execute(peeker);
        return messages;
    }
    
    public List<Message> lookMessagesInQueue(String queueName, MultiPeekerListener listener) {
        List<Message> messages = rabbitAdmin.getRabbitTemplate().execute(new MultiPeeker(queueName, listener));
        return messages;
    }
    

    /**
     * Get workers (registered consumers) of a queue as a list of url endpoints
     */
    public List<String> getWorkers(String queueName) {
        List<String> workersEndpoints = new ArrayList<>();
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials("guest", "guest");
        provider.setCredentials(AuthScope.ANY, credentials);
        HttpClient httpClient = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(provider).build();
        HttpGet httpGet = new HttpGet("http://queue-admin.core.ac.uk/api/consumers");
        HttpEntity entity1 = null;
        try {
            HttpResponse response1 = httpClient.execute(httpGet);
            entity1 = response1.getEntity();
            String response = IOUtils.toString(entity1.getContent(), "UTF-8");
            Gson gson = new Gson();
            List<Consumer> consumerList = gson.fromJson(response, new TypeToken<List<Consumer>>() {
            }.getType());

            for (Consumer consumer : consumerList) {
                if (consumer.getQueue().getName().equals(queueName)) {
//                    System.out.println("endpoint : " + consumer.getConsumerTag());
                    workersEndpoints.add(consumer.getConsumerTag());
                }
            }
        } catch (IOException ioe) {
            System.out.println("ioe" + ioe.getMessage());
            return null;
        } finally {
            EntityUtils.consumeQuietly(entity1);
        }
        return workersEndpoints;
    }

}
