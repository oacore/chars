package uk.ac.core.database.service.task.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskStatus;
import uk.ac.core.database.service.task.TaskDAO;

import static uk.ac.core.database.service.migration.TaskHistoryMigrationHelper.getIncrementalDatesForMigration;
import static uk.ac.core.database.service.migration.TaskHistoryMigrationHelper.getRepositoryIdForMigration;

/**
 *
 * @author mc26486
 */
@Service
public class MySqlTaskDAO implements TaskDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public TaskDescription loadTaskById(String uniqueId) {
        try {
            TaskDescription taskDescription = jdbcTemplate.queryForObject("SELECT * FROM task WHERE id=?", new Object[]{uniqueId}, new BeanPropertyRowMapper<TaskDescription>());
            return taskDescription;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public TaskStatus loadTaskStatusById(String uniqueId) {
        try {
            TaskStatus taskStatus = jdbcTemplate.queryForObject("SELECT * FROM task_status WHERE id=?", new Object[]{uniqueId}, new BeanPropertyRowMapper<TaskStatus>());
            return taskStatus;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public boolean saveTask(TaskDescription task) {
        int result = this.jdbcTemplate.update("INSERT INTO task  "
                + "(unique_id, "
                + "task_type ,"
                + "task_parameters ,"
                + "priority ,"
                + "routing_key ,"
                + "creation_time ,"
                + "start_time ,"
                + "end_time ) "
                + "VALUES (?,?,?,?,?,?,?,?)",
                task.getUniqueId(),
                task.getType().getName(),
                task.getTaskParameters(),
                task.getPriority(),
                task.getRoutingKey(),
                new Timestamp(task.getCreationTime()),
                new Timestamp(task.getStartTime()),
                new Timestamp(task.getEndTime()));
        return result > 0;
    }

    @Override
    //todo
    public boolean saveTaskHistory(final TaskDescription taskDescription, final TaskStatus taskStatus, final String workerName) {

        Pair<LocalDateTime, LocalDateTime> dates = getIncrementalDatesForMigration(taskDescription.getTaskParameters());
        Integer repositoryId = getRepositoryIdForMigration(taskDescription.getTaskParameters());

        String SAVE_HISTORY_SQL
                = "INSERT INTO task_history "
                + "(unique_id, "
                + "worker_name, "
                + "task_type, "
                + "task_parameters, "
                + "from_date, "
                + "to_date, "
                + "id_repository, "
                + "priority, "
                + "routing_key, "
                + "creation_time, "
                + "start_time, "
                + "end_time, "
                + "number_of_items_to_process, "
                + "processed, "
                + "successful, "
                + "success) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatementSetter pss = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, taskDescription.getUniqueId());
                ps.setString(2, workerName);
                ps.setString(3, taskDescription.getType().getName());
                ps.setString(4, taskDescription.getTaskParameters());
                ps.setTimestamp(5, dates.getKey() != null ?
                        Timestamp.valueOf(dates.getKey()) : null);
                ps.setTimestamp(6, dates.getValue() != null ?
                        Timestamp.valueOf(dates.getValue()) : null);
                ps.setInt(7, repositoryId);
                ps.setInt(8, taskDescription.getPriority());
                ps.setString(9, taskDescription.getRoutingKey());
                ps.setTimestamp(10, new Timestamp(taskDescription.getCreationTime()));
                ps.setTimestamp(11, new Timestamp(taskDescription.getStartTime()));
                ps.setTimestamp(12, new Timestamp(taskDescription.getEndTime()));
                ps.setInt(13, taskStatus.getNumberOfItemsToProcess());
                ps.setInt(14, taskStatus.getProcessedCount());
                ps.setInt(15, taskStatus.getSuccessfulCount());
                ps.setBoolean(16, taskStatus.isSuccess());

            }
        };
        System.out.println("SAVE_HISTORY_SQL = " + SAVE_HISTORY_SQL);
        System.out.println("pss = " + pss);
        int result = jdbcTemplate.update(SAVE_HISTORY_SQL, pss);
        return result > 0;
    }

    @Override
    public List<TaskDescription> findSuccessfulTasksByPeriod(String type, int periodInDays) {

        String SQL = "SELECT MAX(t0_.end_time) as end_time, t0_.task_parameters " +
                "FROM task_history t0_ WHERE t0_.task_type IN (?) " +
                "AND t0_.success = 1 AND t0_.start_time > DATE(NOW() - INTERVAL ? DAY) GROUP BY t0_.id_repository";

        List<TaskDescription> results = jdbcTemplate.query(SQL, new Object[]{type, periodInDays}, new TaskDescriptionRowMapper());

        return  results;
    }

    @Override
    public boolean saveTaskStatus(TaskStatus taskStatus) {
        int result = this.jdbcTemplate.update("INSERT INTO task_status (    "
                + "task_id ,"
                + "number_of_items_to_process,"
                + "processed,"
                + "successful,"
                + "success) "
                + "VALUES (?,?,?,?,?)",
                taskStatus.getTaskId(),
                taskStatus.getNumberOfItemsToProcess(),
                taskStatus.getProcessedCount(),
                taskStatus.getSuccessfulCount(),
                taskStatus.isSuccess());

        return result > 0;
    }

  @Override
    public boolean saveSingleItemTask(final TaskDescription taskDescription, final TaskItemStatus taskItemStatus, final String workerName) {
        String SAVE_ITEM_HISTORY_SQL
                = "INSERT INTO single_item_tasks_history "
                + "(unique_id, "
                + "worker_name, "
                + "task_type, "
                + "task_parameters, "
                + "creation_time, "
                + "start_time, "
                + "end_time, "
                + "success) "
                + "VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatementSetter pss = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, taskDescription.getUniqueId());
                ps.setString(2, workerName);
                ps.setString(3, taskDescription.getType().getName());
                ps.setString(4, taskDescription.getTaskParameters());
                ps.setTimestamp(5, new Timestamp(taskDescription.getCreationTime()));
                ps.setTimestamp(6, new Timestamp(taskDescription.getStartTime()));
                ps.setTimestamp(7, new Timestamp(taskDescription.getEndTime()));
                ps.setBoolean(8, taskItemStatus.isSuccess());

            }
        };

        System.out.println("pss = " + pss);
        int result = jdbcTemplate.update(SAVE_ITEM_HISTORY_SQL, pss);
        return result > 0;
    }
}
