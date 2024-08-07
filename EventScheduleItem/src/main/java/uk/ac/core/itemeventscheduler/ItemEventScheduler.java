package uk.ac.core.itemeventscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author lucasanastasiou
 */
@SpringBootApplication
@EnableScheduling
public class ItemEventScheduler {

    public static void main(String[] args) {
        SpringApplication.run(ItemEventScheduler.class, args);
    }
}
