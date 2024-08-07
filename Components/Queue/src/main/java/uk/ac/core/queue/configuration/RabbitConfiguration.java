package uk.ac.core.queue.configuration;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.ConditionalExceptionLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.ac.core.common.constants.CHARSConstants;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.util.DisconnectionListener;

/**
 *
 * @author lucasanastasiou
 */
@Configuration
@ComponentScan(basePackages = {"uk.ac.core"})
@PropertySource("file:/data/core-properties/queue-${spring.profiles.active}.properties")
public class RabbitConfiguration {

    protected static final Logger logger = LoggerFactory.getLogger(RabbitConfiguration.class);

    @Value("${QUEUE_HOST}")
    public String QUEUE_HOST;//= "localhost";

    @Value("${QUEUE_PORT}")
    private int QUEUE_PORT;// = 5672;

    @Value("${TASK_EXCHANGE}")
    private String TASK_EXCHANGE;

    @Value("${EVENTS_EXCHANGE}")
    private String EVENTS_EXCHANGE;

    @Value("${EVENT_QUEUES_NAMES}")
    private String[] EVENT_QUEUES_NAMES;

    @Bean
    ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(QUEUE_HOST, QUEUE_PORT);
        connectionFactory.getRabbitConnectionFactory().setAutomaticRecoveryEnabled(true);
        connectionFactory.setCloseExceptionLogger(new ConditionalExceptionLogger() {
            @Override
            public void log(Log log, String s, Throwable throwable) {
                log.error("System is exiting due to RabbitMQ Exception");
                log.error(throwable.getMessage());
                log.error("exiting...");
//                System.exit(1);
                DisconnectionListener.halt(throwable);
            }
        });
        return connectionFactory;
    }

    /**
     * Declares queues and exchanges
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        
        TopicExchange taskExchange = new TopicExchange(TASK_EXCHANGE, true, false);
        TopicExchange eventsExchange = new TopicExchange(EVENTS_EXCHANGE, true, false);
        admin.declareExchange(taskExchange);
        admin.declareExchange(eventsExchange);

        for (TaskType queueName : TaskType.values()) {
            String fullQueueName = queueName.getName() + CHARSConstants.QUEUE_SUFFIX;
            Queue queue = new Queue(fullQueueName, true, false, false);
            admin.declareQueue(queue);
            Binding binding = BindingBuilder.bind(queue).to(taskExchange).with(queueName.getName());
            admin.declareBinding(binding);
        }
        for (String queueName : EVENT_QUEUES_NAMES) {
            String fullQueueName = queueName + CHARSConstants.QUEUE_SUFFIX;
            Queue queue = new Queue(fullQueueName, true, false, false);
            admin.declareQueue(queue);
            Binding binding = BindingBuilder.bind(queue).to(eventsExchange).with(queueName);
            admin.declareBinding(binding);
        }
        return admin;
    }

}
