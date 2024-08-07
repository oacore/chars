package uk.ac.core.oadiscover;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 *
 * @author lucasanastasiou
 */
@SpringBootApplication(scanBasePackages = {
        "uk.ac.core.dataprovider.logic",
        // scan dependant ES module config and its services
        "uk.ac.core.elasticsearch.configuration",
        // scan dependant database config, to be removed
        "uk.ac.core.database.configuration",
})
public class OADiscoverApplication {

    public static void main(String args[]) {
        SpringApplication.run(OADiscoverApplication.class, args);
    }
}
