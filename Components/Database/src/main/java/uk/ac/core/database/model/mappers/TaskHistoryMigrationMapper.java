package uk.ac.core.database.model.mappers;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskHistoryMigrationMapper implements RowMapper<Pair<Integer, String>> {

    @Override
    public Pair<Integer, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Pair.of(rs.getInt("id"), rs.getString("task_parameters"));
    }
}
