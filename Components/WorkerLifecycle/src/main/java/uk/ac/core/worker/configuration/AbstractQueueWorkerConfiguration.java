package uk.ac.core.worker.configuration;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import uk.ac.core.common.servlet.ServletCustomization;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.WorkerStatus;

import javax.annotation.PostConstruct;

/**
 * This provides a base for workers to describe the task and subscribe to a
 * queue
 *
 * @author samuel
 */
@ComponentScan("uk.ac.core") // search the com.company package for @Component classes
@PropertySource(
        {
            "file:/data/core-properties/queue-${spring.profiles.active}.properties",
            "file:/data/core-properties/chars-components-${spring.profiles.active}.properties"
        }
)
public abstract class AbstractQueueWorkerConfiguration {

    @Value("${noqueue:false}")
    protected Boolean noQueue;
    
    @Bean
    public WorkerStatus workerStatus() {
        return new WorkerStatus();
    }

    /**
     * Returns an array of queue names that the worker should subscribe too
     *
     * @return The Names of the queue
     */
    @Bean
    public abstract QueueList getQueueNames();

    /**
     * Returns the QueueWorker to run when a message is received from the queue
     *
     * @return The Implementation of QueueWorker
     */
    @Bean
    public abstract QueueWorker getQueueWorker();

    /**
     * Creates a MessageListener based on the QueueWorker
     *
     * @param queueWorker The QueueWorker
     * @return a MessageListerAdaptor
     * @see getQueueWorker()
     */
    @Bean
    public MessageListener listenerAdapter(QueueWorker queueWorker) {
        System.out.println("queueWorker = " + queueWorker);
        return new CHARSMessageListener(queueWorker);
    }

    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, 
            final MessageListener listenerAdapter, 
            QueueList queueNames, 
            final ServletCustomization servletCustomization) {

        if (this.noQueue) {
            System.out.println("noQueue is true - registering to known empty queue");
            queueNames.clear();
            queueNames.add("zzempty-queue");
        } 
        
        System.out.println("queueName = " + queueNames.toString());
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueNames.toArray());        
        container.setMessageListener(listenerAdapter);
        
        container.setPrefetchCount(1);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO );
        container.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return servletCustomization.getNodeName() + "#" + queue;
            }
        });
        return container;
    }

    @PostConstruct
    public void setSystemProperties() {
        System.setProperty("com.sun.security.enableAIAcaIssuers", "true");
    }
}
