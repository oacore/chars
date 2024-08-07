package uk.ac.core.extractmetadata.dataset.crossref;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.ac.core.common.util.DisconnectionListener;
import uk.ac.core.extractmetadata.dataset.crossref.exception.CrossrefDatasetLockException;
import uk.ac.core.extractmetadata.dataset.crossref.service.CrossrefDatasetParser;

import java.util.Random;

@Component
public class CrossrefDatasetListener {
    private static final Logger log = LoggerFactory.getLogger(CrossrefDatasetListener.class);
    private static final int MAX_AWAIT_MS = 1000;

    private final CrossrefDatasetParser parser;

    @Value("${crossref-dataset:false}")
    private Boolean datasetMode;

    @Autowired
    public CrossrefDatasetListener(CrossrefDatasetParser parser) {
        this.parser = parser;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void trigger() {
        if (this.datasetMode) {
            try {
                this.awaitRandomMs();

                log.info("Running with option `crossref-dataset`");
                log.info("Starting the process ...");

                while (this.parser.hasNextBatch()) {
                    this.parser.processBatch();
                    this.parser.getReader().rereadCheckpoints(); // update checkpoints before next iteration
                }

                log.info("No batches to process left");
            } catch (CrossrefDatasetLockException le) {
                if (le.getMessage().contains("already exists")) {
                    DisconnectionListener.halt(le);
                }
                log.error("Operation with locks failed", le);
                Runtime.getRuntime().halt(2);
            }
        }
    }

    private void awaitRandomMs() {
        try {
            Random rnd = new Random(System.currentTimeMillis());
            int awaitMs = rnd.nextInt(MAX_AWAIT_MS);
            log.info("Await {} ms", awaitMs);
            Thread.sleep(awaitMs);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
