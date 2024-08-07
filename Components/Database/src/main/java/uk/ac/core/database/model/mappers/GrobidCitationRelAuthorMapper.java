package uk.ac.core.database.model.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.common.model.GrobidCitationAuthor;
import uk.ac.core.common.model.GrobidCitationRelAuthor;

/**
 *
 * @author vb4826
 */
public class GrobidCitationRelAuthorMapper implements RowMapper<GrobidCitationRelAuthor> {
    @Override
    public GrobidCitationRelAuthor mapRow(ResultSet rs, int rowNum) throws SQLException {
        GrobidCitationRelAuthor relation = new GrobidCitationRelAuthor();
        relation.setAuthorId(rs.getInt("relation_id"));
        if (rs.getString("citation_id") != null) {
            relation.setCitationId(rs.getInt("citation_id"));
        }
        if (rs.getString("author_id") != null) {
            relation.setAuthorId(rs.getInt("author_id"));
        }
        return relation;
    }
}
