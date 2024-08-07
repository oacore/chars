package uk.ac.core.itemeventscheduler.configuration;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.ac.core.common.servlet.ServletCustomization;
import uk.ac.core.itemeventscheduler.broker.ItemEventSchedulerQueueBroker;
import uk.ac.core.queue.QueueItemService;

/**
 *
 * @author lucasanastasiou
 */
@Configuration
@ComponentScan({"uk.ac.core.queue"})
public class ItemEventSchedulerConfiguration {

    @Bean
    public ItemEventSchedulerQueueBroker itemEventSchedulerQueueBroker(QueueItemService queueItemService) {
        return new ItemEventSchedulerQueueBroker(queueItemService);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter, final ServletCustomization servletCustomization) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("item-events-queue");
        container.setMessageListener(listenerAdapter);

        container.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return servletCustomization.getNodeName() + "#" + queue;
            }
        });
        container.setPrefetchCount(5000);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(ItemEventSchedulerQueueBroker eventSchedulerQueueBroker) {
        System.out.println("queueWorker = " + eventSchedulerQueueBroker);
        return new MessageListenerAdapter(eventSchedulerQueueBroker, "eventReceived");
    }

}
