package uk.ac.core.documentdownload.worker;

import com.google.gson.Gson;
import crawlercommons.fetcher.AbortedFetchException;
import crawlercommons.fetcher.AbortedFetchReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.configuration.filesystem.FileSystemRepositoriesConfiguration;
import uk.ac.core.common.model.article.LicenseStrategy;
import uk.ac.core.common.model.article.PDFUrlSource;
import uk.ac.core.common.model.legacy.*;
import uk.ac.core.common.model.task.*;
import uk.ac.core.common.model.task.item.TaskItemBuilder;
import uk.ac.core.common.model.task.parameters.DocumentDownloadParameters;
import uk.ac.core.database.entity.FileExtension;
import uk.ac.core.database.entity.FileExtensionType;
import uk.ac.core.database.repository.FileExtensionRepository;
import uk.ac.core.database.service.bigRepository.BigRepositoryDAO;
import uk.ac.core.database.service.document.ArticleMetadataDAO;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.database.service.repositories.RepositoriesDAO;
import uk.ac.core.database.service.repositories.RepositoriesHarvestPropertiesDAO;
import uk.ac.core.database.service.repositories.RepositoryDomainException;
import uk.ac.core.database.service.repositorySourceStatistics.RepositorySourceStatisticsDAO;
import uk.ac.core.documentdownload.downloader.*;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrl;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrlBucket;
import uk.ac.core.documentdownload.downloader.crawling.CrawlingUrlParser;
import uk.ac.core.documentdownload.downloader.crawling.SignpostingUrl;
import uk.ac.core.documentdownload.downloader.fetcher.IllegalDomainException;
import uk.ac.core.documentdownload.downloader.fetcher.RequiresLoginException;
import uk.ac.core.documentdownload.entities.CrawlingHeuristicService;
import uk.ac.core.documentdownload.entities.DocumentDownloadMetric;
import uk.ac.core.documentdownload.entities.dao.DocumentDownloadDAO;
import uk.ac.core.documentdownload.entities.dao.DocumentDownloadMetricDAO;
import uk.ac.core.documentdownload.entities.dao.DocumentDownloadStatusDAO;
import uk.ac.core.documentdownload.issues.DocumentDownloadIssueReporting;
import uk.ac.core.documentdownload.taskitem.DocumentDownloadTaskItem;
import uk.ac.core.documentdownload.taskitem.DocumentDownloadTaskItemList;
import uk.ac.core.documentdownload.taskitem.DocumentDownloadTaskItemStatus;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.issueDetection.data.repository.resetissue.BackwardCompatibilityIssueDao;
import uk.ac.core.issueDetection.model.IssueBO;
import uk.ac.core.issueDetection.service.IssueService;
import uk.ac.core.issueDetection.util.IssueType;
import uk.ac.core.slack.client.SlackWebhookService;
import uk.ac.core.slack.client.model.SlackMessage;
import uk.ac.core.textextraction.exceptions.TextExtractionErrorCodes;
import uk.ac.core.textextraction.exceptions.TextExtractionException;
import uk.ac.core.worker.BasicQueueWorker;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static uk.ac.core.common.util.datastructure.FluentHashMap.map;

/**
 * @author mc26486
 */
@Service
public class DefaultDocumentDownloadWorker extends BasicQueueWorker {

    private static final String NO_DETAILS_MSG = "No details for this type of issue.";
    private final Logger logger = LoggerFactory.getLogger(DefaultDocumentDownloadWorker.class);
    private static final String ARXIV_REPO_ID = "144";

    private DocumentDownloadParameters pdfDownloadParameters;
    private RepositoryHarvestProperties repositoryHarvestProperties;
    private LicenseStrategy licenseStrategy;
    private String repositoryDomain;

    @Autowired
    private FilesystemDAO filesystemDAO;
    @Autowired
    private RepositoriesDAO repositoriesDAO;

    @Autowired
    private CrawlingHeuristicService crawlingHeuristicService;

    @Autowired
    RepositoriesHarvestPropertiesDAO repositoriesHarvestPropertiesDAO;

    @Autowired
    DocumentDownloadDAO documentDownloadDAO;

    @Autowired
    private TaskItemBuilder taskItemBuilder;

    private UrlEvaluator urlFilteringService;

    @Autowired
    private ArticleMetadataDAO articleMetadataDAO;

    @Autowired
    RepositoryDocumentDAO repositoryDocumentDAO;

    @Autowired
    private PageParserService pageParserService;

    @Autowired
    private DocumentDownloadStatusDAO documentDownloadStatusService;

    @Autowired
    private RepositorySourceStatisticsDAO repositorySourceStatistics;

    private List<RepositoryDomainException> domainExceptions;
    private SlownessService slownessService;
    @Autowired
    private FileSystemRepositoriesConfiguration fileSystemRepositoriesConfiguration;
    private boolean isAFilesystemRepo;
    private DocumentDownloadMetric documentDownloadMetric;
    @Autowired
    private DocumentDownloadMetricDAO documentDownloadMetricDAO;

    @Autowired
    private Boolean skipReDownloadThreshold;

    @Autowired
    private IssueService issueService;

    @Autowired
    private BackwardCompatibilityIssueDao backwardCompatibilityIssueDao;

    DocumentDownloadIssueReporting issueReporting;

    private int numberOfRequestsPerformed;
    private int numberOfDocumentsAttempted;
    private static final long NO_REDOWNLOAD_DAYS_THRESHOLD = 180;
    private static final long INDEX_DAYS_THRESHOLD = 10;

    ObtainsFile fileFetcher;
    private boolean isIncrementalUpdate;

    @Autowired
    private FileExtensionRepository fileExtensionRepository;

    @Autowired
    private BigRepositoryDAO bigRepositoryDAO;

    public DefaultDocumentDownloadWorker() {
    }

    @Override
    public List<TaskItem> collectData() {
        logger.info("Start collecting data");
        this.pdfDownloadParameters = new Gson().fromJson(this.currentWorkingTask.getTaskParameters(), DocumentDownloadParameters.class);
        this.isIncrementalUpdate = (this.pdfDownloadParameters.getFromDate() != null);

        this.slownessService = new SlownessService(this.workerStatus.getTaskStatus());

        if (this.pdfDownloadParameters.getSize() != null && !this.pdfDownloadParameters.isSingleItem()) {
            issueService.deleteIssues(pdfDownloadParameters.getRepositoryId(), TaskType.DOCUMENT_DOWNLOAD);
        }

        issueReporting = new DocumentDownloadIssueReporting(issueService, pdfDownloadParameters.getRepositoryId());
        if (!this.pdfDownloadParameters.isSingleItem()) {
            backwardCompatibilityIssueDao.deleteIssues(pdfDownloadParameters.getRepositoryId(), TaskType.DOCUMENT_DOWNLOAD);
        }

        this.repositoryHarvestProperties = repositoriesHarvestPropertiesDAO.load(pdfDownloadParameters.getRepositoryId());
        this.licenseStrategy = this.repositoryHarvestProperties.getLicenseStrategy();
        this.urlFilteringService = new UrlEvaluator(this.repositoryHarvestProperties);
        this.crawlingHeuristicService.loadHeuristic(this.pdfDownloadParameters.getRepositoryId());
        LegacyRepository repository = repositoriesDAO.getRepositoryById(pdfDownloadParameters.getRepositoryId().toString());
        this.domainExceptions = this.repositoriesHarvestPropertiesDAO.getRepositoryDomainExceptions(this.repositoryHarvestProperties.getRepositoryId());
        this.numberOfRequestsPerformed = 0;
        this.numberOfDocumentsAttempted = 0;
        this.isAFilesystemRepo = this.fileSystemRepositoriesConfiguration.getRepositoryConfigById(this.pdfDownloadParameters.getRepositoryId()) != null;
        this.documentDownloadMetric = new DocumentDownloadMetric();
        this.documentDownloadMetric.setRepositoryId(pdfDownloadParameters.getRepositoryId());
        this.documentDownloadMetric.setStartTime(System.currentTimeMillis());
        String repositoryUrl = repository.getUrlOaipmh();
        try {
            this.repositoryDomain = new URL(repositoryUrl).getHost();
        } catch (MalformedURLException ex) {
            this.repositoryDomain = repositoryUrl;
        }

        // Because the processing date is AFTER the date imported into the database, using the harvest dates to search
        //    for files will result in 0 records as the date added to db was after the ToDate in the metadata
        pdfDownloadParameters.setFromDate(pdfDownloadParameters.getFromDate());
        pdfDownloadParameters.setToDate(pdfDownloadParameters.getToDate());

        logger.info("License strategy is set to {}", this.licenseStrategy.name());

        return new DocumentDownloadTaskItemList(repositoryHarvestProperties.getRepositoryId(), repositoryDocumentDAO,
                pdfDownloadParameters, repositoryHarvestProperties.isPrioritiseOldDocumentsForDownload());
    }

    /**
     * @param documentId
     * @param issueType
     * @param message
     * @param details
     * @return
     * @deprecated use this.issueReporting.createIssue directly
     */
    private IssueBO createIssue(long documentId, IssueType issueType, String message, Map<String, String> details, String oai) {
        return this.issueReporting.createIssue(documentId, issueType, message, details, oai);
    }

    /**
     * @param issueBO
     * @deprecated use this.issueReporting.createIssue directly
     */
    private void reportIssue(IssueBO issueBO) {
        this.issueReporting.reportIssue(issueBO);
    }

    @Override
    public void collectStatistics(List<TaskItemStatus> results) {
        // Not statistics, but it needs to run once all PDF's have finished downloading
        logger.warn("Executing last mysql query batch");
        this.documentDownloadDAO.flushDocumentStatus();
        this.documentDownloadMetric.setEndTime(System.currentTimeMillis());
        this.documentDownloadMetric.setNumberOfDocumentsProcessed(this.numberOfDocumentsAttempted);

        int downloadedDocumentAmount = 0;
        for (TaskItemStatus result : results) {
            if (result.isSuccess()) {
                downloadedDocumentAmount++;
            }
        }

        this.documentDownloadMetric.setNumberOfPdfDownloaded(downloadedDocumentAmount);
        this.documentDownloadMetric.setNumberOfRequestsPerformed(this.numberOfRequestsPerformed);
        logger.info("Saving: " + documentDownloadMetric.toString());
        this.documentDownloadMetricDAO.save(documentDownloadMetric);
        logger.info("Saved");

        this.repositorySourceStatistics.setFulltextCount(
                documentDownloadMetric.getRepositoryId(),
                this.repositoryDocumentDAO.countRepositoryDocumentsWithFulltext(documentDownloadMetric.getRepositoryId())
        );
    }

    @Override
    public TaskItemStatus processSingleItem(TaskItem item) {

        DocumentDownloadTaskItem documentDownloadTaskItem = (DocumentDownloadTaskItem) item;
        int id_document = documentDownloadTaskItem.getRepositoryDocumentBase().getIdDocument();

        DocumentDownloadTaskItemStatus status = new DocumentDownloadTaskItemStatus();
        status.setSuccess(false);

        RepositoryDocumentBase repositoryDocumentBase = documentDownloadTaskItem.getRepositoryDocumentBase();
        logger.info("Downloading single document:" + repositoryDocumentBase.getIdDocument() + " with " + repositoryDocumentBase.getUrls().size() + " urls to explore");

        long startProcessTime = System.nanoTime();
        boolean canAttemptDownloadProceed = true;
        if (repositoryDocumentBase.getOai() == null) {
            repositoryDocumentBase.setOai("mucc.core.ac.uk/" + repositoryDocumentBase.getIdDocument());
        }
        String oai = repositoryDocumentBase.getOai();
        this.fileFetcher = new DomainAwareHttpFileDownload(
                this.repositoryHarvestProperties.isSameDomainPolicy(),
                domainExceptions,
                repositoryDomain,
                this.repositoryHarvestProperties.getAcceptedContentTypes(),
                this.issueReporting
        );

        if (repositoryHarvestProperties.getRepositoryId().equals(ARXIV_REPO_ID)) {
            this.fileFetcher = new ArXivFileGetter(fileFetcher, !isIncrementalUpdate);
        }

        if (repositoryHarvestProperties.isSkipAlreadyDownloaded()) {
            // If we have the pdf, we do not want to download it again
            canAttemptDownloadProceed = !documentDownloadTaskItem.getRepositoryDocumentBase().isPdfStatus();
        }

        boolean continueToDownload = this.canTryToDownloadDocument(documentDownloadTaskItem.getRepositoryDocumentBase());

        if (skipReDownloadThreshold || (canAttemptDownloadProceed && continueToDownload)) {
            this.numberOfDocumentsAttempted++;
            String documentTitle = this.articleMetadataDAO.getArticleTitle(repositoryDocumentBase.getIdDocument());
            if (documentTitle == null) {
                logger.error("Document url doesn't have a document attached to it");
                status.setMessage("Document not found");
                status.setSuccess(false);
                return status;
            }
            CrawlingUrlBucket crawlingUrlBucket = fillCrawlingUrlBucket(repositoryDocumentBase);

            // Checking if crawlingUrlBucket is empty at the end of the process is pointless. 
            // It is always empty because we remove items as we check them
            Boolean initialBucketIsEmpty = crawlingUrlBucket.isEmpty();

            CrawlingUrl crawlingUrl;//= crawlingUrlBucket.pop();

            Boolean isLocalFile = Boolean.FALSE;
            File tmpFile = null;
            try {
                try {
                    tmpFile = File.createTempFile(repositoryDocumentBase.getIdDocument().toString(), "DefaultDocumentDownloadWorker");
                } catch (IOException e) {
                    if (e.getMessage().contains("No space left on device")) {
                        logger.error("Quitting: No space left on device: " + e.getMessage(), e);
                        System.exit(1);
                    }
                }

                while ((crawlingUrl = crawlingUrlBucket.pop()) != null && !isLocalFile && !status.isSuccess()) {

                    logger.info("Popped from the stack :" + crawlingUrl.toString());

                    if (crawlingUrl.getCurrentUrl().contains("pdf")) {
                        logger.debug("Current url is probably PDF: " + crawlingUrl.getCurrentUrl(), this.getClass());
                    }

                    try {
                        Boolean canContinueWithDownload = this.fileFetcher.prepare(id_document, oai, crawlingUrl, crawlingUrlBucket);
                        if (!canContinueWithDownload) {
                            logger.info("canContinueWithDownload is " + canContinueWithDownload.toString());
                            continue;
                        }
                    } catch (DocumentDownloadIssueException ex) {
                        status.setIssueType(ex.getIssueType());
                        status.setMessage(ex.getMessage());
                        status.setSuccess(false);
                        continue;
                    } catch (InterruptedException ex) {
                        status.setMessage(ex.getMessage());
                        status.setSuccess(false);
                        return status;
                    }

                    long startDownloadTime = System.nanoTime();
                    // Download the file
                    // HttpFileDownload class is using crawler-commons library and limits download to
                    // specific mime types only (file size is not restricted, maybe TODO?)
                    // TODO stop the download of non pdf when harvest level is reached

                    if (crawlingUrl.getCurrentUrl().startsWith("file://")) {
                        isLocalFile = processFilesystemDocument(crawlingUrl, repositoryDocumentBase, status, crawlingUrlBucket, isLocalFile);
                    } else {
                        logger.info("Downloading url:" + crawlingUrl.getCurrentUrl());

                        DownloadResult downloadResult = null;

                        try {

                            downloadResult = fileFetcher.obtainFile(crawlingUrl.getCurrentUrl(), tmpFile.toString());
                        } catch (AbortedFetchException ex) {
                            logger.error("AbortedFetchException: " + ex.getMessage(), ex);
                            if (ex.getAbortReason() == AbortedFetchReason.INVALID_MIMETYPE) {
                                status.setIssueType(IssueType.UNSUPPORTED_FILETYPE);
                            }
                            if (ex.getAbortReason() == AbortedFetchReason.CONTENT_SIZE) {
                                status.setIssueType(IssueType.ATTACHMENT_TOO_BIG);
                            }
                        } catch (RequiresLoginException e) {
                            // This is equivalent to the server returning a 401 directly
                            if (downloadResult == null) {
                                downloadResult = new DownloadResult();
                            }
                            downloadResult.setStatusCode(401);
                        } catch (IllegalDomainException e) {
                            reportIssue(
                                    createIssue(
                                            id_document,
                                            IssueType.LINK_REDIRECTED_TO_DISALLOWED_URL,
                                            IssueType.LINK_REDIRECTED_TO_DISALLOWED_URL.getDescription(),
                                            map("url", e.getUri().toString()), oai)
                            );
                        } finally {
                            // Stub DownloadResult to prevent null pointer
                            // This is to mock the output before we threw AbortedFetchException
                            if (downloadResult == null) {
                                downloadResult = new DownloadResult();
                                // Place dummy int so we don't cause NullPointer later on
                                // Also useful as 991 can indicate an AbortedFetchException
                                downloadResult.setStatusCode(991);
                            }
                        }
                        this.numberOfRequestsPerformed++;
                        this.logEndTime("download time", startDownloadTime);

                        // Detect is URL is 'rectricted'
                        // https://ore.exeter.ac.uk/repository/bitstream/handle/10871/14704/XieJ.pdf?sequence=1&isAllowed=n
                        if (isRestricted(crawlingUrl.getCurrentUrl())) {
                            logger.debug(String.format("PDF is detected as restricted. url: %s", crawlingUrl), this.getClass());
                            status.setIssueType(IssueType.RESTRICTED_ATTACHMENT);
                        }

                        if (downloadResult.getStatusCode() == 401 || downloadResult.getStatusCode() == 451) {
                            logger.debug(String.format("PDF is detected as restricted (%s)l: %s", downloadResult.getStatusCode(), crawlingUrl), this.getClass());
                            status.setIssueType(IssueType.RESTRICTED_ATTACHMENT);
                        }

                        crawlingUrlBucket.markVisited(crawlingUrl.getCurrentUrl());

                        if (downloadResult.getStatusCode() != 200) {
                            // TODO store some statistics here?
                            logger.debug("Erroneous download result, status code " + downloadResult.getStatusCode() + ", url: " + crawlingUrl, this.getClass());
                            if (downloadResult.getStatusCode() == 404) {
                                status.setSuccess(false);
                                reportIssue(
                                        createIssue(id_document, IssueType.NON_EXISTENT_PAGE_ATTACHMENT, IssueType.NON_EXISTENT_PAGE_ATTACHMENT.getDescription(), map("url", crawlingUrl.getCurrentUrl()), oai)
                                );
                            } else if (downloadResult.getStatusCode() == 990) {
                                status.setIssueType(IssueType.ATTACHMENT_TOO_BIG);
                            } else if (status.getIssueType() == null || status.getIssueType() == IssueType.NO_FULL_TEXT_LINKS) {
                                status.setIssueType(IssueType.UNSPECIFIED_DOWNLOAD_ERROR);
                            }
                            continue;
                        }
                        long startDownloadProcess = System.nanoTime();

                        if (DocumentFileChecker.isFileValid(downloadResult.getContentFirstBytes(), downloadResult.getContentType())) {
                            validateAndSaveDocument(id_document, oai, downloadResult, tmpFile, documentTitle, status, crawlingUrl, HarvestLevel.fromInt(crawlingUrl.getCurrentHarvestLevel()));
                        } // try to parse downloaded content
                        else if (DocumentFileChecker.isParsableFile(downloadResult.getContentType())
                                && !this.repositoryHarvestProperties.getHarvestLevel().isAboveHarvestLevel(crawlingUrl.getCurrentHarvestLevel())) {
                            int numberOfAddedUrls = addUrlsToBucket(id_document, oai, downloadResult, crawlingUrl, crawlingUrlBucket, status, tmpFile.toString());
                            if (numberOfAddedUrls == 0) {
                                initialBucketIsEmpty = true;
                            }
                        }

                        if (status.isSuccess()) {
                            logger.info("Successful download and processing: " + id_document + " " + crawlingUrl.getCurrentUrl(), this.getClass());
                            this.documentDownloadDAO.setDownloadSuccessful(repositoryDocumentBase.getIdDocument(), crawlingUrl.getOriginalUrl(), crawlingUrl.getCurrentUrl());
                        } else {
                            logger.info(String.format("Unsuccessful download: %d %d", id_document, repositoryDocumentBase.getIdDocument()), this.getClass());
                            status.setMessage("Harvest level reached no PDF found");
                            // If there isn't already an issue assigned, add this one
                            if (status.getIssueType() == null) {
                                status.setIssueType(IssueType.EXTERNAL_UNKNOWN);
                            }
                        }
                        this.logEndTime("Process download - " + status.isSuccess(), startDownloadProcess);
                        this.logEndTime("Process Document", startProcessTime);
                    }

                }
            } finally {
                if (tmpFile != null) {
                    tmpFile.delete();
                }
            }

            // Note: initialBucketIsEmpty is set to true if the number of extracted urls from html page is also 0
            if (initialBucketIsEmpty) {
                status.setIssueType(IssueType.NO_VALID_ATTACHMENT_DOWNLOAD_URLS);
            }
            documentDownloadStatusService.setDownloadStatus(id_document, (status.getIssueType() == null) ? status.isSuccess().toString() : status.getIssueType().toString());

            //
            if (slownessService.isTheTaskSlow()) {
                //if we recognise some heavy slowness we just drop the task
                reportIssue(createIssue(id_document, IssueType.SLOW_NETWORK, IssueType.SLOW_NETWORK.getDescription(), map("message", NO_DETAILS_MSG), oai));
                this.drop();
            }
        } else {
            /**
             * The document has not been processed because failed in the last X
             * months, we index it anyway.
             */
            status.setSuccess(true);
            status.setSkipped(true);
        }
        if (!status.isSuccess() && !status.isSkipped()) {
            String message;
            if (status.getIssueType() != null) {
                message = status.getIssueType().getDescription();
            } else {
                message = status.getMessage();
            }
            if (status.getIssueType() == null) {
                status.setIssueType(IssueType.UNSPECIFIED_DOWNLOAD_ERROR);
            }
            this.documentDownloadDAO.setDownloadUnsuccessful(repositoryDocumentBase.getIdDocument());
            reportIssue(createIssue(id_document, status.getIssueType(), message, map("message", NO_DETAILS_MSG), oai));
        }

        if (status.isSuccess() && !status.isSkipped()) {
            logger.info(String.format("Successful download, not skipped: %d %d", id_document, repositoryDocumentBase.getIdDocument()), this.getClass());
        }

        Optional<LocalDateTime> lastAttemptOpt = repositoryDocumentDAO.getIndexLastAttempt(id_document);
        if (isIncrementalUpdate ||
                !lastAttemptOpt.isPresent()
                || lastAttemptOpt.get().isBefore(LocalDateTime.now().minusDays(INDEX_DAYS_THRESHOLD))
        ) {
            TaskDescription taskDescription = taskItemBuilder.buildSingleItemWorkflow(repositoryDocumentBase.getIdDocument(), status.isSuccess() && !status.isSkipped());
            super.notifyItemQueue(taskDescription, null, "SCHEDULE");
        }

        if (status.isSuccess()) {
            bigRepositoryDAO.updateLastHarvestingDate(
                    Integer.parseInt(repositoryHarvestProperties.getRepositoryId()), new Date());
        }

        return status;
    }

    private boolean checkLicense(String docLicense) {
        return this.licenseStrategy.checkLicense(docLicense);
    }

    private Boolean processFilesystemDocument(CrawlingUrl crawlingUrl, RepositoryDocumentBase repositoryDocumentBase, DocumentDownloadTaskItemStatus status, CrawlingUrlBucket crawlingUrlBucket, Boolean isLocalFile) {
        Path pdfPath = Paths.get(URI.create(crawlingUrl.getCurrentUrl()));
        Path hardLink = Paths.get(this.filesystemDAO.getPdfPath(repositoryDocumentBase.getIdDocument(), Integer.parseInt(this.repositoryHarvestProperties.getRepositoryId())));
        logger.info("Creating hardlink for file: " + pdfPath);
        try {
            hardLink.getParent().toFile().mkdirs();
            Files.deleteIfExists(hardLink);
            Files.createLink(hardLink, pdfPath);
            status.setSuccess(Boolean.TRUE);
            logger.info("Hardlink successfully created: " + hardLink + " original PDF path: " + pdfPath);

            CrawlingUrl sourceUrl = crawlingUrlBucket.pop();
            if (sourceUrl != null) {
                this.documentDownloadDAO.setDownloadSuccessful(repositoryDocumentBase.getIdDocument(), sourceUrl.getOriginalUrl(), sourceUrl.getCurrentUrl());
            } else {
                this.documentDownloadDAO.setDownloadSuccessful(repositoryDocumentBase.getIdDocument(), crawlingUrl.getOriginalUrl(), crawlingUrl.getCurrentUrl());
            }

            isLocalFile = Boolean.TRUE;
        } catch (IOException e) {
            status.setSuccess(Boolean.FALSE);
            logger.error("Error creating hardlink: " + hardLink + " - " + e);
        }
        return isLocalFile;
    }

    private int addUrlsToBucket(long documentId, String oai, DownloadResult downloadResult, CrawlingUrl crawlingUrl,
                                CrawlingUrlBucket crawlingUrlBucket, DocumentDownloadTaskItemStatus status,
                                String filePath) {
        long startParsingUrls = System.nanoTime();

        List<CrawlingUrl> unfilteredUrls = Collections.emptyList();

        try {
            unfilteredUrls = pageParserService.getUrlsFromPage(downloadResult, crawlingUrl, filePath);
        } catch (IOException e) {
            logger.error("Error while reading urls from page", e);
        }

        boolean urlIsPotentialPdf = false;
        boolean isASignpostingRepo = false;
        List<CrawlingUrl> signpostingUrls = new ArrayList<>();
        for (CrawlingUrl u : unfilteredUrls) {
            if (u instanceof SignpostingUrl) {
                isASignpostingRepo = true;
                signpostingUrls.add(u);
                if (!repositoryHarvestProperties.isUseSignpost()) {
                    repositoryHarvestProperties.setUseSignpost(true);
                    repositoriesHarvestPropertiesDAO.insertOrUpdate(repositoryHarvestProperties);
                }

            }
            if (u.getCurrentUrl().contains("pdf")) {
                urlIsPotentialPdf = true;
            }
            SyntheticUrlExtension se = new SyntheticUrlExtension(u.getCurrentUrl());
            if (se.hasExtension()) {
                logger.warn("URL has extention: " + se.getExtension());
            }
        }
        String currentUrl = crawlingUrl.getCurrentUrl();
        if (!signpostingUrls.isEmpty()) {
            unfilteredUrls = signpostingUrls;
        }

        if (unfilteredUrls.isEmpty() && null != status.getIssueType()) {
            status.setIssueType(IssueType.NO_FULL_TEXT_LINKS);
            reportIssue(createIssue(documentId, IssueType.NO_FULL_TEXT_LINKS, IssueType.NO_FULL_TEXT_LINKS.getDescription(), map("url", currentUrl), oai));
        }
        if (!unfilteredUrls.isEmpty() && !urlIsPotentialPdf) {
            status.setIssueType(IssueType.UNSUPPORTED_FILETYPE);
            reportIssue(createIssue(documentId, IssueType.UNSUPPORTED_FILETYPE, IssueType.UNSUPPORTED_FILETYPE.getDescription(), map("url", currentUrl), oai));
        }

        List<CrawlingUrl> filteredUrl = urlFilteringService.applyFilters(unfilteredUrls);
        if (isASignpostingRepo) {
            logger.info("This repo is using signposting, I am going to check only the signposted urls");
            crawlingUrlBucket.clear();
        }
        crawlingUrlBucket.addToTop(filteredUrl);
        logger.debug("New " + filteredUrl.size() + " urls obtained from parsing content of url: " + crawlingUrl.getCurrentUrl()
                + " Harvest level: " + crawlingUrl.getCurrentHarvestLevel(), this.getClass());
        this.logEndTime("Add and process new Urls", startParsingUrls);
        return unfilteredUrls.size();
    }

    private void validateAndSaveDocument(int documentId, String oai, DownloadResult downloadResult, File tmpFilePath,
                                         String metadataDocumentTitle, DocumentDownloadTaskItemStatus status,
                                         CrawlingUrl crawlingUrl, HarvestLevel harvestLevel) {
        try {
            String extension = DocumentFileChecker.getExtension(downloadResult.getContentFirstBytes(), downloadResult.getContentType());
            /**
             * CORE-1764 It was decided that we DO NOT want to do a title
             * match on any link which is level 0 but looks like a PDF
             */
            if (harvestLevel.equals(HarvestLevel.LEVEL_0) || this.repositoryHarvestProperties.isUseSignpost() || DocumentFileChecker.isTitleMatching(metadataDocumentTitle, tmpFilePath.toString(), extension, downloadResult.getContentSize())) {
                logger.debug("Title Matches. Harvest Level: " + harvestLevel.equals(HarvestLevel.LEVEL_0), this.getClass());

                fileExtensionRepository.save(new FileExtension(documentId, FileExtensionType.fromNameEqualsIgnoreCase(extension)));

                File outputFile = filesystemDAO.getDocumentPath(documentId, Integer.parseInt(repositoryHarvestProperties.getRepositoryId()), extension);
                Files.copy(tmpFilePath.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                status.setSuccess(Boolean.TRUE);

                // set original url if it is null
                if (crawlingUrl.getOriginalUrl() == null || crawlingUrl.getOriginalUrl().isEmpty()) {
                    crawlingUrl.setOriginalUrl(crawlingUrl.getCurrentUrl());
                }
            } else {
                logger.debug("Title does not Match", this.getClass());
                // not te PDF we are looking for --> delete to not keep taking space
                status.setIssueType(IssueType.ATTACHMENT_TITLE_MISMATCH);
                reportIssue(createIssue(documentId, IssueType.ATTACHMENT_TITLE_MISMATCH, IssueType.ATTACHMENT_TITLE_MISMATCH.getDescription(), map("metadataTitle", metadataDocumentTitle).with("currentUrl", crawlingUrl.getCurrentUrl()), oai));
            }

        } catch (IOException ex) {

            logger.error(ex.getMessage(), ex);

            if (ex instanceof TextExtractionException) {
                TextExtractionException tee = (TextExtractionException) ex;
                if (tee.getErrorCode() == TextExtractionErrorCodes.DOCUMENT_ENCRYPTED) {
                    status.setIssueType(IssueType.ENCRYPTED_ATTACHMENT);
                    reportIssue(createIssue(documentId, IssueType.ENCRYPTED_ATTACHMENT, IssueType.ENCRYPTED_ATTACHMENT.getDescription(), map("metadataTitle", metadataDocumentTitle).with("url", crawlingUrl.getCurrentUrl()), oai));
                }
                if (tee.getErrorCode() == TextExtractionErrorCodes.UNKNOWN_ATTACHMENT_ENCRPYTION) {
                    status.setIssueType(IssueType.ENCRYPTED_ATTACHMENT);
                    reportIssue(createIssue(documentId, IssueType.ENCRYPTED_ATTACHMENT, "We cannot extract this document due to ", map("metadataTitle", metadataDocumentTitle).with("url", crawlingUrl.getCurrentUrl()), oai));
                }
            } else {
                status.setIssueType(IssueType.ATTACHMENT_IO_EXCEPTION);
            }
        }
    }

    private void logEndTime(String eventName, long startTime) {
        long length = System.nanoTime() - startTime;
        String loggingMessage = "Time to " + eventName + ": " + TimeUnit.MILLISECONDS.convert(length, TimeUnit.NANOSECONDS) + " Milliseconds";

        logger.info(loggingMessage, this.getClass());
    }

    @Override
    public boolean evaluate(List<TaskItemStatus> results, List<TaskItem> taskItems) {
        workerStatus.getTaskStatus().setNumberOfItemsToProcess(taskItems.size());
        return this.workerStatus.getTaskStatus().getNumberOfItemsToProcess().equals(this.workerStatus.getTaskStatus().getProcessedCount());
    }

    private CrawlingUrlBucket fillCrawlingUrlBucket(RepositoryDocumentBase repositoryDocumentBase) {

        HashMap<String, PDFUrlSource> urlFromMetadata = repositoryDocumentBase.getUrls();
        List<CrawlingUrl> filteredUrls = CrawlingUrlParser.parseList(urlFromMetadata, null);

        List<CrawlingUrl> crawlableUrls = new ArrayList<>();

        if (repositoryHarvestProperties.isSameDomainPolicy()) {
            // Remove URL's which are not from the source repository UNLESS there 
            // is only 1 url in the list anyway
            // Known issue: Only 1 URL from the metadata is ever harvested

            for (CrawlingUrl url : filteredUrls) {
                Boolean validDomainException = false;

                for (RepositoryDomainException exception : this.domainExceptions) {
                    if (url.getCurrentUrl().contains(exception.getDomainUrl())) {
                        validDomainException = true;
                    }
                }

                if (url.getCurrentUrl().contains(repositoryDomain)
                        || validDomainException
                        || url.getCurrentUrl().contains("hdl.handle")
                        || (url.getCurrentUrl().contains("doi.org"))) {
                    crawlableUrls.add(url);
                } else {
                    reportIssue(
                            createIssue(repositoryDocumentBase.getIdDocument(),
                                    IssueType.ATTACHMENT_SAME_DOMAIN_POLICY_ENFORCED,
                                    IssueType.ATTACHMENT_SAME_DOMAIN_POLICY_ENFORCED.getDescription(),
                                    map("sourceUrl", url.getCurrentUrl()), repositoryDocumentBase.getOai()
                            ));
                }
            }
        } else {
            crawlableUrls = filteredUrls;
        }
        List<CrawlingUrl> secondStageFilter = this.urlFilteringService.applyFilters(crawlableUrls);

        CrawlingUrlBucket crawlingUrlBucket = new CrawlingUrlBucket();

        List<CrawlingUrl> localFileUrls = new ArrayList<>();

        for (CrawlingUrl url : secondStageFilter) {
            if (!url.getCurrentUrl().startsWith("file://")) {
                crawlingUrlBucket.addToTop(url);
            } else {
                localFileUrls.add(url);
            }
        }

        // predict what the final url (containing the pdf) would be...
        List<CrawlingUrl> urlPredictions = crawlingHeuristicService.predict(repositoryDocumentBase);

        if (!urlPredictions.isEmpty()) {
            logger.info("Predicted urls added to the top of the stack:" + urlPredictions);
        }

        // ... and place them on top to be attempted first
        crawlingUrlBucket.addToTop(urlPredictions);

        if (isAFilesystemRepo) {
            for (CrawlingUrl url : localFileUrls) {
                crawlingUrlBucket.addToTop(url);
            }
        }

        return crawlingUrlBucket;
    }

    private boolean isRestricted(String currentUrl) {
        Boolean restricted = false;
        // For DSpace repositories, &isAllowed=n indicates a download is restricted is some way
        if (currentUrl.contains(".pdf?sequence=") && currentUrl.contains("&isAllowed=n")) {
            restricted = true;
        }
        return restricted;
    }

    private boolean wasRecentlyAttempted(RepositoryDocumentBase repositoryDocumentBase) {
        if (repositoryDocumentBase.getPdfLastAttempt() != null) {
            Date now = new Date();
            long diffInDays = ((now.getTime() - repositoryDocumentBase.getPdfLastAttempt().getTime()) / (1000 * 60 * 60 * 24));
            logger.info("Document hasn't been harvested for " + diffInDays + " days");
            if (diffInDays < NO_REDOWNLOAD_DAYS_THRESHOLD) {
                logger.info("Will not attempt download for {}. A download was attempted {} days ago (which is less than {} days)",
                        repositoryDocumentBase.getIdDocument(),
                        diffInDays,
                        NO_REDOWNLOAD_DAYS_THRESHOLD
                );
                return false;
            }
        }
        return true;
    }

    private boolean canTryToDownloadDocument(RepositoryDocumentBase repositoryDocumentBase) {
        int docId = repositoryDocumentBase.getIdDocument();
        String docLicense = this.articleMetadataDAO
                .getArticleMetadata(docId)
                .getLicense();
        boolean wasRecentlyAttempted = this.wasRecentlyAttempted(repositoryDocumentBase);
        boolean isLicenseFollowed = this.checkLicense(docLicense);
        if (!isLicenseFollowed) {
            logger.warn("Skipped document {} due to license strategy {}", docId, this.licenseStrategy.name());
            if (this.licenseStrategy.equals(LicenseStrategy.RECOGNISE_AND_FOLLOW_LICENSE) && docLicense != null) {
                this.issueReporting.reportIssue(
                        this.issueReporting.createIssue(
                                repositoryDocumentBase.getIdDocument(),
                                IssueType.UNRECOGNIZED_LICENSE_STRING,
                                "License string was not recognised",
                                map("licenseFromDocumentMetadata", docLicense),
                                repositoryDocumentBase.getOai()
                        )
                );
            }
        }
        if (!wasRecentlyAttempted) {
            logger.warn("Skipped document {} due to fulltext already downloaded or recently attempted", docId);
        }
        return wasRecentlyAttempted && isLicenseFollowed;
    }

    @Override
    public void finalEventNotification(TaskStatus taskStatus, boolean taskOverallSuccess) {
        super.finalEventNotification(taskStatus, taskOverallSuccess);

        // but also send a slack message
        SlackMessage messageBody = new SlackMessage();
        String success = taskOverallSuccess ? "success" : "*no* success";
        String message = String.format("Finished repository %s with %s", this.pdfDownloadParameters.getRepositoryId(), success);
        messageBody.setText(message);
        SlackWebhookService.sendMessage(messageBody, "operations-report");
    }

    public RepositoryHarvestProperties getRepositoryHarvestProperties() {
        return repositoryHarvestProperties;
    }

    public void setRepositoryHarvestProperties(RepositoryHarvestProperties repositoryHarvestProperties) {
        this.repositoryHarvestProperties = repositoryHarvestProperties;
    }
}
