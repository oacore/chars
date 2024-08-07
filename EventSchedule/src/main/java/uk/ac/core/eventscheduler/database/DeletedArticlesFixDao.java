package uk.ac.core.eventscheduler.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This class used to be doing the next job:
 * <p>
 *     Get batch of documents that have deleted status and check if it's `truly` deleted.
 * </p>
 * <p>
 *     The reason this job launched is a bug in CORE data.
 *     By now, March 5, 2024, the bug seems to be gone.
 *     That's why the periodic task is no longer needed.
 * </p>
 */
@Deprecated
@Service
public class DeletedArticlesFixDao {
    private static final Logger log = LoggerFactory.getLogger(DeletedArticlesFixDao.class);

    private final JdbcTemplate template;

    @Autowired
    public DeletedArticlesFixDao(JdbcTemplate template) {
        this.template = template;
    }

    public List<Integer> getBatch(final int size) {
        String sql = "" +
                "select d.id_document\n" +
                "from document d\n" +
                "where d.deleted = 1 and\n" +
                "      d.date_time_record_deleted > '2022-07-01'" +
                "limit ?, ?";
        List<Integer> results = new ArrayList<>();
        int offset = 0;
        int batchSize = 500;
        boolean hasMoreRecords = true;

        while (hasMoreRecords) {
            Object[] parameters = {offset, batchSize};
            List<Integer> batch = this.template.query(
                    sql,
                    parameters,
                    (resultSet, i) -> resultSet.getInt("id_document")
            );

            results.addAll(batch);

            offset += batchSize;

            if (offset >= size) {
                hasMoreRecords = false;
            }
        }

        return results;
    }
}
