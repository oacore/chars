package uk.ac.core.reindexinvoke;

import java.util.List;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.database.service.document.impl.MySQLRepositoryDocumentDAO;

/**
 *
 * @author samuel
 */
@Service
public class DatabaseQuery {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MySQLRepositoryDocumentDAO.class);

    JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseQuery(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Integer> getIndexableIds(int from, int to) {
        String sql = "SELECT id_document "
                + "FROM document "
                + "WHERE  id_document >= ? AND id_document <= ? AND deleted <> 1 "
                + "ORDER BY id_document ASC";
        try {
            List<Integer> result = jdbcTemplate.queryForList(sql, Integer.class, from, to);
            return result;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }
}
