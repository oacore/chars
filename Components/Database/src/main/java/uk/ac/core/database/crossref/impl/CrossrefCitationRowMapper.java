/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.crossref.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author samuel
 */
public class CrossrefCitationRowMapper implements RowMapper<CrossrefCitationForDocumentId> {

    @Override
    public CrossrefCitationForDocumentId mapRow(ResultSet rs, int i) throws SQLException {
        return new CrossrefCitationForDocumentId(
                rs.getInt("id"),
                rs.getInt("id_document"),
                rs.getString("query_string"),
                rs.getString("doi"),
                rs.getString("coins"),
                rs.getDouble("score")
        );
    }
}
