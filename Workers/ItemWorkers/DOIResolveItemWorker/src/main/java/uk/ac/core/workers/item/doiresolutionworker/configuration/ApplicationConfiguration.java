package uk.ac.core.workers.item.doiresolutionworker.configuration;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Scheduled;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.servlet.ServletCustomization;
import uk.ac.core.database.configuration.DatabaseConfig;
import uk.ac.core.singleitemworker.configuration.AbstractSingleItemWorkerConfiguration;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.configuration.QueueList;
import uk.ac.core.workers.item.doiresolutionworker.DOIResolutionWorker;
import uk.ac.core.workers.item.doiresolutionworker.crossref.Resolver;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lucasanastasiou
 */
@Configuration
@ComponentScan("uk.ac.core")
@Import({DatabaseConfig.class})
public class ApplicationConfiguration extends AbstractSingleItemWorkerConfiguration {

    @Bean
    public Resolver createResolver() {
        return new Resolver();
    }

    @Override
    public QueueList getQueueNames() {
        QueueList queueList = new QueueList();
        queueList.add("item-doi-resolution-queue");
        return queueList;
    }

    @Override
    public QueueWorker getQueueWorker() {
        return new QueueWorker() {
            @Override
            public List<TaskItem> collectData() {
                return new ArrayList<>();
            }

            @Override
            public void collectStatistics(List<TaskItemStatus> results) {
            }

            /**
             * This method won't be called because we override taskRecieved
             * @param taskItems
             * @return 
             */
            @Override
            public List<TaskItemStatus> process(List<TaskItem> taskItems) {
                return null;
            }

            /**
             * Speed up processing by overriding taskRecieved
            */
            @Override
            public void taskReceived(Object task, Channel channel, Long deliveryTag) {
            }
        };
    }

    // Every 1 minuite, execute any DOIs in the list which are pending DOI resolution
    // This is incase the number of items in the queue are less than Resolver.BATCH_SIZE.
    // We want to ensure they are resolved before the process dies   
    @Scheduled(fixedDelay = 6000)
    public void scheduleDOI(QueueWorker DOIResolutionWorker) {
        ((DOIResolutionWorker) DOIResolutionWorker).scheduledProcessing();
    }

    @Bean
    @Override
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, final MessageListener listenerAdapter, QueueList queueNames, final ServletCustomization servletCustomization) {
        SimpleMessageListenerContainer container = super.container(connectionFactory, listenerAdapter, queueNames, servletCustomization);
        container.setPrefetchCount(1000);
        System.out.println("container = " + container);
        return container;
    }

}
