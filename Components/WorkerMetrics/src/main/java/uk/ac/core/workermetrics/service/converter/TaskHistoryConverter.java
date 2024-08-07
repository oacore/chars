package uk.ac.core.workermetrics.service.converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.core.workermetrics.data.entity.taskhistory.DiagnosticTaskHistory;
import uk.ac.core.workermetrics.service.taskhistory.model.TaskHistoryBO;

public final class TaskHistoryConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskHistoryConverter.class);

    private TaskHistoryConverter() {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
    }

    public static TaskHistoryBO toTaskHistoryBO(DiagnosticTaskHistory diagnosticTaskHistory) {
        TaskHistoryBO taskHistoryBO = new TaskHistoryBO();
        try {
            taskHistoryBO.setRepositoryId(objectMapper.readValue(diagnosticTaskHistory.getTaskParameters(), TaskParameters.class).getRepositoryId());
            taskHistoryBO.setDaysPassedAfterTheFirstTry(diagnosticTaskHistory.getDaysPassedAfterTheFirstTry());
        } catch (JsonProcessingException e) {
            LOGGER.warn("Failed to convert task parameters of task history. Exception: ", e);
            throw new IllegalArgumentException();
        }
        return taskHistoryBO;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class TaskParameters {

        private int repositoryId;

        public TaskParameters() {

        }

        public int getRepositoryId() {
            return repositoryId;
        }

        public void setRepositoryId(int repositoryId) {
            this.repositoryId = repositoryId;
        }
    }
}