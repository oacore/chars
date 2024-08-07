package uk.ac.core.database.service.repositories.impl;

/**
 *
 * @author lucasanastasiou
 */
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.database.service.repositories.RepositoryMetadataFormatsDAO;

@Service
public class MetadataFormatsDAO implements RepositoryMetadataFormatsDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MetadataFormatsDAO.class);

    @Override
    public void insertMetadataFormat(String prefix, String schema, String namespace) {
        prefix = nullToEmpty(prefix);
        schema = nullToEmpty(schema);
        namespace = nullToEmpty(namespace);
        String sql = "INSERT INTO `metadata_formats` (`md_prefix`, `md_schema`, `md_namespace`) VALUES (?,?,?) ON DUPLICATE KEY UPDATE md_prefix=md_prefix";
        
        this.jdbcTemplate.update(sql, new Object[]{prefix, schema, namespace});
    }

    private String nullToEmpty(String input) {
        return (input == null) ? "" : input;
    }

    @Override
    public void insertOrUpdateRepositoryMetadataFormat(Integer repositoryId, String metadataFormatPrefix) {
        if (!exists(repositoryId, metadataFormatPrefix)) {
            this.insertRepositoryMetadataFormat(repositoryId, metadataFormatPrefix);
        }
    }
    
    private void insertRepositoryMetadataFormat(Integer repositoryId, String metadataFormatPrefix) {
        String sql = "INSERT INTO `repository_metadata_formats` (`id_repository`, `format`) VALUES (?,?)";
        this.jdbcTemplate.update(sql, new Object[]{repositoryId, metadataFormatPrefix});
    }
    
    private boolean exists(Integer repositoryId, String metadataFormatPrefix) {
        String sql = "SELECT count(id_repository) FROM `repository_metadata_formats` WHERE `id_repository` = ? AND `format` = ?";
        Integer result = this.jdbcTemplate.queryForObject(sql, Integer.class, new Object[]{repositoryId, metadataFormatPrefix });
        return result != null && result > 0;
    }
    
    @Override
    public void removeAllMetadataFormats(Integer repositoryId) {
        String sql = "DELETE FROM repository_metadata_formats WHERE id_repository = ?";
        this.jdbcTemplate.update(sql, new Object[]{repositoryId});
    }
    
    
}
