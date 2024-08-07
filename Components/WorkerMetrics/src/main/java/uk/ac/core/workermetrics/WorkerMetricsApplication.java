package uk.ac.core.workermetrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@ComponentScan("uk.ac.core.workermetrics")
@EnableJpaRepositories("uk.ac.core.workermetrics.data.repo")
public class WorkerMetricsApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkerMetricsApplication.class, args);
    }
}