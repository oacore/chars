package uk.ac.core.workers.item.metadatapageprocess.configuration;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.ac.core.common.servlet.ServletCustomization;
import uk.ac.core.singleitemworker.configuration.AbstractSingleItemWorkerConfiguration;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.configuration.QueueList;
import uk.ac.core.workers.item.metadatapageprocess.MetadataPageProcessWorker;

/**
 *
 * @author Samuel Pearce
 */
@Configuration
@ComponentScan("uk.ac.core")
public class MetadataPageProcessItemConfiguration extends AbstractSingleItemWorkerConfiguration {
    
    @Override
    public QueueList getQueueNames() {
        QueueList queueList = new QueueList();
        queueList.add("metadatapageprocess-item-queue");
        return queueList;
    }
    
    @Override
    public QueueWorker getQueueWorker() {
        return new MetadataPageProcessWorker();
    }
    
    @Bean
    @Override
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, final MessageListener listenerAdapter, QueueList queueNames, final ServletCustomization servletCustomization) {
        SimpleMessageListenerContainer container = super.container(connectionFactory, listenerAdapter, queueNames, servletCustomization);
        container.setPrefetchCount(100);
        System.out.println("container = " + container);
        return container;
    }
    
}
