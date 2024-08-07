package uk.ac.core.workers.item.grobid.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.ac.core.singleitemworker.configuration.AbstractSingleItemWorkerConfiguration;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.configuration.QueueList;
import uk.ac.core.workers.item.grobid.CitationParserWorker;

/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
@Configuration
@ComponentScan("uk.ac.core.workers.item.grobid")
public class CitationParserConfiguration extends AbstractSingleItemWorkerConfiguration {

    @Override
    public QueueList getQueueNames() {
        QueueList queueList = new QueueList();
        queueList.add("grobid-citation-parser-item-queue");
        return queueList;
    }

    @Override
    public QueueWorker getQueueWorker() {
        return new CitationParserWorker();
    }

}
