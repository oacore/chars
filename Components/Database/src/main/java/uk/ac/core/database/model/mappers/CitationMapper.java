package uk.ac.core.database.model.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.common.model.legacy.Citation;

/**
 *
 * @author lucasanastasiou
 */
public class CitationMapper implements RowMapper<Citation> {

    @Override
    public Citation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Citation citation = new Citation();
        citation.setIdCitation(rs.getInt("id_citation"));
        citation.setDocId(rs.getInt("id_cites"));
        if (rs.getString("id_cited") != null) {
            try {
                citation.setRefDocId(Integer.parseInt(rs.getString("id_cited")));
            } catch (NumberFormatException ex) {
            }
        }
        if (rs.getString("raw_string") != null) {
            citation.setRawString(rs.getString("raw_string"));
        }
        if (rs.getString("title") != null) {
            citation.setTitle(rs.getString("title"));
        }
        if (rs.getString("authors") != null) {
            citation.setAuthors(rs.getString("authors"));
        }
        if (rs.getString("date") != null) {
            citation.setDate(rs.getString("date"));
        }
        if (rs.getString("doi") != null) {
            citation.setDoi(rs.getString("doi"));
        }
        return citation;
    }

}
