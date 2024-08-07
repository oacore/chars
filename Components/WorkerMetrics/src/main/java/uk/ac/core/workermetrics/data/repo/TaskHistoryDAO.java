package uk.ac.core.workermetrics.data.repo;

import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.workermetrics.data.entity.taskhistory.DiagnosticTaskHistory;
import java.util.List;

public interface TaskHistoryDAO {

    List<DiagnosticTaskHistory> findConsistentlyFailedTasksByTypeFromYesterday(TaskType taskType);
}