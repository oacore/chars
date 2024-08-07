package uk.ac.core.cronscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uk.ac.core.dataprovider.logic.repository.DataProviderLocationRepository;

/**
 *
 * @author mc26486
 */
@SpringBootApplication
public class CronSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CronSchedulerApplication.class, args);
    }
}