package uk.ac.core.documentdownload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uk.ac.core.database.repository.FileExtensionRepository;

/**
 *
 * @author mc26486
 */
@SpringBootApplication
public class DocumentDownloadWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentDownloadWorkerApplication.class, args);
    }

}
