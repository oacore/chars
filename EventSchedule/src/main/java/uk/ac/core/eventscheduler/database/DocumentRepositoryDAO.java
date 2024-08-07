package uk.ac.core.eventscheduler.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;

@Service
public class DocumentRepositoryDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     *
     * @return Date or null
     */
    public Date getLastUpdateTime(Integer repositoryId) {
        String sql = "select metadata_added from document where id_repository = " + repositoryId + " order by id_document desc limit 1;";

        try {
            Timestamp ts = this.jdbcTemplate.queryForObject(sql,
                    (resultSet, i) -> resultSet.getTimestamp("metadata_added"));

            return new Date(ts.getTime());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

}
