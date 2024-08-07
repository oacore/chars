package uk.ac.core.extractmetadata.periodic.crossref.runnables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class DeleteAmbiguousRecordsTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(DeleteAmbiguousRecordsTask.class);

    private static final String DELETE_QUERY = "" +
            "delete from document_raw_metadata where id = ?";

    private final List<Integer> idsToDelete;
    private final JdbcTemplate jdbcTemplate;

    public DeleteAmbiguousRecordsTask(List<Integer> idsToDelete, JdbcTemplate jdbcTemplate) {
        this.idsToDelete = idsToDelete;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run() {
        log.info("Deleting ambiguous records if there any ...");
        for (int drmId: idsToDelete) {
            this.jdbcTemplate.update(DELETE_QUERY, drmId);
            log.info("Deleted `document_raw_metadata` record with ID {}", drmId);
            int delay = 1000; // ms
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("Done");
    }
}
