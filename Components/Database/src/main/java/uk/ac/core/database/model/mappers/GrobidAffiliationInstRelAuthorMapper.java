/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.model.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.common.model.GrobidAffiliationInstitutionRelAuthor;

/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
public class GrobidAffiliationInstRelAuthorMapper implements RowMapper<GrobidAffiliationInstitutionRelAuthor>{
    @Override
    public GrobidAffiliationInstitutionRelAuthor mapRow(ResultSet rs, int rowNum) throws SQLException {
        GrobidAffiliationInstitutionRelAuthor relation = new GrobidAffiliationInstitutionRelAuthor();
        if (rs.getString("institution_id") != null) {
            relation.setInstitutionId(rs.getInt("institution_id"));
        }
        if (rs.getString("author_id") != null) {
            relation.setInstitutionId(rs.getInt("author_id"));
        }
        return relation;
    }
}
