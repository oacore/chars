package uk.ac.core.ExtendedMetadataProcessWorker.configuration;

import java.util.List;
import org.elasticsearch.client.Client;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uk.ac.core.ExtendedMetadataProcessWorker.DownloadStrategies.ExtendedMetadataDownloadStrategy;
import uk.ac.core.ExtendedMetadataProcessWorker.worker.ExtendedMetadataProcessWorker;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.configuration.AbstractQueueWorkerConfiguration;
import uk.ac.core.worker.configuration.QueueList;

/**
 *
 * @author mc26486
 */
@Configuration
public class MetadataPageProcessWorkerApplicationConfig extends AbstractQueueWorkerConfiguration {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(MetadataPageProcessWorkerApplicationConfig.class);

    private final List<ExtendedMetadataDownloadStrategy> strategies;

    public MetadataPageProcessWorkerApplicationConfig(List<ExtendedMetadataDownloadStrategy> strategies) {
        this.strategies = strategies;
    }

    @Bean
    public List<ExtendedMetadataDownloadStrategy> registeredStrategies() {
        return this.strategies;
    }

    @Override
    public QueueList getQueueNames() {
        return new QueueList("extended-metadata-process-queue");
    }

    @Override
    public QueueWorker getQueueWorker() {
        return new ExtendedMetadataProcessWorker();
    }
}
