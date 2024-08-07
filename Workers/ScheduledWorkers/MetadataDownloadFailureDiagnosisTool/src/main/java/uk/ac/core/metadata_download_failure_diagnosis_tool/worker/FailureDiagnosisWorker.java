package uk.ac.core.metadata_download_failure_diagnosis_tool.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.metadata_download_failure_diagnosis_tool.service.FailureDiagnosisService;
import uk.ac.core.worker.ScheduledWorker;

import java.util.ArrayList;
import java.util.List;

@Component
public class FailureDiagnosisWorker extends ScheduledWorker {
    private static final Logger log = LoggerFactory.getLogger(FailureDiagnosisWorker.class);

    private final FailureDiagnosisService failureDiagnosisService;

    @Autowired
    public FailureDiagnosisWorker(FailureDiagnosisService failureDiagnosisService) {
        this.failureDiagnosisService = failureDiagnosisService;
    }

    @Override
    public String generateReport(List<TaskItemStatus> results, boolean taskOverallSuccess) {
        return this.failureDiagnosisService.generateReport(results, taskOverallSuccess);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.METADATA_DOWNLOAD_FAILURE_DIAGNOSTICS;
    }

    @Override
    // every day at 1 AM
    @Scheduled(cron = "0 0 1 * * *")
    public void scheduledStart() {
        this.start();
    }

    @Override
    public TaskDescription generateTaskDescription() {
        TaskDescription taskDescription = new TaskDescription();
        taskDescription.setType(TaskType.METADATA_DOWNLOAD_FAILURE_DIAGNOSTICS);
        taskDescription.setCreationTime(System.currentTimeMillis());
        taskDescription.setStartTime(System.currentTimeMillis());
        return taskDescription;
    }

    @Override
    public List<TaskItem> collectData() {
        return this.failureDiagnosisService.collectTaskItems();
    }

    @Override
    public void collectStatistics(List<TaskItemStatus> results) {

    }

    @Override
    public List<TaskItemStatus> process(List<TaskItem> taskItems) {
        long start = System.currentTimeMillis(), end;
        log.info("Start processing {} task items", taskItems.size());
        List<TaskItemStatus> statuses = new ArrayList<>();
        for (TaskItem taskItem: taskItems) {
            TaskItemStatus taskItemStatus = this.failureDiagnosisService.processSingleItem(taskItem);
            statuses.add(taskItemStatus);
        }
        end = System.currentTimeMillis();
        log.info("Done in {} ms", end - start);
        return statuses;
    }
}
