package uk.ac.core.workers.item.textextraction.configuration;

import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.ac.core.common.servlet.ServletCustomization;
import uk.ac.core.database.configuration.DatabaseConfig;
import uk.ac.core.singleitemworker.configuration.AbstractSingleItemWorkerConfiguration;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.configuration.QueueList;
import uk.ac.core.workers.item.textextraction.TextExtractionWorker;

/**
 *
 * @author lucasanastasiou
 */
@Configuration
@ComponentScan("uk.ac.core")
@Import({DatabaseConfig.class})
public class TextExtractionApplicationConfiguration extends AbstractSingleItemWorkerConfiguration {

    @Override
    public QueueList getQueueNames() {
        QueueList queueList = new QueueList();
        queueList.add("extract-text-item-queue");
        return queueList;
    }

    @Override
    public QueueWorker getQueueWorker() {
        return new TextExtractionWorker();
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
