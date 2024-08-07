package uk.ac.core.workermetrics.data.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.workermetrics.data.entity.taskhistory.DiagnosticTaskHistory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TaskHistoryDAOImpl implements TaskHistoryDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final int PREVIOUS_DAYS_TO_CHECK_QUANTITY = 15;

    public TaskHistoryDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<DiagnosticTaskHistory> findConsistentlyFailedTasksByTypeFromYesterday(TaskType taskType) {
        List<Map<String, Object>> resultMap = jdbcTemplate.queryForList("SELECT task_parameters, date_diff " +
                "FROM (SELECT" +
                "             t.task_parameters," +
                "             COUNT(t.task_parameters) AS                task_count," +
                "             DATEDIFF(MAX(t.end_time), MIN(t.end_time)) date_diff" +
                "      FROM task_history t" +
                "      WHERE t.end_time > ?" +
                "        AND success = FALSE" +
                "        AND task_type = ?" +
                "      GROUP BY t.task_parameters) s " +
                "WHERE task_count > 3" +
                "  AND date_diff > 1", LocalDate.now().atStartOfDay().minusDays(PREVIOUS_DAYS_TO_CHECK_QUANTITY), taskType.getName());

        List<DiagnosticTaskHistory> results = new ArrayList<>();
        
        for (Map<String, Object> row : resultMap) {
            results.add(new DiagnosticTaskHistory((String) row.get("task_parameters"), (int) row.get("date_diff")));
        }

        return results;
    }
}