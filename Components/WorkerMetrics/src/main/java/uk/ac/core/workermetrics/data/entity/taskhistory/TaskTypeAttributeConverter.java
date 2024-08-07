package uk.ac.core.workermetrics.data.entity.taskhistory;

import uk.ac.core.common.model.task.TaskType;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class TaskTypeAttributeConverter implements AttributeConverter<TaskType, String> {

    @Override
    public String convertToDatabaseColumn(TaskType taskType) {
        return taskType.getName();
    }

    @Override
    public TaskType convertToEntityAttribute(String columnValue) {
        return TaskType.fromString(columnValue);
    }
}
