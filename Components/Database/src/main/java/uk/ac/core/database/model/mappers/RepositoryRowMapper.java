/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.model.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.common.model.legacy.LegacyRepository;

/**
 *
 * @author mc26486
 * @param <R>
 */
public class RepositoryRowMapper<R extends LegacyRepository> implements RowMapper<R> {

    public Class<R> REPOCLASS;

    public RepositoryRowMapper() {
         this.REPOCLASS = (Class<R>) LegacyRepository.class;
    }

    public RepositoryRowMapper(Class<R> repoClass) {
        this.REPOCLASS = repoClass;
    }

    @Override
    public R mapRow(ResultSet rs, int rowNum) throws SQLException {
        R repository = null;
        try {
            repository = REPOCLASS.newInstance();
            repository.setId(rs.getString("id_repository"));
            repository.setOpenDoarId(rs.getInt("id_opendoar"));
            repository.setName(rs.getString("name"));
            repository.setUrlOaipmh(rs.getString("urlOaipmh"));
            repository.setUrlHomepage(rs.getString("urlHomepage"));
            repository.setUriJournals(rs.getString("uri_journals"));
            repository.setSource(rs.getString("source"));
            repository.setPhysicalName(rs.getString("physical_name"));
            repository.setDisabled(((rs.getInt("disabled") == 1)));
            repository.setSoftware(rs.getString("software"));
            repository.setMetadataFormat(rs.getString("metadata_format"));
            repository.setDescription(rs.getString("description"));
            repository.setDescription(rs.getString("description"));
            repository.setRoarId(rs.getInt("id_roar"));
            repository.setRoarId(rs.getInt("id_base"));
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(RepositoryRowMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return repository;
    }

}
