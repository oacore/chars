package uk.ac.core.eventscheduler.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class MySQLNewRepositoryDao implements NewRepositoryDao {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void insertUnharvestedIntoScheduledRepository() {
        String sql = "INSERT INTO scheduled_repository " +
                "(id_repository, scheduled_state, repository_priority, last_time_scheduled, prevent_harvest_until) \n" +
                "SELECT id_repository, \"NEW\", \"NORMAL\", DATE_SUB(NOW(),INTERVAL 20 YEAR), CURRENT_TIMESTAMP() FROM repository" +
                " WHERE id_repository NOT IN (SELECT id_repository FROM scheduled_repository);";

        jdbcTemplate.update(sql);
    }
}
