package uk.ac.core.workers.item.thumbnail.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.ac.core.singleitemworker.configuration.AbstractSingleItemWorkerConfiguration;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.configuration.QueueList;
import uk.ac.core.workers.item.thumbnail.ItemThumbnailWorker;

/**
 *
 * @author lucasanastasiou
 */
@Configuration
@ComponentScan("uk.ac.core")
public class ItemThumbnailConfiguration extends AbstractSingleItemWorkerConfiguration {

    @Override
    public QueueList getQueueNames() {
        QueueList queueList = new QueueList();
        queueList.add("thumbnail-generation-item-queue");
        return queueList;
    }

    @Override
    public QueueWorker getQueueWorker() {
        return new ItemThumbnailWorker();
    }

}
