package uk.ac.core.extractmetadata.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import uk.ac.core.common.configuration.filesystem.FileSystemRepositoriesConfiguration;
import uk.ac.core.database.configuration.DatabaseConfig;
import uk.ac.core.extractmetadata.worker.ExtractMetadataWorker;
import uk.ac.core.issueDetection.configuration.IssueCollectionConfiguration;
import uk.ac.core.supervisor.client.HttpSupervisorClient;
import uk.ac.core.supervisor.client.SupervisorClient;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.configuration.AbstractQueueWorkerConfiguration;
import uk.ac.core.worker.configuration.QueueList;

/**
 *
 * @author lucasanastasiou
 */
@Configuration
@ComponentScan("uk.ac.core")
@Import({DatabaseConfig.class, IssueCollectionConfiguration.class})
@EnableScheduling
public class ExtractMetadataWorkerConfiguration extends AbstractQueueWorkerConfiguration {

    @Value("${metadataMode:all}")
    String metadataMode;
    
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(ExtractMetadataWorkerConfiguration.class);

    @Override
    public QueueList getQueueNames() {
        QueueList queueList = new QueueList();
        System.out.println("metadataMode = " + metadataMode);
        if (metadataMode.equals("dit")) {
            queueList.add("dit-ingestion-queue");
        } else {
            queueList.add("extract-metadata-queue");
        }
        return queueList;
    }

    @Override
    public QueueWorker getQueueWorker() {
        return new ExtractMetadataWorker();
    }

    @Bean
    public FileSystemRepositoriesConfiguration getFileSystemRepositoriesConfiguration() {
        String yamlFileLocation = "/data/core-properties/fs-repos-default.yaml";
        Yaml yaml = new Yaml(new Constructor(FileSystemRepositoriesConfiguration.class));
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(yamlFileLocation));
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage());
        }
        FileSystemRepositoriesConfiguration obj = yaml.load(inputStream);
        return obj;
    }
        
    @Value("${supervisor.url}")
    public String SUPERVISOR_URL;

    @Bean
    SupervisorClient supervisorClient() {
        return new HttpSupervisorClient(SUPERVISOR_URL);
    }
}
