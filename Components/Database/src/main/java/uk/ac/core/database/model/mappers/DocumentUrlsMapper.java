package uk.ac.core.database.model.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.common.model.article.PDFUrlSource;
import uk.ac.core.common.model.legacy.DocumentUrl;


/**
 *
 * @author mc26486
 */
public class DocumentUrlsMapper implements RowMapper<DocumentUrl> {

    /**
     * Maps one row of result set to an instance of RepositoryHarvestProperties
     * class.
     *
     * @param rs
     * @param i
     * @return
     * @throws SQLException
     */
    @Override
    public DocumentUrl mapRow(ResultSet rs, int i) throws SQLException {
        DocumentUrl documentUrl = new DocumentUrl(
                rs.getInt("id"),
                rs.getInt("id_document"),
                rs.getString("url"),
                rs.getInt("pdf_status"),
                PDFUrlSource.valueOf(rs.getString("source")));
        return documentUrl;
    }

    // jdbctemplate does not natively map multiple rows to a List<objects> 
    public List<DocumentUrl> mapToListOfObjects(List<Map<String, Object>> map) {
        List<DocumentUrl> documentUrls = new ArrayList<>();
        for (Map<String, Object> documentUrlMap : map) {
            Integer pdfStatus = (documentUrlMap.get("pdf_status") == null) ? 0 : (int) documentUrlMap.get("pdf_status");
            
            DocumentUrl documentUrl = new DocumentUrl(
                    (int) documentUrlMap.get("id"),
                    (int) documentUrlMap.get("id_document"),
                    (String) documentUrlMap.get("url"),
                    pdfStatus,
                    PDFUrlSource.valueOf((String)documentUrlMap.get("source")));
            documentUrls.add(documentUrl);
        }
        return documentUrls;
    }
}