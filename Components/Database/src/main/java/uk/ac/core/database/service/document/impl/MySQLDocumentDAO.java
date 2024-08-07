package uk.ac.core.database.service.document.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.legacy.PreviewStatus;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.database.service.document.DocumentDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lucasanastasiou
 */
@Service
public class MySQLDocumentDAO implements DocumentDAO {

    private final JdbcTemplate jdbcTemplate;

    private final Logger logger = LoggerFactory.getLogger(MySQLDocumentDAO.class);

    public MySQLDocumentDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean setPreviewStatus(Integer documentId, PreviewStatus previewStatus) {
//        logger.info("Updating previewStatus document ID: {} Preview Status: {}", new Object[]{documentId, previewStatus.getMask()});
        return (this.jdbcTemplate.update(
                "UPDATE document set preview_status = ?, preview_datetime_generated = NOW() where id_document = ?",
                previewStatus.getMask(), documentId) > 0);
    }

    @Override
    public Boolean getPreviewStatus(String documentId) {
        Integer previewStatus = this.jdbcTemplate.queryForObject("SELECT preview_status FROM document WHERE id_document=?", new Object[]{documentId}, Integer.class);
        return previewStatus == 1;
    }

    @Override
    public Integer getDocumentCount() {
        String SQL = "SELECT MAX(id_document) FROM document";
        return jdbcTemplate.queryForObject(SQL, Integer.class);
    }

    @Override
    public Long countIndexedDocsSince(LocalDate date) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM document d WHERE d.index_last_attempt_successful > ?", new Object[]{date}, Long.class);
    }

    @Override
    public Long getPreviewCount() {
        this.jdbcTemplate.setQueryTimeout(600);
        String SQL = "SELECT COUNT(id_document) FROM document WHERE preview_status=1";
        Long count = 0L;
        try {
            count = this.jdbcTemplate.queryForObject(SQL, Long.class);
        } catch (QueryTimeoutException e) {
            logger.info("Preview cancelled because of timeout, we don't want to hit too much the db");
        } finally {
            this.jdbcTemplate.setQueryTimeout(-1);
        }

        return count;
    }

    @Override
    public List<RepositoryDocument> getArticlesByOaiAndRepo(String oaiSuffix, Long repositoryId) {
        String SQL = "SELECT id_document, deleted, pdf_status "
                + "FROM document "
                + "WHERE id_repository = ? "
                + "AND oai LIKE '%:" + oaiSuffix + "'";

        List<RepositoryDocument> results = new ArrayList<>();

        // workaround of not adding another where clause which makes performance very very very slow
        jdbcTemplate.query(SQL, new Object[]{repositoryId}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                if (rs.getInt("deleted") == 0) {
                    RepositoryDocument rd = new RepositoryDocument();
                    rd.setIdDocument(rs.getInt("id_document"));
                    rd.setPdfStatus(rs.getInt("pdf_status"));
                    results.add(rd);
                }
            }
        });
        return results;
    }

    @Override
    public String getArticleDoiById(Integer documentId) {

        String SQL = "SELECT doi FROM document_metadata where id_document = ?";

        try {

            return this.jdbcTemplate.queryForObject(SQL, new Object[]{documentId}, String.class);

        } catch (EmptyResultDataAccessException ex) {
            return null;
        }

    }

    @Override
    public void updateIndexedFieldForNotIndexedDocument(Integer documentId) {
        String sql = "UPDATE document set indexed = 0 where id_document = ? ;";
        jdbcTemplate.update(sql, documentId);
        logger.info("Indexed status for document {} was updated to 0", documentId );
    }

}
