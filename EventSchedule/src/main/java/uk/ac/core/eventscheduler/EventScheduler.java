package uk.ac.core.eventscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author lucasanastasiou
 */
@SpringBootApplication(exclude = ElasticsearchAutoConfiguration.class)
@EnableScheduling
public class EventScheduler {

    public static void main(String[] args) {
        SpringApplication.run(EventScheduler.class, args);
    }
}
