package uk.ac.core.reporting.metrics;

import org.junit.jupiter.api.Test;
import uk.ac.core.database.model.TaskUpdateReporting;
import uk.ac.core.reporting.metrics.model.AlertStatus;
import uk.ac.core.reporting.metrics.model.DocumentDownloadStats;
import uk.ac.core.reporting.metrics.model.Throughput;
import uk.ac.core.reporting.metrics.service.dto.CompleteGlobalMetricsBO;
import uk.ac.core.reporting.metrics.service.dto.OverallStatsBuilder;
import uk.ac.core.reporting.metrics.service.dto.StatsPerDay;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class AlertServiceTest {

    public static final CompleteGlobalMetricsBO completeGlobalMetricsBO = new CompleteGlobalMetricsBO(
            new OverallStatsBuilder().build(),
            new StatsPerDay(10L, new DocumentDownloadStats(10L, 11L), new Throughput()),
            LocalDateTime.now()
    );

    @Test
    public void testMetricsToAlertMdCase() {

        int numberOfFastTasks = 10;
        completeGlobalMetricsBO.setTaskUpdateMetric(generateBrokenTaskUpdateMetric("md", numberOfFastTasks, numberOfFastTasks * 2));
        AlertService alertService = new AlertServiceImpl();
        Optional<AlertStatus> alertStatus = alertService.metricsToAlert(completeGlobalMetricsBO);
        assertEquals(alertStatus.get().getMessage(),
                MessageFormat.format(AlertServiceImpl.QUICKLY_FINISHING_TASKS_STRING, "metadata_download", numberOfFastTasks));
    }

    @Test
    void testMetricsToAlertDDCase() {
        int numberOfFastTasks = 10;
        completeGlobalMetricsBO.setTaskUpdateMetric(generateBrokenTaskUpdateMetric("dd", numberOfFastTasks, numberOfFastTasks * 2));
        AlertService alertService = new AlertServiceImpl();
        alertService.metricsToAlert(completeGlobalMetricsBO);
        Optional<AlertStatus> alertStatus = alertService.metricsToAlert(completeGlobalMetricsBO);
        assertEquals(alertStatus.get().getMessage(),
                MessageFormat.format(AlertServiceImpl.QUICKLY_FINISHING_TASKS_STRING, "document_download", numberOfFastTasks));

    }

    @Test
    void testMetricsToNoMD() {
        completeGlobalMetricsBO.setTaskUpdateMetric(generateBrokenTaskUpdateMetric("md", 0, 0));
        AlertService alertService = new AlertServiceImpl();
        alertService.metricsToAlert(completeGlobalMetricsBO);
        Optional<AlertStatus> alertStatus = alertService.metricsToAlert(completeGlobalMetricsBO);
        assertEquals(alertStatus.get().getMessage(), MessageFormat.format(AlertServiceImpl.NO_MD_TASKS_STRING, "document_download", 9));
    }


    private TaskUpdateMetric generateBrokenTaskUpdateMetric(String type, int fastTask, int totalTasks) {
        TaskUpdateMetric taskUpdateMetric = new TaskUpdateMetric();
        List<TaskUpdateReporting> taskUpdateReportings = new ArrayList<>();
        for (int i = 0; i < fastTask; i++) {
            taskUpdateReportings.add(getTaskUpdate(0L));
        }
        while (taskUpdateReportings.size() < totalTasks) {
            taskUpdateReportings.add(getTaskUpdate(110L));
        }

        taskUpdateMetric.setFailedReposPdf(new ArrayList<>());
        taskUpdateMetric.setFailedReposMd(new ArrayList<>());
        taskUpdateMetric.setTaskUpdates(new ArrayList<>());

        switch (type) {
            case "md":
                taskUpdateMetric.setFailedReposMd(taskUpdateReportings);
                taskUpdateMetric.setFailedReposPdf(new ArrayList<>());
                break;
            case "dd":
                taskUpdateMetric.setFailedReposMd(new ArrayList<>());
                taskUpdateMetric.setFailedReposPdf(taskUpdateReportings);
                break;
        }
        return taskUpdateMetric;
    }

    private TaskUpdateReporting getTaskUpdate(Long durationInS) {
        TaskUpdateReporting taskUpdateReporting = new TaskUpdateReporting();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before = LocalDateTime.now().minusSeconds(durationInS);
        taskUpdateReporting.setLastUpdateTime(convertToDateViaInstant(now));
        taskUpdateReporting.setCreated(convertToDateViaInstant(before));
        return taskUpdateReporting;
    }

    private Date convertToDateViaInstant(LocalDateTime dateToConvert) {
        return java.util.Date
                .from(dateToConvert.atZone(ZoneId.systemDefault())
                        .toInstant());
    }


}