package uk.ac.core.metadatadownloadworker.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.issueDetection.configuration.IssueCollectionConfiguration;
import uk.ac.core.metadatadownloadworker.worker.MetadataDownloadWorker;
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
@Import({IssueCollectionConfiguration.class})
@PropertySource(
        {
                "file:/data/core-properties/metadata-${spring.profiles.active}.properties",
                "file:/data/core-properties/chars-components-${spring.profiles.active}.properties"
        }
)
public class MetadataDownloadWorkerApplicationConfig extends AbstractQueueWorkerConfiguration {

    @Override
    public QueueList getQueueNames() {
        QueueList queueList = new QueueList();
        TaskType[] tasksToRegister;

        tasksToRegister = new TaskType[]{
            TaskType.METADATA_DOWNLOAD
        };

        for (TaskType taskToRegister : tasksToRegister) {
            String fullQueueName = taskToRegister.getName() + "-queue";
            queueList.add(fullQueueName);
        }
        return queueList;
    }

    @Override
    public QueueWorker getQueueWorker() {
        return new MetadataDownloadWorker();
    }

    @Value("${supervisor.url}")
    public String SUPERVISOR_URL;

    @Bean
    SupervisorClient supervisorClient() {
        return new HttpSupervisorClient(SUPERVISOR_URL);
    }
}
