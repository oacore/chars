package uk.ac.core.reporting.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.legacy.ActionType;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.database.model.TaskUpdateReporting;
import uk.ac.core.database.model.TaskUpdateStatus;
import uk.ac.core.database.service.document.DocumentDAO;
import uk.ac.core.database.service.updates.TaskUpdatesDAO;
import uk.ac.core.elasticsearch.repositories.ArticleMetadataRepository;
import uk.ac.core.elasticsearch.services.ArticleMetadataService;
import uk.ac.core.reporting.metrics.database.DocumentFreshnessDAO;
import uk.ac.core.reporting.metrics.model.DocumentDownloadStats;
import uk.ac.core.reporting.metrics.model.HarvestStep;
import uk.ac.core.reporting.metrics.model.HarvestTaskMetrics;
import uk.ac.core.reporting.metrics.model.Throughput;
import uk.ac.core.reporting.metrics.service.dto.CompleteGlobalMetricsBO;
import uk.ac.core.reporting.metrics.service.dto.OverallStats;
import uk.ac.core.reporting.metrics.service.dto.OverallStatsBuilder;
import uk.ac.core.reporting.metrics.service.dto.StatsPerDay;
import uk.ac.core.workermetrics.service.taskhistory.TaskService;
import uk.ac.core.workermetrics.data.state.ScheduledState;
import uk.ac.core.workermetrics.service.document.DocumentDownloadMetricsService;
import uk.ac.core.workermetrics.service.dto.DocumentDownloadMetricsBO;
import uk.ac.core.workermetrics.service.scheduled.ScheduledRepoService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lucasanastasiou
 */
@Service
public class MetricCollectionService {

    private final TaskUpdatesDAO taskUpdatesDAO;
    private final DocumentDAO documentDAO;
    private final ArticleMetadataRepository articleMetadataRepository;
    private final ArticleMetadataService articleMetadataService;
    private final DocumentFreshnessDAO documentFreshnessDAO;
    private final TaskService taskService;
    private final ScheduledRepoService scheduledRepoService;
    private final DocumentDownloadMetricsService documentDownloadMetricsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricCollectionService.class);

    public MetricCollectionService(TaskUpdatesDAO taskUpdatesDAO,
                                   DocumentDAO documentDAO,
                                   ArticleMetadataRepository articleMetadataRepository,
                                   ArticleMetadataService articleMetadataService,
                                   DocumentFreshnessDAO documentFreshnessDAO,
                                   TaskService taskService,
                                   ScheduledRepoService scheduledRepoService,
                                   DocumentDownloadMetricsService documentDownloadMetricsService) {
        this.taskUpdatesDAO = taskUpdatesDAO;
        this.documentDAO = documentDAO;
        this.articleMetadataRepository = articleMetadataRepository;
        this.articleMetadataService = articleMetadataService;
        this.documentFreshnessDAO = documentFreshnessDAO;        
        this.taskService = taskService;
        this.scheduledRepoService = scheduledRepoService;
        this.documentDownloadMetricsService = documentDownloadMetricsService;
    }

    public CompleteGlobalMetricsBO generateMetrics() {
        return createGlobalMetricsBO();
    }

    private CompleteGlobalMetricsBO createGlobalMetricsBO() {
        TaskUpdateMetric taskUpdateMetric = generateTaskUpdateMetric();
        CompleteGlobalMetricsBO completeGlobalMetricsBO = new CompleteGlobalMetricsBO(createOverallStats(taskUpdateMetric),
                createPerDayStats(), LocalDateTime.now());
        completeGlobalMetricsBO.setTaskUpdateMetric(taskUpdateMetric);

        LOGGER.info(completeGlobalMetricsBO.toString());

        return completeGlobalMetricsBO;
    }

    private DocumentDownloadStats collectDocumentDownloadStats() {
        long attemptedDocuments = 0;
        long successDownloadedDocuments = 0;
        for (DocumentDownloadMetricsBO documentDownloadMetricsBO : documentDownloadMetricsService.getDocumentMetricsFromYesterday()) {
            attemptedDocuments += documentDownloadMetricsBO.getNumberOfDocumentsAttempted();
            successDownloadedDocuments += documentDownloadMetricsBO.getNumberOfDocumentsDownloaded();
        }
        return new DocumentDownloadStats(attemptedDocuments, successDownloadedDocuments);
    }

    private StatsPerDay createPerDayStats() {
        return new StatsPerDay(countIndexedDocsFromYesterday(), collectDocumentDownloadStats(), getThroughput());
    }

    private OverallStats createOverallStats(TaskUpdateMetric taskUpdateMetric) {
        return new OverallStatsBuilder()
                .allMetadataCount(articleMetadataRepository.count())
                .allowedMetadataCount(articleMetadataRepository.countByDeleted("ALLOWED"))
                .downloadedPdfsCount(articleMetadataService.countAllDownloadedArticles())
                .extractedTextsCount(articleMetadataRepository.countByFullText("*"))
                .thumbnailsCount(documentDAO.getPreviewCount())
                .freshnessGB(taskUpdatesDAO.getAverageFreshnessWithCountryCode("GB"))
                .freshness(taskUpdatesDAO.getAverageFreshness())
                .harvestedReposCount(taskUpdateMetric.getFailedCount() + taskUpdateMetric.getSuccessfulCount())
                .documentFreshness(documentFreshnessDAO.getDocumentFreshness().orElse(0))
                .build();
    }

    private HarvestTaskMetrics<HarvestStep> retrieveHarvestTaskMetricsFromYesterday(HarvestStep harvestStep, TaskType taskType, ScheduledState scheduledState) {
        return new HarvestTaskMetrics<>(
                harvestStep,
                taskService.countTasksByTypeAfter(taskType, LocalDate.now().minusDays(1)),
                scheduledRepoService.countAllByScheduledState(scheduledState)
        );
    }

    private Throughput getThroughput() {
        return new Throughput()
                .addHarvestMetrics(retrieveHarvestTaskMetricsFromYesterday(HarvestStep.METADATA_DOWNLOAD, TaskType.METADATA_DOWNLOAD, ScheduledState.IN_DOWNLOAD_METADATA_QUEUE))
                .addHarvestMetrics(retrieveHarvestTaskMetricsFromYesterday(HarvestStep.EXTRACT_METADATA, TaskType.EXTRACT_METADATA, ScheduledState.IN_EXTRACT_METADATA_QUEUE))
                .addHarvestMetrics(retrieveHarvestTaskMetricsFromYesterday(HarvestStep.DOCUMENT_DOWNLOAD, TaskType.DOCUMENT_DOWNLOAD, ScheduledState.IN_DOCUMENT_DOWNLOAD_QUEUE));
    }

    private long countIndexedDocsFromYesterday() {
        return documentDAO.countIndexedDocsSince(LocalDate.now().minusDays(1));
    }

    private TaskUpdateMetric generateTaskUpdateMetric() {
        List<TaskUpdateReporting> taskUpdates = taskUpdatesDAO.getUpdatesOfTheDayForReporting();
        List<TaskUpdateReporting> successfulRepos = new ArrayList<>();
        List<TaskUpdateReporting> failedReposPdf = new ArrayList<>();
        List<TaskUpdateReporting> failedReposMd = new ArrayList<>();
        long countSuccessful = 0;
        long countFailures = 0;
        if (taskUpdates != null) {
            for (TaskUpdateReporting taskUpdate : taskUpdates) {
                if (taskUpdate.getStatus().equals(TaskUpdateStatus.SUCCESSFUL)) {
                    if (taskUpdate.getOperation().equals(ActionType.DOCUMENT)) {
                        successfulRepos.add(taskUpdate);
                        countSuccessful++;
                    }
                } else {
                    if (taskUpdate.getOperation().equals(ActionType.DOCUMENT)) {
                        failedReposPdf.add(taskUpdate);
                        countFailures++;
                    } else {
                        failedReposMd.add(taskUpdate);
                    }
                }
            }
        }
        TaskUpdateMetric taskUpdateMetric = new TaskUpdateMetric();
        taskUpdateMetric.setFailedCount(countFailures);
        taskUpdateMetric.setSuccessfulCount(countSuccessful);
        taskUpdateMetric.setFailedReposMd(failedReposMd);
        taskUpdateMetric.setFailedReposPdf(failedReposPdf);
        taskUpdateMetric.setSuccessfulRepos(successfulRepos);
        taskUpdateMetric.setTaskUpdates(taskUpdates);
        return taskUpdateMetric;

    }
}
