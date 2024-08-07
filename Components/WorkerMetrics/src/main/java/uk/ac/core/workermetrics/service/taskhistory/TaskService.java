package uk.ac.core.workermetrics.service.taskhistory;

import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.workermetrics.service.taskhistory.model.TaskHistoryBO;
import java.time.LocalDate;
import java.util.List;

/**
 * Task service
 */
public interface TaskService {

    long countTasksByTypeAfter(TaskType taskType, LocalDate day);

    List<TaskHistoryBO> getFailedMetadataDownloadTasksFromYesterday();
}
