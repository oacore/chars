package uk.ac.core.metadatadownloadworker.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.metadatadownloadworker.model.RepositoryMetadataDownloadAllowedSets;

/**
 *
 * @author samuel
 */
public class RepositoryMetadataDownloadAllowedSetsMapper implements RowMapper<RepositoryMetadataDownloadAllowedSets> {

    @Override
    public RepositoryMetadataDownloadAllowedSets mapRow(ResultSet rs, int rowNum) throws SQLException {

        RepositoryMetadataDownloadAllowedSets set = new RepositoryMetadataDownloadAllowedSets(
                rs.getInt("id_repository"), 
                rs.getString("set_spec"), 
                rs.getString("set_name"));
        set.setId(rs.getLong("id"));
               
        return set;

    }
}
