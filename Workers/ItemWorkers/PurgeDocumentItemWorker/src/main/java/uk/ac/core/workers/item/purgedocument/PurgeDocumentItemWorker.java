package uk.ac.core.workers.item.purgedocument;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author lucasanastasiou
 */
@SpringBootApplication
public class PurgeDocumentItemWorker {

    public static void main(String args[]) {
        SpringApplication.run(PurgeDocumentItemWorker.class, args);
    }
}
