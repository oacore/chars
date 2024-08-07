package uk.ac.core.documentdownload.configuration;

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
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import uk.ac.core.common.configuration.filesystem.FileSystemRepositoriesConfiguration;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.database.configuration.DatabaseConfig;
import uk.ac.core.documentdownload.worker.DocumentDownloadQueueWorkerWrapper;
import uk.ac.core.issueDetection.configuration.IssueCollectionConfiguration;
import uk.ac.core.supervisor.client.HttpSupervisorClient;
import uk.ac.core.supervisor.client.SupervisorClient;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.configuration.AbstractQueueWorkerConfiguration;
import uk.ac.core.worker.configuration.QueueList;

/**
 *
 * @author mc26486
 */
@Configuration
@ComponentScan("uk.ac.core")
@Import({DatabaseConfig.class, IssueCollectionConfiguration.class})
public class DocumentDownloadWorkerApplicationConfig extends AbstractQueueWorkerConfiguration {

    @Value("${metadataMode:all}")
    String metadataMode;

    @Value("${skipReDownloadThreshold:false}")
    Boolean skipReDownloadThreshold;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(DocumentDownloadWorkerApplicationConfig.class);

    @Override
    public QueueList getQueueNames() {
        QueueList queueList = new QueueList();
        TaskType[] tasksToRegister;
        switch (metadataMode) {
            case "dit":
                tasksToRegister = new TaskType[]{
                    TaskType.DIT_INGESTION
                };  break;
            case "mucc":
                tasksToRegister = new TaskType[]{
                    TaskType.MUCC_DOCUMENT_DOWNLOAD
                };  break;
            default:
                tasksToRegister = new TaskType[]{
                    TaskType.DOCUMENT_DOWNLOAD
                };  break;
        }

        for (TaskType taskToRegister : tasksToRegister) {
            String fullQueueName = taskToRegister.getName() + "-queue";
            queueList.add(fullQueueName);
        }

        return queueList;
    }

    @Bean
    public Boolean skipReDownloadThreshold() {
        return skipReDownloadThreshold;
    }

    @Value("${supervisor.url}")
    public String SUPERVISOR_URL;

    @Bean
    SupervisorClient supervisorClient() {
        return new HttpSupervisorClient(SUPERVISOR_URL);
    }

    @Override
    public QueueWorker getQueueWorker() {
        return new DocumentDownloadQueueWorkerWrapper();
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

}
