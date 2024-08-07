package uk.ac.core.extractmetadata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import uk.ac.core.elasticsearch.repositories.WorksMetadataRepository;

/**
 * @author lucasanastasiou
 */
@SpringBootApplication
public class ExtractMetadataWorkerApplication {

    public static void main(String args[]) {
        SpringApplication.run(ExtractMetadataWorkerApplication.class, args);
    }
}
