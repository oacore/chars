package uk.ac.core.metadatadownloadworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 *
 * @author mc26486
 */
@SpringBootApplication
public class MetadataDownloadWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetadataDownloadWorkerApplication.class, args);
    }
}
