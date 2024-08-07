package uk.ac.core.ExtendedMetadataProcessWorker.worker;

import com.google.gson.Gson;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.core.ExtendedMetadataProcessWorker.DownloadStrategies.ExtendedMetadataDownloadStrategy;
import uk.ac.core.ExtendedMetadataProcessWorker.dates.PublicationDateService;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskStatus;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.database.model.DocumentMetadataExtendedAttributes;
import uk.ac.core.database.service.document.ExtendedAttributesDAO;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.database.service.repositorySourceStatistics.RepositorySourceStatisticsDAO;
import uk.ac.core.dataprovider.logic.entity.DataProvider;
import uk.ac.core.dataprovider.logic.service.origin.DataProviderService;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.worker.BasicQueueWorker;

import java.io.File;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author
 */
public class ExtendedMetadataProcessWorker extends BasicQueueWorker {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(ExtendedMetadataProcessWorker.class);

    @Autowired
    private RepositoryDocumentDAO repositoryDocumentDAO;

    @Autowired
    private DataProviderService dataProviderService;

    @Autowired
    private FilesystemDAO filesystemDAO;

    private RepositoryTaskParameters taskParameters;

    private DataProvider dataProvider;

    private Integer repositoryId;

    @Autowired
    private ExtendedAttributesDAO extendedAttributesDAO;

    @Autowired
    private RepositorySourceStatisticsDAO repositorySourceStatistics;

    @Autowired
    private List<ExtendedMetadataDownloadStrategy> extendedMetadataDownloadStrategies;

    @Autowired
    private PublicationDateService publicationDateService;

    AtomicInteger attachmentCount = new AtomicInteger();
    AtomicInteger metadataWithAtLeastOneAttachmentCount = new AtomicInteger();

    ExtendedMetadataDownloadStrategy strategy;

    private int repositorySize;
    private int batchSize;
    private int offset;
    private boolean batchMode;

    public ExtendedMetadataProcessWorker() {
        this.batchSize = 1000;
        this.offset = 0;
        this.batchMode = false;
    }

    @Override
    public void prepare() {

    }

    @Override
    public List<TaskItem> collectData() {
        logger.info("Preparing some values ...");
        this.taskParameters = new Gson().fromJson(this.currentWorkingTask.getTaskParameters(), RepositoryTaskParameters.class);
        this.repositoryId = this.taskParameters.getRepositoryId();
        this.dataProvider = this.dataProviderService.legacyFindById(Long.valueOf(this.repositoryId));
        this.repositorySize = this.repositoryDocumentDAO.countRepositoryDocuments(this.repositoryId);

        this.strategy = this.findStrategy();
        logger.info("Strategy Chosen: {}", this.strategy.getClass().toString());

        if (this.batchMode) {
            logger.info("Batch mode enabled");
        }

        final List<TaskItem> items = new ArrayList<>();
        if (strategy.isCompatible(dataProvider)) {
            if (this.batchMode) {
                List<RepositoryDocument> documents =
                        this.repositoryDocumentDAO.getRepositoryDocumentsByRepositoryId(
                                this.repositoryId, this.offset, this.batchSize);
                documents.forEach(rd -> {
                    if (rd.getDeletedStatus() == DeletedStatus.ALLOWED.getValue()) {
                        Optional<TaskItem> taskItemOpt = this.strategy.repositoryDocumentToMetadataPageProcessTaskItem(rd);
                        taskItemOpt.ifPresent(items::add);
                    }
                });
            } else {
                repositoryDocumentDAO.streamRepositoryDocumentsByRepositoryId(repositoryId, DeletedStatus.ALLOWED, (RepositoryDocument document) -> {
                    Optional<TaskItem> taskItemOpt = strategy.repositoryDocumentToMetadataPageProcessTaskItem(document);
                    if (taskItemOpt.isPresent()) {
                        items.add(taskItemOpt.get());
                    }
                });
            }
        }

        this.offset += this.batchSize;

        logger.info("Number of items to process: {}", items.size());
        return items;
    }

    @Override
    public TaskItemStatus processSingleItem(TaskItem item) {

        MetadataPageProcessTaskItem task = (MetadataPageProcessTaskItem) item;

        DocumentMetadataExtendedAttributes attrs = new DocumentMetadataExtendedAttributes(task.getDocumentId());

        File downloadLocation = new File(filesystemDAO.getMetadataPageDownloadPath(task.getDocumentId(), this.repositoryId));
        Object metadataPageResponse;
        try {
            metadataPageResponse = this.strategy.downloadMetadataPage(task, downloadLocation);
            attrs.setAttachmentCount(
                    this.strategy.attachmentCount(task, metadataPageResponse)
            );

            attrs.setRepositoryMetadataPublicReleaseDate(
                    this.strategy.repositoryMetadataRecordPublishDate(task, metadataPageResponse)
            );
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        if (strategy.isCompatible(dataProvider)) {
            this.attachmentCount.addAndGet(attrs.getAttachmentCount());
            if (attrs.getAttachmentCount() > 0) {
                this.metadataWithAtLeastOneAttachmentCount.addAndGet(1);
            }
            this.publicationDateService.checkAndSavePublicationDate(task.getDocumentId());

            extendedAttributesDAO.save(attrs);
        }
        TaskItemStatus itemStatus = new TaskItemStatus();
        itemStatus.incProcessed();
        itemStatus.incSuccessful();
        itemStatus.setSuccess(true);

        float processed = workerStatus.getTaskStatus().getProcessedCount();
        float toProcess = workerStatus.getTaskStatus().getNumberOfItemsToProcess();
        String percentComplete = (toProcess == 0) ? "NaN%" : String.format("%.2f%%", processed / toProcess);

        logger.info("{}/{} {}%", workerStatus.getTaskStatus().getProcessedCount(), workerStatus.getTaskStatus().getNumberOfItemsToProcess(), percentComplete);
        return itemStatus;

    }

    @Override
    public void collectStatistics(List<TaskItemStatus> results) {
        logger.info("Attachments Found: {}", attachmentCount.get());
        logger.info("Metadata Records with at least 1 attachment: {}", metadataWithAtLeastOneAttachmentCount.get());
        if (strategy.isCompatible(dataProvider)) {
            repositorySourceStatistics.setMetadataWithAttachmentsCount(this.repositoryId, metadataWithAtLeastOneAttachmentCount.get());
        }
    }

    @Override
    public void endPreEventNotification(TaskStatus taskStatus, boolean taskOverallSuccess) {
        this.strategy = null;
    }

    // @todo this could be moved to another class?
    private ExtendedMetadataDownloadStrategy findStrategy() {
        ExtendedMetadataDownloadStrategy potentialStrategy = new ExtendedMetadataDownloadStrategy() {
            @Override
            public boolean isCompatible(DataProvider dataProvider) {
                return false;
            }

            @Override
            public Optional repositoryDocumentToMetadataPageProcessTaskItem(RepositoryDocument repositoryDocument) {
                return Optional.empty();
            }

            @Override
            public Object downloadMetadataPage(TaskItem taskItem, File saveLocation) throws URISyntaxException {
                return null;
            }

            @Override
            public int attachmentCount(TaskItem taskItem, Object data) {
                return 0;
            }

            @Override
            public LocalDateTime repositoryMetadataRecordPublishDate(TaskItem taskItem, Object data) {
                return LocalDateTime.now();
            }
        };

        for (ExtendedMetadataDownloadStrategy testStrat : this.extendedMetadataDownloadStrategies) {
            if (testStrat.isCompatible(dataProvider)) {
                potentialStrategy = testStrat;
                break;
            }
        }
        return potentialStrategy;
    }

    public boolean hasNextBatch() {
        return this.offset + this.batchSize <= this.repositorySize;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public int getRepositorySize() {
        return repositorySize;
    }

    @Override
    protected void notifyStartTask(TaskDescription taskDescription, TaskStatus taskStatus) {
        if (!this.batchMode) {
            super.notifyStartTask(taskDescription, taskStatus);
        }
    }

    @Override
    public void finalEventNotification(TaskStatus taskStatus, boolean taskOverallSuccess) {
        if (!this.batchMode) {
            super.finalEventNotification(taskStatus, taskOverallSuccess);
        }
    }

    public void setBatchMode(boolean batchMode) {
        this.batchMode = batchMode;
    }

    public boolean isBatchMode() {
        return batchMode;
    }
}
