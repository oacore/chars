package uk.ac.core.itemclouduploader.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import uk.ac.core.database.configuration.DatabaseConfig;
import uk.ac.core.itemclouduploader.CloudUploaderWorker;
import uk.ac.core.singleitemworker.configuration.AbstractSingleItemWorkerConfiguration;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.configuration.QueueList;

/**
 *
 * @author lucasanastasiou
 */
@Configuration
@ComponentScan("uk.ac.core")
@Import({DatabaseConfig.class})
@PropertySource("file:/data/core-properties/aws-s3-${spring.profiles.active}.properties")
public class ItemCloudUploaderApplicationConfiguration extends AbstractSingleItemWorkerConfiguration {

    @Override
    public QueueList getQueueNames() {
        QueueList queueList = new QueueList();
        queueList.add("upload-to-cloud-queue");
        return queueList;
    }

    @Override
    public QueueWorker getQueueWorker() {
        return new CloudUploaderWorker();
    }

}
