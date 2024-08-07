package uk.ac.core.reporting.metrics;

import org.springframework.stereotype.Service;
import uk.ac.core.common.model.legacy.ActionType;
import uk.ac.core.database.model.TaskUpdateReporting;
import uk.ac.core.reporting.metrics.model.AlertLevel;
import uk.ac.core.reporting.metrics.model.AlertStatus;
import uk.ac.core.reporting.metrics.service.dto.CompleteGlobalMetricsBO;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;

@Service
public class AlertServiceImpl implements AlertService {

    public static final String QUICKLY_FINISHING_TASKS_STRING =
            "The number of {0} tasks that finished really quickly (<10s) is suspiciously high ({1} of them).";
    public static final String NO_MD_TASKS_STRING=
            "There were no Metadata Download tasks completion today.";
    private final List<Function<CompleteGlobalMetricsBO, Optional<AlertStatus>>> RULES = Arrays.asList(
            this::checkNumberOfMDFailures,
            this::checkNumberOfDDFailures,
            this::checkNoMdCompletions
    );


    @Override
    public Optional<AlertStatus> metricsToAlert(CompleteGlobalMetricsBO completeGlobalMetricsBO) {
        List<Optional<AlertStatus>> results = new ArrayList<>();
        for (Function<CompleteGlobalMetricsBO, Optional<AlertStatus>> rule : RULES) {
            results.add(rule.apply(completeGlobalMetricsBO));
        }
        for (Optional<AlertStatus> result:results){
            if (result.isPresent()){
                return result;
            }
        }
        return Optional.empty();
    }

    private Optional<AlertStatus> checkNumberOfMDFailures(CompleteGlobalMetricsBO completeGlobalMetricsBO) {
        TaskUpdateMetric taskUpdateMetric = completeGlobalMetricsBO.getTaskUpdateMetric();
        List<TaskUpdateReporting> failedReposMd = taskUpdateMetric.getFailedReposMd();
        long quicklyFinishingTasks = getQuicklyFinishingTasks(failedReposMd);
        if (quicklyFinishingTasks > 5) {
            return Optional.of(
                    new AlertStatus(MessageFormat.format(QUICKLY_FINISHING_TASKS_STRING,
                            "metadata_download", quicklyFinishingTasks), AlertLevel.RED));
        } else {
            return Optional.empty();
        }
    }

    private Optional<AlertStatus> checkNumberOfDDFailures(CompleteGlobalMetricsBO completeGlobalMetricsBO) {
        TaskUpdateMetric taskUpdateMetric = completeGlobalMetricsBO.getTaskUpdateMetric();
        List<TaskUpdateReporting> failedReposMd = taskUpdateMetric.getFailedReposPdf();
        long quicklyFinishingTasks = getQuicklyFinishingTasks(failedReposMd);
        if (quicklyFinishingTasks > 5) {
            return Optional.of(
                    new AlertStatus(MessageFormat.format(QUICKLY_FINISHING_TASKS_STRING,
                            "document_download", quicklyFinishingTasks), AlertLevel.AMBER));
        } else {
            return Optional.empty();
        }
    }


    private Optional<AlertStatus> checkNoMdCompletions(CompleteGlobalMetricsBO completeGlobalMetricsBO) {
        TaskUpdateMetric taskUpdateMetric = completeGlobalMetricsBO.getTaskUpdateMetric();
        List<TaskUpdateReporting> failedReposMd = taskUpdateMetric.getFailedReposPdf();
        List<TaskUpdateReporting> taskUpdates = taskUpdateMetric.getTaskUpdates();
        for (TaskUpdateReporting taskUpdateReporting : taskUpdates) {
            if (taskUpdateReporting.getOperation().equals(ActionType.METADATA_DOWNLOAD)) {
                return Optional.empty();
            }
        }
        return Optional.of(
                new AlertStatus(MessageFormat.format(NO_MD_TASKS_STRING,
                        new Object[]{}), AlertLevel.AMBER));
    }


    private long getQuicklyFinishingTasks(List<TaskUpdateReporting> failedReposMd) {
        long countQuicklyFinishingTasks = 0;
        for (TaskUpdateReporting taskUpdateReporting : failedReposMd) {
            Date finished = taskUpdateReporting.getLastUpdateTime();
            Date started = taskUpdateReporting.getCreated();
            long durationInMillis = Math.abs(finished.getTime() - started.getTime());
            if (durationInMillis < 10_000) {
                countQuicklyFinishingTasks++;
            }
        }
        return countQuicklyFinishingTasks;
    }

}
