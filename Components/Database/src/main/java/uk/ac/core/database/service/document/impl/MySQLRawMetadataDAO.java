package uk.ac.core.database.service.document.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.legacy.DocumentRawMetadata;
import uk.ac.core.database.service.document.RawMetadataDAO;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class MySQLRawMetadataDAO implements RawMetadataDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public DocumentRawMetadata getDocumentRawMetadataByCoreID(final Integer articleId) {
        String SQL = "SELECT id,id_document,datetime,metadata FROM document_raw_metadata "
                + "WHERE id_document = ? "
                + "ORDER BY datetime DESC LIMIT 0,1";
        PreparedStatementSetter preparedStatementSetter = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, articleId);
            }
        };

        RowMapper<DocumentRawMetadata> rowMapper = new DocumentRawMetadataMapper();

        List<DocumentRawMetadata> results = jdbcTemplate.query(SQL, preparedStatementSetter, rowMapper);
        if (results.isEmpty()){
            return null;
        }else {
            return results.get(0);
        }
    }

    @Override
    public DocumentRawMetadata getDocumentRawMetadataByDrmId(Integer drmId) {
        String sql = "" +
                "select * " +
                "from document_raw_metadata drm " +
                "where drm.id = ?";
        PreparedStatementSetter setter = preparedStatement -> preparedStatement.setInt(1, drmId);
        RowMapper<DocumentRawMetadata> mapper = new DocumentRawMetadataMapper();
        List<DocumentRawMetadata> results = jdbcTemplate.query(sql, setter, mapper);
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    private static class DocumentRawMetadataMapper implements RowMapper<DocumentRawMetadata> {

        @Override
        public DocumentRawMetadata mapRow(ResultSet rs, int rowNum) throws SQLException {
            DocumentRawMetadata documentRawMetadata = new DocumentRawMetadata();
            documentRawMetadata.setId(rs.getInt("id"));
            documentRawMetadata.setIdDocument(rs.getInt("id_document"));
            documentRawMetadata.setDatetime(rs.getTimestamp("datetime"));
            documentRawMetadata.setMetadata(rs.getString("metadata"));
            return documentRawMetadata;
        }
    }

}
