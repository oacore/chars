package uk.ac.core.eventscheduler.configuration;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import uk.ac.core.common.servlet.ServletCustomization;
import uk.ac.core.eventscheduler.broker.EventSchedulerQueueBroker;
import uk.ac.core.queue.QueueInfoService;
import uk.ac.core.supervisor.client.HttpSupervisorClient;
import uk.ac.core.supervisor.client.SupervisorClient;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 * @author lucasanastasiou
 */
@ComponentScan({"uk.ac.core.queue"})
@PropertySource("file:/data/core-properties/chars-components-${spring.profiles.active}.properties")
@Configuration
public class EventSchedulerConfiguration implements AsyncConfigurer, SchedulingConfigurer {

    @Bean
    EventSchedulerQueueBroker eventSchedulerQueueBroker() {
        return new EventSchedulerQueueBroker();
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter, final ServletCustomization servletCustomization) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("task-events-queue");
        container.setMessageListener(listenerAdapter);

        container.setConsumerTagStrategy((String queue) -> servletCustomization.getNodeName() + "#" + queue);

        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(EventSchedulerQueueBroker eventSchedulerQueueBroker) {
        System.out.println("queueWorker = " + eventSchedulerQueueBroker);
        return new MessageListenerAdapter(eventSchedulerQueueBroker, "eventReceived");
    }

    @Value("${supervisor.url}")
    public String SUPERVISOR_URL;

    @Bean
    SupervisorClient supervisorClient() {
        return new HttpSupervisorClient(SUPERVISOR_URL);
    }

    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(10000);
        executor.setThreadNamePrefix("project-Executor-");
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    @Bean
    public Executor scheduledTaskExecutor() {
        return Executors.newScheduledThreadPool(10);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(scheduledTaskExecutor());
    }
}
