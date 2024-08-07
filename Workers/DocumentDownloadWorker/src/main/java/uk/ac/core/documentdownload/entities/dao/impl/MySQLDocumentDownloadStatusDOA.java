package uk.ac.core.documentdownload.entities.dao.impl;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.documentdownload.entities.dao.DocumentDownloadStatusDAO;

/**
 *
 * @author mc26486
 */
@Service
public class MySQLDocumentDownloadStatusDOA implements DocumentDownloadStatusDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MySQLDocumentDownloadStatusDOA.class);

    @Override
    public void setDownloadStatus(Integer documentId, String status) {
        if (status == null) {
            status = "UNKNOWN_ERROR";
        }
        if ("false".equals(status)) {
            status = "UNSUCCESSFUL_DOWNLOAD_NO_ERROR_DEFINED";
        } else if ("true".equals(status)) {
            status = "SUCCESSFUL_DOWNLOAD";
        }       
        status = status.toUpperCase();
        logger.debug("Saving Status: " + documentId + " " + status);
        jdbcTemplate.update(
                "REPLACE INTO document_download_document_statuses (id_document, status) VALUES (?, ?)",
                documentId,
                status
        );

    }

}
