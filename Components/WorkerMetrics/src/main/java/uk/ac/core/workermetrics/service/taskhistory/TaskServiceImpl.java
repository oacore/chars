package uk.ac.core.workermetrics.service.taskhistory;

import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.workermetrics.data.repo.TaskHistoryDAO;
import uk.ac.core.workermetrics.data.repo.TaskHistoryRepository;
import uk.ac.core.workermetrics.service.converter.TaskHistoryConverter;
import uk.ac.core.workermetrics.service.taskhistory.model.TaskHistoryBO;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskHistoryRepository taskHistoryRepository;
    private final TaskHistoryDAO taskHistoryDAO;

    private TaskServiceImpl(TaskHistoryRepository taskHistoryRepository, TaskHistoryDAO taskHistoryDAO) {
        this.taskHistoryRepository = taskHistoryRepository;
        this.taskHistoryDAO = taskHistoryDAO;
    }

    @Override
    public long countTasksByTypeAfter(TaskType taskType, LocalDate day) {
        return taskHistoryRepository.countByTaskTypeWithEndTimeAfter(taskType, day);
    }

    @Override
    public List<TaskHistoryBO> getFailedMetadataDownloadTasksFromYesterday() {
        return taskHistoryDAO.findConsistentlyFailedTasksByTypeFromYesterday(TaskType.METADATA_DOWNLOAD).stream()
                .map(TaskHistoryConverter::toTaskHistoryBO)
                .collect(Collectors.toList());

    }
}