package uk.ac.core.workers.item.languagedetection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author lucasanastasiou
 */
@SpringBootApplication(scanBasePackages = {
        // scan dependant ES module config and its services
        "uk.ac.core.elasticsearch.configuration",
        // scan dependant database config, to be removed
        "uk.ac.core.database.configuration"
})
public class LanguageDetectionApplication {

    public static void main(String args[]) {
        SpringApplication.run(LanguageDetectionApplication.class, args);
    }
}
