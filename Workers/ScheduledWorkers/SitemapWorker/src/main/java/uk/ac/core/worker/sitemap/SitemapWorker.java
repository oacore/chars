package uk.ac.core.worker.sitemap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.worker.ScheduledWorker;
import uk.ac.core.worker.sitemap.exception.SitemapException;
import uk.ac.core.worker.sitemap.service.SitemapService;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class SitemapWorker extends ScheduledWorker {
    private static final TaskType TASK_TYPE = TaskType.SITEMAPS_GENERATION;

    private static final String SITEMAP_ROUTINE_STARTED_MSG = "Sitemap generation routine has started.";
    private static final String SITEMAPS_SUCCESSFULLY_GENERATED_MSG = "Sitemaps were successfully generated.";
    private static final String SUCCESSFUL_REPORT_TEMPLATE_MSG = SITEMAPS_SUCCESSFULLY_GENERATED_MSG +
            " It took %s day(s) %s hour(s) %s minute(s) %s second(s).";
    private static final String SITEMAPS_GENERATION_FAILED_MSG = "Sitemaps weren't successfully generated.";

    private String reportMessage = SITEMAPS_GENERATION_FAILED_MSG;

    private static final Logger LOGGER = LoggerFactory.getLogger(SitemapWorker.class);

    private final SitemapService sitemapService;

    public SitemapWorker(SitemapService sitemapService) {
        this.sitemapService = sitemapService;
    }

    @Override
    public List<TaskItemStatus> process(List<TaskItem> taskItems) {
        try {
            LOGGER.info(SITEMAP_ROUTINE_STARTED_MSG);
            Instant startTime = Instant.now();

            sitemapService.generateSitemaps();

            Instant finishTime = Instant.now();
            LOGGER.info(SITEMAPS_SUCCESSFULLY_GENERATED_MSG);

            reportMessage = composeReportMessage(Duration.between(startTime, finishTime));

        } catch (SitemapException e) {
            LOGGER.error(e.getMessage(), e);
            reportMessage = reportMessage + " " + e.getMessage();
        }
        return null;
    }

    private static String composeReportMessage(Duration duration) {
        return String.format(SUCCESSFUL_REPORT_TEMPLATE_MSG, duration.toDays(),
                duration.toHours() - TimeUnit.DAYS.toHours(duration.toDays()),
                duration.toMinutes() - TimeUnit.HOURS.toMinutes(duration.toHours()),
                duration.getSeconds() - TimeUnit.MINUTES.toSeconds(duration.toMinutes()));
    }

    @Override
    public TaskDescription generateTaskDescription() {
        TaskDescription task = new TaskDescription();
        task.setCreationTime(System.currentTimeMillis());
        task.setStartTime(System.currentTimeMillis());
        task.setType(TASK_TYPE);
        return task;
    }

    @Override
    public TaskType getTaskType() {
        return TASK_TYPE;
    }

    @Override
    public List<TaskItem> collectData() {
        return null;
    }

    @Override
    public String generateReport(List<TaskItemStatus> results, boolean taskOverallSuccess) {
        return reportMessage;
    }

    //Generate sitemaps every 1th of every month at 03:00 am
    @Scheduled(cron = "0 0 3 1 * ?")
    public void scheduledStart() {
        this.start();
    }

    @Override
    public void collectStatistics(List<TaskItemStatus> results) {
    }

    @Override
    public boolean evaluate(List<TaskItemStatus> results, List<TaskItem> taskItems) {
        return true;
    }
}