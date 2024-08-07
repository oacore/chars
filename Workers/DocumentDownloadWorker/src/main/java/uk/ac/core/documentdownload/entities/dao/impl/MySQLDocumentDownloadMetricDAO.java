package uk.ac.core.documentdownload.entities.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.documentdownload.entities.DocumentDownloadMetric;
import uk.ac.core.documentdownload.entities.dao.DocumentDownloadMetricDAO;

/**
 *
 * @author mc26486
 */
@Service
public class MySQLDocumentDownloadMetricDAO implements DocumentDownloadMetricDAO{

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public void save(DocumentDownloadMetric documentDownloadMetric) {
        jdbcTemplate.update("INSERT INTO document_download_metrics VALUES(?,?,?,?,?, NOW())",
                documentDownloadMetric.getRepositoryId(),
                documentDownloadMetric.getNumberOfPdfDownloaded(),
                documentDownloadMetric.getNumberOfDocumentsProcessed(),
                documentDownloadMetric.getNumberOfRequestsPerformed(),
                documentDownloadMetric.getEndTime() - documentDownloadMetric.getStartTime());
    }
    
}
