package uk.ac.core.extractmetadata.worker.oaipmh.XMLParser;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.common.model.article.License;
import uk.ac.core.common.model.legacy.ArticleMetadata;
import uk.ac.core.common.model.legacy.DocumentTdmStatus;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.database.service.document.DocumentTdmStatusDAO;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.extractmetadata.worker.extractMetadataService.ArticleMetadataExtractMetadataService;
import uk.ac.core.extractmetadata.worker.issue.MetadataExtractIssueService;
import uk.ac.core.extractmetadata.worker.oaipmh.GetRecords.ArticleMetadataPersist;
import uk.ac.core.extractmetadata.worker.oaipmh.GetRecords.ArticleMetadataPersistFactory;
import uk.ac.core.extractmetadata.worker.oaipmh.GetRecords.Statistics;
import uk.ac.core.extractmetadata.worker.oaipmh.SAXReaderCorpus;
import uk.ac.core.extractmetadata.worker.taskitem.ExtractMetadataTaskItemStatus;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.supervisor.client.SupervisorClient;
import uk.ac.core.worker.WorkerStatus;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Parses xml metadata file (incremental or full) and stores the metadata to the
 * db
 *
 * @author lucas
 */
@Service
public class XMLMetadataParser {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(XMLMetadataParser.class);
    private static int MINIMUM_ITEMS_BEFORE_DELETE_TASK_RUN_THRESHOLD = 10;

    @Autowired
    private MetadataExtractIssueService metadataExtractIssueService;
    @Autowired
    private ArticleMetadataPersistFactory articleMetadataPersistFactory;
    @Autowired
    private WorkerStatus workerStatus;
    @Autowired
    private DocumentTdmStatusDAO documentTdmStatusDAO;
    @Autowired
    private RepositoryDocumentDAO repositoryDocumentDAO;
    @Autowired
    private SupervisorClient supervisorClient;
    @Autowired
    private ArticleMetadataExtractMetadataService articleMetadataExtractMetadataService;

    private ArticleMetadataPersist persist;
    private Long updateId;
    private long repositoryId;
    private Date fromDate;
    private Date untilDate;

    public void init(ArticleMetadataPersist persist) {
        this.persist = persist;
    }

    public void initStats() {
        /* reset statistics */
        for (Statistics stat : Statistics.values()) {
            stat.resetValue();
        }
    }

    public void updateMetadataToDatabase(SAXReaderCorpus corpus, Integer repositoryId, Date fromDate, Date untilDate) {
        this.repositoryId = repositoryId;
        this.fromDate = fromDate;
        this.untilDate = untilDate;

        metadataExtractIssueService.cleanMetadataExtractIssuesForRepo(repositoryId);

        ExtractMetadataTaskItemStatus extractMetadataTaskStatus = (ExtractMetadataTaskItemStatus) workerStatus.getTaskStatus();

        persist = articleMetadataPersistFactory.create(extractMetadataTaskStatus, repositoryId);

        corpus.start();
        while (corpus.hasNext()) {

            persist.persist(corpus.get());

            extractMetadataTaskStatus.incProcessed();
            extractMetadataTaskStatus.setMaxNumBytes(corpus.getXMLreader().getMaxNumBytes());
            extractMetadataTaskStatus.setPercentage(corpus.getXMLreader().getPercentage());
            extractMetadataTaskStatus.setTotalNumBytesRead(corpus.getXMLreader().getTotalNumBytesRead());
        }
        if(fromDate == null && untilDate == null){
            deleteRemovedArticlesFromDatabase();
        }
        persist.finalise(true);

    }

    /**
     * Sets and updates TdmStatus according to the repository and license of the article
     * <p>
     * Public for testing...
     *
     * @param documentTdmStatus
     * @param license
     * @param repositoryTdmOnly
     */
    public void processTdmOnlyValues(DocumentTdmStatus documentTdmStatus, License license, Boolean repositoryTdmOnly) {
        if (!documentTdmStatus.getFixed()) {
            boolean isTdmOnly = repositoryTdmOnly;
            if (license.isOpenAccess()) {
                isTdmOnly = false;
            }

            if (Objects.equals(documentTdmStatus.getTdmOnly(), isTdmOnly)) {
                logger.info("TDM Status already set for " + documentTdmStatus.getIdDocument() + " - doing nothing");
            } else {
                logger.info("Updating tdm status to" + documentTdmStatus.getTdmOnly());
                documentTdmStatus.setTdmOnly(isTdmOnly);
                documentTdmStatusDAO.insertOrUpdateTdmStatus(documentTdmStatus);
                logger.info("Tdm status successfully updated");
            }
        }
    }

    /***
     * For testing...
     * @param documentTdmStatusDAO
     */
    public void setDocumentTdmStatusDAO(DocumentTdmStatusDAO documentTdmStatusDAO) {
        this.documentTdmStatusDAO = documentTdmStatusDAO;
    }

    /**
     * Deletes Articles which were not seen during Metadata Extract
     * <p>
     * If the number of articles seen in the metadata is < MINIMUM_ITEMS_BEFORE_DELETE_TASK_RUN_THRESHOLD, we skip this
     * process and return false.
     * This prevents the scenario where Metadata Download failed in a way which means 0 items were downloaded but
     * Metadata Download was able to continue. What happens is during Metadata Extract, we see 0 items therefore delete
     * all items in CORE for the repository.
     *
     * @return false if this process was skipped, otherwise, false
     */
    public boolean deleteRemovedArticlesFromDatabase() {

        if (this.persist.getMetadataAllCount().get() < MINIMUM_ITEMS_BEFORE_DELETE_TASK_RUN_THRESHOLD) {
            logger.warn("Skipped deleting records due to repository count being less than " + MINIMUM_ITEMS_BEFORE_DELETE_TASK_RUN_THRESHOLD);
            return false;
        }

        final AtomicInteger i = new AtomicInteger(0);
        final int millis = 1500; // number of milliseconds to wait before deleting next document
        // Get list of repositories
        logger.debug("Start deleting articles not in metadata file");
        repositoryDocumentDAO.streamRepositoryDocumentsByRepositoryId(Math.toIntExact(this.repositoryId),  null, databaseRecord -> {

            // If database record oai is null OR record oai is not in metadata (this.oai)
            // NOTE: null records might be in oai list so cannot rely on this. Explicitly look for this case
            if ((databaseRecord.getOai() == null
                    || !XMLMetadataParser.this.persist.getOaiSet().contains(databaseRecord.getOai()))) {

                // If the oai does exist in the metadata, check its deleted status. Its status should be deleted (0)
                if (databaseRecord.getDeletedStatus() == DeletedStatus.ALLOWED.getValue()) {
                    if (databaseRecord.getOai() == null) {
                        logger.debug("OAI is NULL " + databaseRecord.getIdDocument() + " Deleted Status = " + String.valueOf(databaseRecord.getDeletedStatus()), XMLMetadataParser.this.getClass());
                    }
                    if (databaseRecord.getPdfUrl() == null) {
                        logger.debug("Pdf url is NULL " + databaseRecord.getIdDocument() + " Deleted Status = " + String.valueOf(databaseRecord.getDeletedStatus()), XMLMetadataParser.this.getClass());
                    }
                    logger.debug("wow! This needs to be deleted - " + repositoryId + " " + databaseRecord.getOai() + " " + databaseRecord.getDeletedStatus(), XMLMetadataParser.this.getClass());

                    i.getAndAdd(1);

                    // Now mark the document as deleted
                    // Set repository document deleted in database
                    repositoryDocumentDAO.setDocumentDeleted(databaseRecord.getIdDocument(), DeletedStatus.DELETED);

                    try {
                        supervisorClient.sendIndexItemRequest(databaseRecord.getIdDocument(), DeletedStatus.DELETED);
                        supervisorClient.sendWorkIndexItemRequest(databaseRecord.getIdDocument());

                        logger.debug("request sent, wait for {} ms", millis);
                        Thread.sleep(millis);
                    } catch (CHARSException | InterruptedException ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            }
        });
        logger.debug("Repository: " + repositoryId + ". Deleted " + i.get() + " number of documents", this.getClass());

        return true;
    }
}
