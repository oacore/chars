package uk.ac.core.ExtendedMetadataProcessWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uk.ac.core.dataprovider.logic.repository.elasticsearch.IndexDataProviderRepository;
import uk.ac.core.elasticsearch.repositories.ArticleMetadataRepository;
import uk.ac.core.elasticsearch.repositories.WorksMetadataRepository;

@SpringBootApplication(scanBasePackages = {
        "uk.ac.core.dataprovider.logic",
        // scan dependant database config, to be removed
        "uk.ac.core.database.configuration",
        "uk.ac.core.issueDetection"
})
public class ExtendedMetadataProcessWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExtendedMetadataProcessWorkerApplication.class, args);
    }

}
