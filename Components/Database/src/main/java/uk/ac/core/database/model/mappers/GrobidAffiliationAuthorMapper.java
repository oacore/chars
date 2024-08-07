/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.model.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.common.model.GrobidAffiliationAuthor;
/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
public class GrobidAffiliationAuthorMapper implements RowMapper<GrobidAffiliationAuthor> {
    @Override
    public GrobidAffiliationAuthor mapRow(ResultSet rs, int rowNum) throws SQLException {
        GrobidAffiliationAuthor author = new GrobidAffiliationAuthor();
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
        if (rs.getString("contact") != null) {
            author.setContact(rs.getString("contact"));
        }
        if (rs.getString("rolename") != null) {
            author.setRolename(rs.getString("rolename"));
        }
        return author;
    }
}
