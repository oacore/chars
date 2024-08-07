package uk.ac.core.workers.item.grobid.configuration;

import org.grobid.core.engines.Engine;
import org.grobid.core.factory.GrobidFactory;
import org.grobid.core.main.GrobidHomeFinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.ac.core.singleitemworker.configuration.AbstractSingleItemWorkerConfiguration;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.worker.configuration.QueueList;
import uk.ac.core.workers.item.grobid.GrobidExtractorWorker;
import java.io.File;

/**
 *
 * @author lucasanastasiou
 */
@Configuration
@ComponentScan("uk.ac.core")
@PropertySource("file:/data/core-properties/grobid-${spring.profiles.active}.properties")
public class GrobidExtractorConfiguration extends AbstractSingleItemWorkerConfiguration {

    @Override
    public QueueList getQueueNames() {
        QueueList queueList = new QueueList();
        queueList.add("grobid-extraction-item-queue");
        return queueList;
    }

    @Override
    public QueueWorker getQueueWorker() {
        return new GrobidExtractorWorker();
    }

    @Value("${grobid.grobidHome}")
    String grobidHome;

    @Value("${grobid.grobidProperties}")
    String grobidHomeConfProperties;

    @Bean
    public Engine engine() throws Exception {

        GrobidHomeFinder grobidHomeFinder = new GrobidHomeFinder();
        grobidHomeFinder.findGrobidConfigOrFail(new File(grobidHome));

        return GrobidFactory.getInstance().createEngine();
    }
}
