package uk.ac.core.workers.item.pdf.configuration;

import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.ac.core.common.servlet.ServletCustomization;
import uk.ac.core.singleitemworker.configuration.AbstractSingleItemWorkerConfiguration;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.configuration.QueueList;
import uk.ac.core.workers.item.pdf.PdfDecorateItemWorker;

@Configuration
@ComponentScan("uk.ac.core")
public class DecoratePdfConfiguration extends AbstractSingleItemWorkerConfiguration {
    
    @Override
    public QueueList getQueueNames() {
        QueueList queueList = new QueueList();
        queueList.add("pdf-decorate-item-queue");
        //queueList.add("zzempty-queue");
        return queueList;
    }
    
    @Override
    public QueueWorker getQueueWorker() {
        return new PdfDecorateItemWorker();
    }

    @Bean
    @Override
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListener listenerAdapter, QueueList queueNames, ServletCustomization servletCustomization) {
        SimpleMessageListenerContainer container =  super.container(connectionFactory, listenerAdapter, queueNames, servletCustomization);
        container.setPrefetchCount(100);
        return container;
    }
}
