/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.service.repositories.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import uk.ac.core.common.model.legacy.LegacyRepositoryLocation;

/**
 *
 * @author mc26486
 */
public class RepositoryLocationRowMapper implements ResultSetExtractor<LegacyRepositoryLocation> {

    public RepositoryLocationRowMapper() {
    }

    @Override
    public LegacyRepositoryLocation extractData(ResultSet rs) throws SQLException, DataAccessException {
        LegacyRepositoryLocation repositoryLocation = new LegacyRepositoryLocation();
        repositoryLocation.setRepositoryId(rs.getInt("id_repository"));
        repositoryLocation.setCountry(rs.getString("country_code"));
        repositoryLocation.setLatitude(rs.getString("latitude"));
        repositoryLocation.setLongitude(rs.getString("longitude"));
        return repositoryLocation;
    }
    
}
