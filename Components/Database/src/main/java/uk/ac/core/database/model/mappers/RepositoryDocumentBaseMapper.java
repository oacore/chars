
package uk.ac.core.database.model.mappers;

import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.common.model.article.PDFUrlSource;
import uk.ac.core.common.model.legacy.RepositoryDocumentBase;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Drahomira Herrmannova <d.herrmannova@gmail.com>
 */
public class RepositoryDocumentBaseMapper implements RowMapper<RepositoryDocumentBase> {

    /**
     * Maps one row of result set to an instance of RepositoryDocumentBase class.
     * @param rs
     * @param i
     * @return
     * @throws SQLException 
     */
    @Override
    public RepositoryDocumentBase mapRow(ResultSet rs, int i) throws SQLException {
        RepositoryDocumentBase id = new RepositoryDocumentBase();
        id.setIdDocument(rs.getInt("id_document"));
        id.setOai(rs.getString("oai"));
        if (rs.getString("url") != null && rs.getString("source") != null) {
            id.addUrl(rs.getString("url"), PDFUrlSource.valueOf(rs.getString("source")));
        }
        id.setPdfLastAttempt(rs.getDate("pdf_last_attempt"));
        id.setPdfStatus(rs.getBoolean("pdf_status"));
        id.setMetadataUpdated(rs.getDate("metadata_updated"));
        return id;
    }
    
}
