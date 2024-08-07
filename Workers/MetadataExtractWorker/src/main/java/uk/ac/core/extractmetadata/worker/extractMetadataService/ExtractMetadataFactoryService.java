package uk.ac.core.extractmetadata.worker.extractMetadataService;

import java.util.Date;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.extractmetadata.worker.oaipmh.GetRecords.ArticleMetadataPersist;
import uk.ac.core.extractmetadata.worker.oaipmh.GetRecords.ArticleMetadataPersistFactory;
import uk.ac.core.extractmetadata.worker.oaipmh.GetRecords.CrossrefSaxHandler;
import uk.ac.core.worker.WorkerStatus;

/**
 * @author Giorgio Basile
 * @since 04/04/2017
 */
@Service
public class ExtractMetadataFactoryService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ExtractMetadataFactoryService.class);

    @Autowired
    OaiPmhExtractMetadataService oaiPmhExtractMetadataService;

    @Autowired
    ArticleMetadataPersistFactory articleMetadataPersistFactory;

    @Autowired
    ArticleMetadataExtractMetadataService articleMetadataExtractMetadataService;

    @Autowired
    WorkerStatus workerStatus;

    public ExtractMetadataService createExtractor(Integer repositoryId, Date fromDate, Date untilDate, ArticleMetadataPersist persist) {

        ExtractMetadataService service;

        if (repositoryId == 4786) { //Crossref
            service = articleMetadataExtractMetadataService.init(new CrossrefSaxHandler(persist), repositoryId, fromDate, untilDate);
        } else {
            service = oaiPmhExtractMetadataService.init(persist, repositoryId, fromDate, untilDate);
        }

        logger.info("Extracting repository: " + repositoryId + ", type: " + service.getClass() + ", from:" + fromDate);
        return service;
    }
}
