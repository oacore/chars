package uk.ac.core.database.service.document.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.legacy.DocumentTdmStatus;
import uk.ac.core.database.service.document.DocumentTdmStatusDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Giorgio Basile
 * @since 16/06/2017
 */
@Service
public class MySQLDocumentTdmStatusDAO implements DocumentTdmStatusDAO {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MySQLDocumentTdmStatusDAO.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public DocumentTdmStatus getDocumentTdmStatus(Integer documentId) {

        String sql = "SELECT * FROM document_tdm_status WHERE id_document = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{documentId}, new DocumentTdmStatusMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void insertOrUpdateTdmStatus(DocumentTdmStatus documentTdmStatus) {
        this.jdbcTemplate.update(
                "INSERT INTO document_tdm_status "
                        + "(`id_document`, "
                        + "`tdm_only`, "
                        + "`fixed`) "
                        + "VALUES (?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE `id_document`= " + documentTdmStatus.getIdDocument() + ","
                        + "`tdm_only`="+ documentTdmStatus.getTdmOnly()+ ","
                        + "`fixed`=" + documentTdmStatus.getFixed() + ";",
                documentTdmStatus.getIdDocument(),
                documentTdmStatus.getTdmOnly(),
                documentTdmStatus.getFixed());
    }

    private static class DocumentTdmStatusMapper implements RowMapper<DocumentTdmStatus> {

        @Override
        public DocumentTdmStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
            DocumentTdmStatus documentTdmStatus = new DocumentTdmStatus();
            documentTdmStatus.setIdDocument(rs.getInt("id_document"));
            documentTdmStatus.setFixed(rs.getBoolean("fixed"));
            documentTdmStatus.setTdmOnly(rs.getBoolean("tdm_only"));
            documentTdmStatus.setLastUpdateTime(rs.getTimestamp("last_update_time"));
            return documentTdmStatus;
        }
    }
}

