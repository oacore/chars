/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.model.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.common.model.GrobidAffiliationInstitution;
/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
public class GrobidAffiliationInstitutionMapper implements RowMapper<GrobidAffiliationInstitution>{
    @Override
    public GrobidAffiliationInstitution mapRow(ResultSet rs, int rowNum) throws SQLException {
        GrobidAffiliationInstitution institution = new GrobidAffiliationInstitution();
        institution.setInstitutionId(rs.getInt("institution_id"));
        if (rs.getString("id_document") != null) {
            try {
                institution.setInstitutionId(Integer.parseInt(rs.getString("id_document")));
            } catch (NumberFormatException ex) {
            }
        }
        if (rs.getString("name") != null) {
            institution.setName(rs.getString("name"));
        }
        if (rs.getString("address") != null) {
            institution.setAddress(rs.getString("address"));
        }
        if (rs.getString("departments_str") != null) {
            institution.setGrobidAffiliationDepartmentsStr(rs.getString("departments_str"));
        }
        if (rs.getString("labs_str") != null) {
            institution.setGrobidAffiliationLabsStr(rs.getString("labs_str"));
        }
        return institution;
    }
}
