package uk.ac.core.dataprovider.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@SpringBootApplication(scanBasePackages = {
        "uk.ac.core.dataprovider.api.config",
        "uk.ac.core.dataprovider.logic",
        // scan dependant ES module config and its services
        "uk.ac.core.elasticsearch.configuration",
        // scan dependant database config, to be removed
        "uk.ac.core.database.configuration"
})
public class DataProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataProviderApplication.class, args);
    }
}