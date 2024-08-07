package uk.ac.core.workers.reindex.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.core.singleitemworker.SingleItemWorkerStatus;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.configuration.AbstractQueueWorkerConfiguration;
import uk.ac.core.worker.configuration.QueueList;
import uk.ac.core.workers.reindex.ReindexWorker;

/**
 *
 * @author lucasanastasiou
 */
@Configuration
public class ReindexWorkerConfiguration extends AbstractQueueWorkerConfiguration {

    @Override
    public QueueList getQueueNames() {
        QueueList queueList = new QueueList();
        queueList.add("reindex-item-queue");
        return queueList;
    }

    @Override
    public QueueWorker getQueueWorker() {
        return new ReindexWorker();
    }

    @Bean
    @Override
    public SingleItemWorkerStatus workerStatus() {
        return new SingleItemWorkerStatus();
    }    
}
