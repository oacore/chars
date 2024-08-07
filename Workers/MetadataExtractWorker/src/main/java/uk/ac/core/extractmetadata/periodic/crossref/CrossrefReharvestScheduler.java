package uk.ac.core.extractmetadata.periodic.crossref;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.extractmetadata.periodic.crossref.service.CrossrefReharvestService;

import java.io.File;

@Component
public class CrossrefReharvestScheduler {
    private static final Logger log = LoggerFactory.getLogger(CrossrefReharvestScheduler.class);

    private final CrossrefReharvestService service;
    @Value("${crossref:false}")
    private Boolean crossrefReharvestingMode;

    @Autowired
    public CrossrefReharvestScheduler(CrossrefReharvestService service) {
        this.service = service;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void start() {
        log.info("CLI argument `crossref` is set to {}", this.crossrefReharvestingMode);
        if (this.crossrefReharvestingMode != null && this.crossrefReharvestingMode) {
            log.info("Start scheduled task ...");
            File metadataTmpFile = null;
            try {
                // init ArticleMetadataPersist object
                this.service.initPersist(new TaskItemStatus());
                // get documents from database tables
                // and write it to the file
                metadataTmpFile = this.service.getRawMetadataFile();
                if (metadataTmpFile == null) {
                    log.error("Couldn't create a file, check the logs above");
                    return;
                }
                // parse metadata from the file
                // using CrossrefSaxHandler
                try {
                    this.service.parseMetadata(metadataTmpFile);
                } catch (SAXException e) {
                    throw new RuntimeException(e);
                }
                // update records to the database
                this.service.flushRecordsToDatabase();
                // schedule indexing for processed documents
                int toReindexCount = this.service.processedCount();
                this.service.scheduleReindex();
                // delete temporary file
                boolean fileDeleted = this.service.deleteTmpFile(metadataTmpFile);
                if (fileDeleted) {
                    log.info("Temporary file deleted");
                }
                log.info("This time processed {} documents", toReindexCount);
            } catch (RuntimeException re) {
                log.error("Scheduled task finished with runtime exception", re);
            }
            log.info("End scheduled task");
        } else {
            log.info("Can't start the process now");
        }
    }

    public CrossrefReharvestService getService() {
        return service;
    }
}
