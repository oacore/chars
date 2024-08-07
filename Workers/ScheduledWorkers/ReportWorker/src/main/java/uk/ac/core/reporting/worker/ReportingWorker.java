package uk.ac.core.reporting.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.util.datetime.TimePattern;
import uk.ac.core.database.model.TaskUpdateReporting;
import uk.ac.core.reporting.metrics.AlertService;
import uk.ac.core.reporting.metrics.MetricCollectionService;
import uk.ac.core.reporting.metrics.model.AlertStatus;
import uk.ac.core.reporting.metrics.model.HarvestStep;
import uk.ac.core.reporting.metrics.service.dto.BigRepositoryMetric;
import uk.ac.core.reporting.metrics.service.dto.CompleteGlobalMetricsBO;
import uk.ac.core.reporting.metrics.service.globalmetrics.GlobalMetricsService;
import uk.ac.core.slack.client.SlackWebhookService;
import uk.ac.core.worker.ScheduledWorker;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author lucasanastasiou
 */
@Service
public class ReportingWorker extends ScheduledWorker {

    private final MetricCollectionService metricCollectionService;
    private final GlobalMetricsService globalMetricsService;

    private ReportingTaskItem taskItemToReport;
    private final AlertService alertService;
    private static final String REPOSITORY_STATS = "<a href=\"http://apple.core.ac.uk/dataproviders/%d\" style=\"color:%s\">%d</a> %s";
    private static final String NOT_AVAILABLE = "N/A";
    private static final String ERROR_MSG = "Error during generating a report has occurred.";
    private static final String BRACES_WRAPPER_TEMPLATE = "(%s)";
    private static final String UK_COLOR = "#f44141";
    private static final String OTHERS_COLOR = "#4286f4";
    private static final String DUPLICATE_MESSAGE_TEMPLATE = "(%d failures, %s)";
    private static final String UK_ABBREV = "GB";
    private static final int UNIQUE_REPORTS = 1;
    private static final long DEFAULT_DURATION = 0L;

    private final TaskType taskType = TaskType.REPORTING;

    private static final Logger logger = LoggerFactory.getLogger(ReportingWorker.class);

    public ReportingWorker(MetricCollectionService metricCollectionService, GlobalMetricsService globalMetricsService, AlertService alertService) {
        this.metricCollectionService = metricCollectionService;
        this.globalMetricsService = globalMetricsService;
        this.alertService = alertService;
    }

    @Override
    public String generateReport(List<TaskItemStatus> results, boolean taskOverallSuccess) {

        String template = "Hello, <br>"
                + "this is CHARS, <br> I think you would be interested to know that {0} repositories have been successfully harvested today <br>"
                + "and that {1} repositories have failed the harvesting process. <br>"
                + "also, the following are our performance metrics for the day, compared with {17} <br> <br>"
                + "<hr>"
                + "<b>OVERALL STATISTICS:</b><br>" +
                "<ul>" +
                "   <li>METADATA <small>(Including Deleted and Disabled)</small>: {2}({3})</li>" +
                "   <li>METADATA <small>allowed only</small>: {20} </li>" +
                "   <li>FULLTEXT: {4}({5})</li>" +
                "   <li>PDFs: {6}({7})</li>" +
                "   <li>THUMBNAILS: {8}({9})</li>" +
                "   <li>FRESHNESS UK: {10}({11})</li>" +
                "   <li>FRESHNESS: {12}({13})</li>" +
                "   <li>DOCUMENT FRESHNESS: {18}({19})</li>" +
                "</ul>"
                + "<b>STATISTICS FROM YESTERDAY, GENERATED AT {30} :</b><br>" +
                "<ul>" +
                "   <li>{21} documents were indexed.</li>" +
                "   <li>{22} metadata download tasks were completed and {25} were in the queue.</li>" +
                "   <li>{23} metadata extract tasks were completed and {26} were in the queue.</li>" +
                "   <li>{24} document download tasks were completed and {27} were in the queue.</li>" +
                "   <li>{28} documents were attempted to download and {29} were actually downloaded.</li>" +
                "</ul>"
                + "<hr>" +
                 "<b>BIG REPOSITORY HARVESTING STATISTIC:</b><br>" +
                "{31}"
                + "<hr>" +
                "<br><b>Repository Harvest Statuses</b><br>"
                + "<br>SUCCESSFULLY HARVESTED REPOSITORIES<br>"
                + "<br>{14}<br>"
                + "<br>FAILED REPOSITORIES DURING DOCUMENT DOWNLOAD<br>"
                + "<br>{15}<br>"
                + "<br>FAILED REPOSITORIES DURING METADATA DOWNLOAD/EXTRACT<br>"
                + "<br>{16}<br>" +
                "<br><sub><strong>Interpretation:</strong><br>" +
                "<span style=\"color:" + UK_COLOR + "\">UK</span> - <span style=\"color:" + OTHERS_COLOR + "\">OTHERS</span><br>" +
                "   <em>Failures</em> are the number of failed attempts.<br>" +
                "   <em>Days</em> are the duration of the longest <em>failure</em>. " +
                "If there are no failures - this is just a duration of a task execution.</sub><br>"
                + "<hr>"
                + "<br> Best regards <br>"
                + "<br> Always yours <br>"
                + "<br> CHARS - I know sometimes I am tough, but I have a warm heart inside <br>";

        CompleteGlobalMetricsBO currentMetrics = taskItemToReport.getCurrentCompleteGlobalMetricsBO();
        Optional<CompleteGlobalMetricsBO> lastGlobalMetricsBO = taskItemToReport.getLastGlobalMetricsBO();
        Optional<AlertStatus> diagnosticReport = this.alertService.metricsToAlert(currentMetrics);
        if (diagnosticReport.isPresent()) {
            String notification = MessageFormat.format("<div style=\"background-color:{0};color:white;font-size:1.5rem\">{1}</div>", diagnosticReport.get().getAlertLevel().toString(),
                    diagnosticReport.get().getMessage());
            template = notification + template;

        }

        DecimalFormat decimalFormat = new DecimalFormat("###,###.###");
        DecimalFormat differenceFormat = new DecimalFormat("+###,###.###;-###,###.###");

        List<String> htmlSuccessfulRepos = transformReportTasksToMessages(currentMetrics.getTaskUpdateMetric().getSuccessfulRepos());
        List<String> failedPdfReposMessages = transformReportTasksToMessages(currentMetrics.getTaskUpdateMetric().getFailedReposPdf());
        List<String> htmlFailedReposMd = transformReportTasksToMessages(currentMetrics.getTaskUpdateMetric().getFailedReposMd());

        //TODO: adopt a templating engine.
        Object[] params = new Object[]{
                decimalFormat.format(currentMetrics.getTaskUpdateMetric().getSuccessfulCount()),
                decimalFormat.format(currentMetrics.getTaskUpdateMetric().getFailedCount()),
                decimalFormat.format(currentMetrics.getAllMetadataCount()),
                differenceFormat.format(lastGlobalMetricsBO.map(lastMetrics -> currentMetrics.getAllMetadataCount() - lastMetrics.getAllMetadataCount()).orElse(0L)),
                decimalFormat.format(currentMetrics.getExtractedTextsCount()),
                differenceFormat.format(lastGlobalMetricsBO.map(lastMetrics -> currentMetrics.getExtractedTextsCount() - lastMetrics.getExtractedTextsCount()).orElse(0L)),
                decimalFormat.format(currentMetrics.getDownloadedPdfsCount()),
                differenceFormat.format(lastGlobalMetricsBO.map(lastMetrics -> currentMetrics.getDownloadedPdfsCount() - lastMetrics.getDownloadedPdfsCount()).orElse(0L)),
                decimalFormat.format(currentMetrics.getGeneratedThumbnailsCount()),
                differenceFormat.format(lastGlobalMetricsBO.map(lastMetrics -> currentMetrics.getGeneratedThumbnailsCount() - lastMetrics.getGeneratedThumbnailsCount()).orElse(0L)),
                decimalFormat.format(currentMetrics.getFreshnessGB()),
                differenceFormat.format(lastGlobalMetricsBO.map(lastMetrics -> currentMetrics.getFreshnessGB() - lastMetrics.getFreshnessGB()).orElse(0.0)),
                decimalFormat.format(currentMetrics.getFreshness()),
                differenceFormat.format(lastGlobalMetricsBO.map(lastMetrics -> currentMetrics.getFreshness() - lastMetrics.getFreshness()).orElse(0.0)),
                String.join(", ", htmlSuccessfulRepos),
                String.join(", ", failedPdfReposMessages),
                String.join(", ", htmlFailedReposMd),
                lastGlobalMetricsBO.map(completeGlobalMetricsBO -> completeGlobalMetricsBO.getCreatedAt().toLocalDate().toString()).orElse(NOT_AVAILABLE),
                currentMetrics.getDocumentFreshness() != 0 ? decimalFormat.format(currentMetrics.getDocumentFreshness()) : ERROR_MSG,
                currentMetrics.getDocumentFreshness() != 0 ? lastGlobalMetricsBO.map(lastMetrics -> currentMetrics.getDocumentFreshness() - lastMetrics.getDocumentFreshness()).get() : NOT_AVAILABLE,
                decimalFormat.format(currentMetrics.getAllowedMetadataCount()),
                currentMetrics.getIndexedDocumentsCount(),
                currentMetrics.getCompletedHarvestTasks(HarvestStep.METADATA_DOWNLOAD),
                currentMetrics.getCompletedHarvestTasks(HarvestStep.EXTRACT_METADATA),
                currentMetrics.getCompletedHarvestTasks(HarvestStep.DOCUMENT_DOWNLOAD),
                currentMetrics.getHarvestTasksInQueue(HarvestStep.METADATA_DOWNLOAD),
                currentMetrics.getHarvestTasksInQueue(HarvestStep.EXTRACT_METADATA),
                currentMetrics.getHarvestTasksInQueue(HarvestStep.DOCUMENT_DOWNLOAD),
                currentMetrics.getDocumentDownloadStats().getAttemptedDownloadDocumentsCount(),
                currentMetrics.getDocumentDownloadStats().getSuccessfulDownloadDocumentsCount(),
                currentMetrics.getCreatedAt().toLocalTime().format(DateTimeFormatter.ofPattern(TimePattern.SIMPLE_LOCAL_TIME.toString())),
                getBigRepositoryReport()
        };

        String message = MessageFormat.format(template, params);
        logger.info("Sending message...");

        return message;
    }

    private List<String> transformReportTasksToMessages(List<TaskUpdateReporting> reportTasks) {
        Map<Integer, List<TaskUpdateReporting>> taskReportByRepoId = reportTasks.stream()
                .collect(groupingBy(TaskUpdateReporting::getRepositoryId));

        List<String> reportTasksMessages = new ArrayList<>();
        for (Map.Entry<Integer, List<TaskUpdateReporting>> entry : taskReportByRepoId.entrySet()) {
            if (entry.getValue().size() > UNIQUE_REPORTS) {
                reportTasksMessages.add(transformDuplicateRepoTasksToMsg(entry.getValue()));
            } else {
                reportTasksMessages.add(createRepoHtml(entry.getValue().get(0)));
            }
        }

        return reportTasksMessages;
    }

    private String transformDuplicateRepoTasksToMsg(List<TaskUpdateReporting> duplicateTaskUpdateReporting) {

        int repoId = duplicateTaskUpdateReporting.get(0).getRepositoryId();
        String countryCode = duplicateTaskUpdateReporting.get(0).getCountryCode();

        long longestDurationInMillis = duplicateTaskUpdateReporting.stream()
                .map(task -> Math.abs(task.getLastUpdateTime().getTime() - task.getCreated().getTime()))
                .max(Long::compare)
                .orElse(DEFAULT_DURATION);

        String message = String.format(DUPLICATE_MESSAGE_TEMPLATE,
                duplicateTaskUpdateReporting.size(),
                durationToMessage(longestDurationInMillis));

        return formatTaskReportMessage(message, repoId, countryCode);
    }

    private String createRepoHtml(TaskUpdateReporting taskUpdateReporting) {
        String countryCode = taskUpdateReporting.getCountryCode();
        Integer repoId = taskUpdateReporting.getRepositoryId();
        Date finished = taskUpdateReporting.getLastUpdateTime();
        Date started = taskUpdateReporting.getCreated();
        String durationMessage = NOT_AVAILABLE;
        if (finished != null && started != null) {
            durationMessage = String.format(BRACES_WRAPPER_TEMPLATE,
                    durationToMessage(Math.abs(finished.getTime() - started.getTime()))
            );
        }

        return formatTaskReportMessage(durationMessage, repoId, countryCode);
    }

    private String formatTaskReportMessage(String appendedMessage, long repoId, String countryCode) {
        if (UK_ABBREV.equalsIgnoreCase(countryCode)) {
            return String.format(REPOSITORY_STATS, repoId, UK_COLOR, repoId, appendedMessage);
        } else {
            return String.format(REPOSITORY_STATS, repoId, OTHERS_COLOR, repoId, appendedMessage);
        }
    }

    private String durationToMessage(long durationInMillis) {
        long durationInDays = TimeUnit.DAYS.convert(durationInMillis, TimeUnit.MILLISECONDS);
        long durationInHours = TimeUnit.HOURS.convert(durationInMillis, TimeUnit.MILLISECONDS);
        long durationInMinutes = TimeUnit.MINUTES.convert(durationInMillis, TimeUnit.MILLISECONDS);
        long durationInSeconds = TimeUnit.SECONDS.convert(durationInMillis, TimeUnit.MILLISECONDS);
        String duration;
        if (durationInDays >= 1) {
            duration = durationInDays + " days";
        } else if (durationInHours >= 1) {
            duration = durationInHours + " hours";
        } else if (durationInMinutes >= 1) {
            duration = durationInMinutes + " minutes";
        } else {
            duration = durationInSeconds + " seconds";
        }
        return duration;
    }

    private String getBigRepositoryReport() {
        CompleteGlobalMetricsBO currentMetrics = taskItemToReport.getLastGlobalMetricsBO().orElse(null);
        return currentMetrics == null ? "" : currentMetrics.getBigRepositoryMetrics().stream()
                .map(this::toBigRepositoryReportLine)
                .map(l -> "<li>" + l + "</li>")
                .collect(Collectors.joining("", "<ul>", "</ul>"));
    }

    private String toBigRepositoryReportLine(BigRepositoryMetric metric) {
        return String.format("Repository %s: %s", metric.getRepositoryId(), metric.getTaskLastSuccessDate().entrySet().stream()
                .map(e -> e.getKey() + " last success " + getDaysBetween(e.getValue(), new Date()) + " days ago")
                .collect(Collectors.joining(", ")));
    }

    private long getDaysBetween(Date from, Date to) {
        long diff = to.getTime() - from.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public List<TaskItem> collectData() {
        List<TaskItem> taskItems = new ArrayList<>();
        ReportingTaskItem taskItem = new ReportingTaskItem();
        taskItem.setLastGlobalMetricsBO(globalMetricsService.getLatestGlobalMetric().orElse(null));
        taskItem.setCurrentCompleteGlobalMetricsBO(metricCollectionService.generateMetrics());
        taskItems.add(taskItem);
        return taskItems;

    }

    @Override
    public List<TaskItemStatus> process(List<TaskItem> taskItems) {
        List<TaskItemStatus> statuses = new ArrayList<>();
        taskItemToReport = (ReportingTaskItem) taskItems.get(0);
        globalMetricsService.save(taskItemToReport.getCurrentCompleteGlobalMetricsBO());
        return statuses;
    }

    @Override
    public void collectStatistics(List<TaskItemStatus> results) {

    }

    @Override
    @Scheduled(cron = "0 0 2 * * *")
    public void scheduledStart() {
        this.start();
    }

    @Override
    public TaskDescription generateTaskDescription() {
        TaskDescription task = new TaskDescription();
        task.setCreationTime(System.currentTimeMillis());
        task.setStartTime(System.currentTimeMillis());
        task.setType(taskType);
        return task;
    }

    @Override
    public TaskType getTaskType() {
        return this.taskType;
    }

    @Override
    public void sendNotification(String messageBody) {
        //Still send the Email
        super.sendNotification(messageBody);
        System.out.println("Sending slack message!");
        SlackWebhookService.sendMessage(messageBody, "operations-report");
    }
}
