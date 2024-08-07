package uk.ac.core.configuration;

import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.ac.core.common.servlet.ServletCustomization;
import uk.ac.core.singleitemworker.configuration.AbstractSingleItemWorkerConfiguration;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.configuration.QueueList;
import uk.ac.core.worker.WorksIndexItemWorker;

/**
 *
 * @author MTarasyuk
 */
@Configuration
@ComponentScan("uk.ac.core")
public class WorksItemIndexConfiguration extends AbstractSingleItemWorkerConfiguration {

    @Value("${queue.name:works-index-item-queue}")
    public String queueName;

    @Override
    public QueueList getQueueNames() {
        QueueList queueList = new QueueList();
        queueList.add(queueName);
        queueList.add("zzempty-queue");
        return queueList;
    }
    
    @Override
    public QueueWorker getQueueWorker() {
        return new WorksIndexItemWorker();
    }
    
    @Bean
    @Override
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, final MessageListener listenerAdapter, QueueList queueNames, final ServletCustomization servletCustomization) {
        SimpleMessageListenerContainer container = super.container(connectionFactory, listenerAdapter, queueNames, servletCustomization);
        container.setPrefetchCount(800);
        System.out.println("container = " + container);
        return container;
    }
    
}
