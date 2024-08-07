/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.extractmetadata.worker.oaipmh.GetRecords;

import org.slf4j.LoggerFactory;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.common.model.article.License;
import uk.ac.core.common.model.legacy.ArticleMetadata;
import uk.ac.core.common.model.legacy.DocumentTdmStatus;
import uk.ac.core.common.model.task.TaskStatus;
import uk.ac.core.database.model.RepositorySourceStatistics;
import uk.ac.core.database.service.document.ArticleMetadataDAO;
import uk.ac.core.database.service.document.DocumentTdmStatusDAO;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.database.service.document.RepositoryMetadataDAO;
import uk.ac.core.database.service.repositoryDepositData.RepositoryDepositData;
import uk.ac.core.database.service.repositorySourceStatistics.RepositorySourceStatisticsDAO;
import uk.ac.core.extractmetadata.worker.edgecases.CrossrefDuplicates;
import uk.ac.core.extractmetadata.worker.edgecases.DeleteStatusDocument;
import uk.ac.core.extractmetadata.worker.issue.MetadataExtractIssueService;
import uk.ac.core.extractmetadata.worker.oaipmh.XMLParser.Persist;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author samuel
 */
public class ArticleMetadataPersist implements Persist<ArticleMetadata> {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ArticleMetadataPersist.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final long UPDATE_ID = 0;

    private RepositoryMetadataDAO repositoryMetadataDAO;
    private CrossrefDuplicates crossrefDuplicates;
    private DeleteStatusDocument deleteDocument;
    private ArticleMetadataDAO articleMetadataDAO;
    private RepositoryDocumentDAO repositoryDocumentDAO;
    private DocumentTdmStatusDAO documentTdmStatusDAO;
    private RepositoryDepositData repositoryDepositDataDAO;
    private RepositorySourceStatisticsDAO repositorySourceStatisticsDAO;

    private TaskStatus taskStatus;
    private MetadataExtractIssueService metadataExtractIssueService;

    private Integer repositoryId;
    private Boolean repositoryTdmOnly;

    private Set<String> setSpecsForExclude;


    // Statistics
    private TreeMap<String, Integer> history;
    private TreeMap<String, Integer> cumulativeHistory;
    private Set oaiSet;
    private Set urls;
    private AtomicInteger metadataAllCount;
    private AtomicInteger metadataNonDeletedCount;

    public ArticleMetadataPersist(
            RepositoryMetadataDAO repositoryMetadataDAO,
            CrossrefDuplicates crossrefDuplicates,
            DeleteStatusDocument deleteDocument,
            ArticleMetadataDAO articleMetadataDAO,
            RepositoryDocumentDAO repositoryDocumentDAO,
            DocumentTdmStatusDAO documentTdmStatusDAO,
            RepositoryDepositData repositoryDepositDataDAO,
            RepositorySourceStatisticsDAO repositorySourceStatistics,
            TaskStatus taskStatus,
            MetadataExtractIssueService metadataExtractIssueService,
            Integer repositoryId,
            boolean isRepositoryTDMOnly,
            Set<String> excludeSetsForImport
    ) {
        this.repositoryMetadataDAO = repositoryMetadataDAO;
        this.crossrefDuplicates = crossrefDuplicates;
        this.deleteDocument = deleteDocument;
        this.articleMetadataDAO = articleMetadataDAO;
        this.repositoryDocumentDAO = repositoryDocumentDAO;
        this.documentTdmStatusDAO = documentTdmStatusDAO;
        this.repositoryDepositDataDAO = repositoryDepositDataDAO;
        this.repositorySourceStatisticsDAO = repositorySourceStatistics;
        this.repositoryId = repositoryId;
        this.repositoryTdmOnly = isRepositoryTDMOnly;
        this.setSpecsForExclude = excludeSetsForImport;
        this.taskStatus = taskStatus;
        this.metadataExtractIssueService = metadataExtractIssueService;

        logger.info("Repository {} | TDM Only {}", this.repositoryId, repositoryTdmOnly);
        logger.info("setSpecsForExclude: " + String.join(",", excludeSetsForImport));


        /**
         * For statistics, store all oai's in a set
         * we can then compare the documents that we have already parsed so we dont double count them
         */
        this.oaiSet = new HashSet();
        this.metadataAllCount = new AtomicInteger(0);
        this.metadataNonDeletedCount = new AtomicInteger(0);
        this.history = new TreeMap<>();
        this.cumulativeHistory = new TreeMap<>();
    }

    @Override
    public void persist(final ArticleMetadata articleMetadata) {

        Statistics.META_TOTAL.incValue();
        logger.debug("am :" + articleMetadata.getOAIIdentifier() + articleMetadata.toString(), this.getClass());
        logger.debug(articleMetadata.getIdentifiers().toString(), this.getClass());

        logger.info("Article metadata setNames: {}", articleMetadata.getSetNames());

        boolean isExclude = false;
        for (String setName : articleMetadata.getSetNames()) {
            if (setSpecsForExclude.contains(setName)) {
                isExclude = true;
                logger.info("Excluding article {} with setName: {}", articleMetadata, setName);
            }
        }

        if (articleMetadata.getPdfUrl() != null || articleMetadata.getOAIIdentifier() != null) {
            // update progress statistics
            if (articleMetadata.getPdfUrl() != null) {
                Statistics.META_PDF_URL.incValue();
            } else {
                Statistics.META_NO_URL.incValue();
            }
            if (articleMetadata.getDeleted().equals(DeletedStatus.DELETED)) {
                Statistics.META_DELETED.incValue();
            }
            // insert or update document
            Integer docId = this.repositoryMetadataDAO.getIdDocumentByOai(articleMetadata.getOAIIdentifier(), repositoryId);

            // Override with Crossref if relevant
            docId = this.crossrefDuplicates.Run(docId, articleMetadata, repositoryId);

            if (isExclude && docId != null) {
                deleteDocument.setStatus(DeletedStatus.DELETED, docId, repositoryId, true);
                return;
            }

            trackDepositStatistics(articleMetadata, docId);

            if (articleMetadata.getSetNames().size() > 0) {
                String updatedDocClass = String.join(":", articleMetadata.getSetNames());
                if (updatedDocClass.length()>300){
                    updatedDocClass=updatedDocClass.substring(0,300);
                }
                articleMetadata.setDocClass(updatedDocClass);
            }
            //
            // ADD new record
            //
            if (docId == null) {

                // handle multiple urls
                docId = repositoryDocumentDAO.addDocument(UPDATE_ID, repositoryId, articleMetadata.getOAIIdentifier(), articleMetadata.getPdfUrls(), articleMetadata.getDocClass());
                repositoryDocumentDAO.setDocumentMetadataStatus(docId);
                repositoryDocumentDAO.setDocumentDateStamp(docId, articleMetadata.getDateStamp());
                Statistics.DB_ADDED.incValue();
            } else {
                //
                // UPDATE document record
                //

                // normal update - handle multiple urls
                repositoryDocumentDAO.updateDocuments(docId, UPDATE_ID, repositoryId, articleMetadata.getOAIIdentifier(),
                        articleMetadata.getPdfUrls(), articleMetadata.getDocClass());
                repositoryDocumentDAO.setDocumentMetadataStatus(docId);
                repositoryDocumentDAO.setDocumentDateStamp(docId, articleMetadata.getDateStamp());

                //
                // set as deleted if deleted
                //
                if (articleMetadata.getDeleted().equals(DeletedStatus.DELETED)) {
                    deleteDocument.setStatus(DeletedStatus.DELETED, docId, repositoryId, true);
                }// end of setting as deleted

                // if it is marked as deleted in database but not deleted in metadata
                // then UNDELETE
                Integer databaseDeletedStatus = repositoryDocumentDAO.getDocumentDeletedStatus(docId);
                if (databaseDeletedStatus == DeletedStatus.DELETED.getValue()
                        && articleMetadata.getDeleted() == DeletedStatus.ALLOWED) {
                    logger.debug("Undeleting File " + docId, this.getClass());

                    // document is again available
                    Statistics.DB_UNDELETED.incValue();

                    deleteDocument.setStatus(DeletedStatus.ALLOWED, docId, repositoryId, true);

                }// end of undeleting
                Statistics.DB_UPDATED.incValue();
            }//end updating record

            //
            // add document metadata to database
            //
            DocumentTdmStatus documentTdmStatus = documentTdmStatusDAO.getDocumentTdmStatus(docId);
            License license = new License(articleMetadata.getLicense());
            if (documentTdmStatus == null) {
                //insert only if this repository is tdm only and is not Open Access
                if (repositoryTdmOnly && !license.isOpenAccess()) {
                    logger.info("Assigning tdm status to" + Boolean.TRUE);
                    documentTdmStatusDAO.insertOrUpdateTdmStatus(new DocumentTdmStatus(docId, Boolean.TRUE, Boolean.FALSE));
                    logger.info("Tdm status successfully assigned");
                }
            } else {
                //if this tdm status has not been overwritten manually
                processTdmOnlyValues(documentTdmStatus, license, repositoryTdmOnly);
            }

            articleMetadata.setId(docId);
            articleMetadataDAO.addArticleMetadata(articleMetadata);
            articleMetadataDAO.addRawArticleMetadata(articleMetadata);
            metadataExtractIssueService.reportIssues(repositoryId, articleMetadata);
            taskStatus.incSuccessful();

        }
    }

    private void trackDepositStatistics(ArticleMetadata articleMetadata, Integer docId) {
        // For deposit statistics
        if (oaiSet.contains(articleMetadata.getOAIIdentifier())) { // if we already have the oai, dont add to set
            logger.warn(articleMetadata.getOAIIdentifier() + " already exists in the table", this.getClass());
        } else {// add to set
            this.metadataAllCount.incrementAndGet();
            if (articleMetadata.getDeleted() != DeletedStatus.DELETED) {
                this.metadataNonDeletedCount.addAndGet(1);
                oaiSet.add(articleMetadata.getOAIIdentifier());

                Date depositedDate;
                // add record to date array
                if (docId == null) {
                    depositedDate = articleMetadata.getDateStamp();
                } else {
                    depositedDate = articleMetadataDAO.getDepositedDateStamp(docId);
                }
                if (depositedDate != null) {
                    String date = sdf.format(depositedDate);

                    if (history.containsKey(date)) {
                        history.put(date, history.get(date) + 1);
                    } else {
                        history.put(date, 1);
                    }
                }
            }
        }
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

    /**
     * flushes batched mysql queries and generates statistics
     */
    @Override
    public void finalise(boolean generateStatistics) {
        articleMetadataDAO.flushArticles();
        articleMetadataDAO.flushRawMetadata();

        if (generateStatistics) {
            // for statistics, do something with the size.
            logger.info("Total updated documents: " + Statistics.DB_UPDATED.getValue(), this.getClass());
            logger.info("Total added documents: " + Statistics.DB_ADDED.getValue(), this.getClass());

            // For cumulative map
            int count = 0;
            for (Map.Entry<String, Integer> date : history.entrySet()) {
                count += date.getValue();
                cumulativeHistory.put(date.getKey(), count);
            }
            repositoryDepositDataDAO.setRepositoryDepositData(repositoryId, this.history, this.cumulativeHistory);

            Optional<RepositorySourceStatistics> statsOpt = repositorySourceStatisticsDAO.get(repositoryId);
            if (statsOpt.isPresent()) {
                RepositorySourceStatistics stats = statsOpt.get();
                stats.setMetadataCount(this.metadataAllCount.get());
                stats.setMetadataNonDeletedCount(this.metadataNonDeletedCount.get());
                this.repositorySourceStatisticsDAO.save(stats);
            } else {
                RepositorySourceStatistics stats = new RepositorySourceStatistics(repositoryId);
                stats.setMetadataCount(this.metadataAllCount.get());
                stats.setMetadataNonDeletedCount(this.metadataNonDeletedCount.get());
                this.repositorySourceStatisticsDAO.save(stats);
            }
        }
    }

    public TreeMap<String, Integer> getHistory() {
        return history;
    }

    public TreeMap<String, Integer> getCumulativeHistory() {
        return cumulativeHistory;
    }

    public AtomicInteger getMetadataAllCount() {
        return metadataAllCount;
    }

    public AtomicInteger getMetadataNonDeletedCount() {
        return metadataNonDeletedCount;
    }

    public Set getOaiSet() {
        return oaiSet;
    }
}