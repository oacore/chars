package uk.ac.core.workermetrics.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.workermetrics.data.entity.taskhistory.TaskHistory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Task history repository.
 */
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Integer> {

    default Long countByTaskTypeWithEndTimeAfter(TaskType taskType, LocalDate localDate) {
        return countByTaskTypeAndEndTimeAfter(taskType, localDate.atStartOfDay());
    }

    Long countByTaskTypeAndEndTimeAfter(TaskType taskType, LocalDateTime localDateTime);

    TaskHistory findTopByRepositoryIdOrderByStartTimeDesc(int repositoryId);
}