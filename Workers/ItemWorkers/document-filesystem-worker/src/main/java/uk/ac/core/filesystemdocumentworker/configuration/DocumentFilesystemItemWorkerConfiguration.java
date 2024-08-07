package uk.ac.core.filesystemdocumentworker.configuration;

import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.ac.core.common.servlet.ServletCustomization;
import uk.ac.core.filesystemdocumentworker.worker.DocumentFilesystemWorker;
import uk.ac.core.singleitemworker.configuration.AbstractSingleItemWorkerConfiguration;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.configuration.QueueList;

@Configuration
@ComponentScan("uk.ac.core")
public class DocumentFilesystemItemWorkerConfiguration extends AbstractSingleItemWorkerConfiguration {
    
    @Override
    public QueueList getQueueNames() {
        QueueList queueList = new QueueList();
        queueList.add("document-filesystem-item-queue");
        queueList.add("zzempty-queue");
        return queueList;
    }
    
    @Override
    public QueueWorker getQueueWorker() {
        return new DocumentFilesystemWorker();
    }
    
    @Bean
    @Override
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, final MessageListener listenerAdapter, QueueList queueNames, final ServletCustomization servletCustomization) {
        SimpleMessageListenerContainer container = super.container(connectionFactory, listenerAdapter, queueNames, servletCustomization);
        container.setPrefetchCount(800);
        return container;
    }
    
}
