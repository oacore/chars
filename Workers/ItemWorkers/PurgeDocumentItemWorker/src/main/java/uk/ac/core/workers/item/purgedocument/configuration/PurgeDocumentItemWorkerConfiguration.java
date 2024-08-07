package uk.ac.core.workers.item.purgedocument.configuration;

import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.servlet.ServletCustomization;
import uk.ac.core.singleitemworker.configuration.AbstractSingleItemWorkerConfiguration;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.configuration.QueueList;
import uk.ac.core.workers.item.purgedocument.PurgeDocumentWorker;

/**
 *
 */
@Configuration
@ComponentScan("uk.ac.core")
public class PurgeDocumentItemWorkerConfiguration extends AbstractSingleItemWorkerConfiguration {
    
    @Override
    public QueueList getQueueNames() {
        QueueList queueList = new QueueList();
        queueList.add(TaskType.PURGE_DOCUMENT_ITEM.toString().replace("_", "-").toLowerCase() + "-queue");
        return queueList;
    }
    
    @Override
    public QueueWorker getQueueWorker() {
        return new PurgeDocumentWorker();
    }
    
    @Bean
    @Override
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, final MessageListener listenerAdapter, QueueList queueNames, final ServletCustomization servletCustomization) {
        SimpleMessageListenerContainer container = super.container(connectionFactory, listenerAdapter, queueNames, servletCustomization);
        container.setPrefetchCount(5);
        System.out.println("container = " + container);
        return container;
    }
    
}
