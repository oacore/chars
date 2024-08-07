package uk.ac.core.eventscheduler.database;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class MySQLCrossrefRepositoryDAO implements CrossrefRepositoryDAO {

    @Autowired
    private DocumentRepositoryDAO documentRepositoryDAO;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    /**
     *
     * @return Date or null
     */
    @Override
    public Date getLastUpdateTime() {
       return documentRepositoryDAO.getLastUpdateTime(4786);
    }

    @Override
    public List<Pair<String, Date>> getUnsuccessfulTasks(String taskType, Date fromDate, Date toDate) {
        String sql = "select task_parameters, start_time from task_history where task_parameters like '%repositoryId\":4786%' " +
                "and task_type = ? and success = 0 " +
                "and creation_time BETWEEN ? and ? order by creation_time ";

        return jdbcTemplate.query(sql, new Object[]{taskType, fromDate, toDate}, ((rs, rowNum) ->
                Pair.of(rs.getString("task_parameters"), rs.getDate("start_time"))));
    }
}
