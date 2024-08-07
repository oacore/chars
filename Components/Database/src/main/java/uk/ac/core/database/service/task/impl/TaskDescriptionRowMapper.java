package uk.ac.core.database.service.task.impl;

import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.common.model.legacy.TaskHistory;
import uk.ac.core.common.model.task.TaskDescription;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskDescriptionRowMapper implements RowMapper<TaskDescription> {
    @Override
    public TaskDescription mapRow(ResultSet resultSet, int i) throws SQLException {
        TaskDescription taskDescription = new TaskDescription();
        taskDescription.setEndTime(resultSet.getTimestamp("end_time").getTime());
        taskDescription.setTaskParameters(resultSet.getString("task_parameters"));
        return taskDescription;
    }
}
