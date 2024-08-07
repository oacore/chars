package uk.ac.core.database.service.ctr.impl;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.database.service.ctr.CTRDAO;

/**
 *
 * @author mc26486
 */
@Service
public class MySQLCTRDAO implements CTRDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int getAlgorithmID(String algorithmName, JsonObject algorithmParams) {
        String sql = "SELECT algorithm_id FROM ctr_algorithmIds WHERE "
                + "algorithm_name = ? AND algorithm_parameters = ?";
        int id = 0;
        try {
            id = this.jdbcTemplate.queryForObject(sql, new Object[]{algorithmName, algorithmParams.toString()}, Integer.class);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            String insertSql = "INSERT INTO ctr_algorithmIds (algorithm_name, algorithm_parameters) VALUES(?,?)";
            this.jdbcTemplate.update(insertSql, new Object[]{algorithmName, algorithmParams.toString()});
            id = this.jdbcTemplate.queryForObject(sql, new Object[]{algorithmName, algorithmParams.toString()}, Integer.class);
        }
        return id;
    }

}
