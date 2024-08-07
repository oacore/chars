package uk.ac.core.database.model.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.common.model.GrobidCitationAuthor;

/**
 *
 * @author vb4826
 */
public class GrobidCitationAuthorMapper implements RowMapper<GrobidCitationAuthor> {
    @Override
    public GrobidCitationAuthor mapRow(ResultSet rs, int rowNum) throws SQLException {
        GrobidCitationAuthor author = new GrobidCitationAuthor();
        author.setAuthorId(rs.getInt("author_id"));
        if (rs.getString("forename_first") != null) {
            author.setForenameFirst(rs.getString("forename_first"));
        }
        if (rs.getString("forename_middle") != null) {
            author.setForenameMiddle(rs.getString("forename_middle"));
        }
        if (rs.getString("surname") != null) {
            author.setSurname(rs.getString("surname"));
        }
        return author;
    }
}
