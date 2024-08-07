package uk.ac.core.database.model.mappers;

import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.database.model.BigRepoHarvestingStatistic;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BigRepositoryMetricMapper implements RowMapper<BigRepoHarvestingStatistic> {

    @Override
    public BigRepoHarvestingStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {
        BigRepoHarvestingStatistic result = new BigRepoHarvestingStatistic();

        result.setIdRepository(rs.getInt("id_repository"));
        result.setUniqueId(rs.getString("unique_id"));
        result.setRoutingKey(rs.getString("task_type"));
        result.setTaskParameters(rs.getString("task_parameters"));

        return result;
    }
}
