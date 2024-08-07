package uk.ac.core.workers.item.index;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 *
 * @author lucasanastasiou
 */
@SpringBootApplication
public class ItemIndexApplication {

    public static void main(String args[]) {
        SpringApplication.run(ItemIndexApplication.class, args);
    }
}
