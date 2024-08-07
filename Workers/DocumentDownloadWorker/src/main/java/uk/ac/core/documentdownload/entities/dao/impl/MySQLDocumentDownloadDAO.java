package uk.ac.core.documentdownload.entities.dao.impl;

import java.util.LinkedList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.documentdownload.entities.dao.DocumentDownloadDAO;
import uk.ac.core.documentdownload.worker.DefaultDocumentDownloadWorker;

/**
 *
 * @author mc26486
 */
@Service
public class MySQLDocumentDownloadDAO implements DocumentDownloadDAO {

    private final List<Object[]> successfulDocumentsBatch = new LinkedList<>();

    private final List<Object[]> unsuccessfulDocumentsBatch = new LinkedList<>();

    private static final Integer DOCUMENT_STATUS_MAX_BATCH_SIZE = 1;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MySQLDocumentDownloadDAO.class);

    private static final String SUCCESSFUL_DOCUMENT_QUERY = "UPDATE document AS d "
            + "	JOIN document_urls AS du ON d.id_document = du.id_document "
            + "	SET "
            + "	    d.url = ?, "
            + "	    d.url_tr = substr(?, 1, 1000), "
            + "	    d.pdf_status = 1, "
            + "	    d.id_document_url = du.id, "
            + "     d.pdf_first_attempt = CASE "
            + "	        WHEN d.pdf_first_attempt IS NULL "
            + "	        THEN NOW() "
            + "	        ELSE d.pdf_first_attempt "
            + "	        END, "
            + "	    d.pdf_first_attempt_successful = CASE "
            + "	        WHEN d.pdf_first_attempt_successful IS NULL "
            + "	        THEN NOW() "
            + "	        ELSE d.pdf_first_attempt_successful "
            + "	        END, "
            + "	    d.pdf_last_attempt = NOW(), "
            + "	    d.pdf_last_attempt_successful = NOW() "
            + "	WHERE "
            + "	    d.id_document = ?;";

    private static final String UNSUCCESSFUL_DOCUMENT_QUERY = "UPDATE document "
            + "	SET "
            + "	    url = null, "
            + "     url_tr = null, "
            + "	    pdf_status = 0, "
            + "     id_document_url = null,"
            + "     pdf_last_attempt = NOW(), "
            + "     pdf_first_attempt = CASE "
            + "	        WHEN pdf_first_attempt IS NULL "
            + "	        THEN NOW() "
            + "	        ELSE pdf_first_attempt "
            + "	        END "
            + "	WHERE id_document = ?;";

    @Override
    public void setDownloadSuccessful(Integer documentId, String originalUrl, String downloadUrl) {

        if (originalUrl == null) {
            originalUrl = downloadUrl;
        }
        try {
            this.jdbcTemplate.update(SUCCESSFUL_DOCUMENT_QUERY, new Object[]{downloadUrl, downloadUrl, documentId});
        } catch (DataAccessException ex) {
            logger.error(ex.getMessage(), ex);
        } 

    }

    /**
     *
     * @param documentId
     */
    @Override
    public void setDownloadUnsuccessful(Integer documentId) {
        try {
            this.jdbcTemplate.update(UNSUCCESSFUL_DOCUMENT_QUERY, new Object[]{documentId});
        } catch (DataAccessException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     *
     */
    @Override
    public void flushDocumentStatus() {
        logger.info("Flushing document status.", this.getClass());
        this.processDocumentStatusBatch(SUCCESSFUL_DOCUMENT_QUERY, this.successfulDocumentsBatch);
        this.processDocumentStatusBatch(UNSUCCESSFUL_DOCUMENT_QUERY, this.unsuccessfulDocumentsBatch);
        this.successfulDocumentsBatch.clear();
        this.unsuccessfulDocumentsBatch.clear();
    }

    /**
     *
     * @param sql
     * @param batchList
     */
    private void processDocumentStatusBatch(String sql, final List<Object[]> batchList) {

        logger.info("Updating PDF status. Number of docs to be updated: " + batchList.size());

        try {
            int[] result = this.jdbcTemplate.batchUpdate(sql, batchList);
        } catch (DataAccessException ex) {
            logger.error("Document status update failed", ex);
        }
    }
}
