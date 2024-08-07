package uk.ac.core.database.service.repositorySourceStatistics.impl;

import java.sql.ResultSet;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.database.model.RepositorySourceStatistics;
import uk.ac.core.database.service.repositorySourceStatistics.RepositorySourceStatisticsDAO;

@Service
public class MySQLRepositorySourceStatistics implements RepositorySourceStatisticsDAO {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MySQLRepositorySourceStatistics.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean save(RepositorySourceStatistics repositorySourceStatistics) {
        Optional<RepositorySourceStatistics> attr = this.get(repositorySourceStatistics.getIdRepository());
        if (attr.isPresent()) {
            String sql = "UPDATE repository_source_statistics "
                    + "SET "
                    + "metadata_count=?,"
                    + "metadata_non_deleted_count=?,"
                    + "metadata_with_attachments_count=?,"
                    + "fulltext_count=? "
                    + "WHERE "
                    + "id_repository = ?";
            return jdbcTemplate.update(sql,
                    repositorySourceStatistics.getMetadataCount(),
                    repositorySourceStatistics.getMetadataNonDeletedCount(),
                    repositorySourceStatistics.getMetadataWithAttachmentCount(),
                    repositorySourceStatistics.getFulltextCount(),
                    repositorySourceStatistics.getIdRepository()
                    ) > 0;
        } else {
            String sql = "INSERT INTO repository_source_statistics "
                    + "(id_repository, metadata_count, metadata_non_deleted_count, metadata_with_attachments_count, fulltext_count) "
                    + "VALUES (?,?,?,?,?) ";
            return jdbcTemplate.update(sql,
                    repositorySourceStatistics.getIdRepository(),
                    repositorySourceStatistics.getMetadataCount(),
                    repositorySourceStatistics.getMetadataNonDeletedCount(),
                    repositorySourceStatistics.getMetadataWithAttachmentCount(),
                    repositorySourceStatistics.getFulltextCount()
            ) > 0;
        }
    }

    @Override
    public Optional<RepositorySourceStatistics> get(int idDocument) {
        final String sql = "SELECT * FROM repository_source_statistics WHERE id_repository = ?";
        try {
            return Optional.of(this.jdbcTemplate.queryForObject(sql, new Object[]{idDocument}, (ResultSet rs, int rowNum) -> {
                RepositorySourceStatistics stats = new RepositorySourceStatistics(idDocument);
                stats.setMetadataCount(rs.getInt("metadata_count"));
                stats.setMetadataNonDeletedCount(rs.getInt("metadata_non_deleted_count"));
                stats.setMetadataWithAttachmentCount(rs.getInt("metadata_with_attachments_count"));
                stats.setFulltextCount(rs.getInt("fulltext_count"));
                return stats;
            }));
        } catch (EmptyResultDataAccessException ex) {
            // gobble exception
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public void setRepositorySourceStatistics(int id_repository, Integer metadataCount, Integer metadataNonDeletedCount, Integer metadataWithAttachmentsCount, Integer fulltextCount) {
        RepositorySourceStatistics stats = new RepositorySourceStatistics(id_repository);
        stats.setMetadataCount(metadataCount);
        stats.setMetadataNonDeletedCount(metadataNonDeletedCount);
        stats.setMetadataWithAttachmentCount(metadataWithAttachmentsCount);
        stats.setFulltextCount(fulltextCount);
        this.save(stats);
    }

    @Override
    public void setFulltextCount(int id_repository, int fulltextCount) {
        this.jdbcTemplate.update(
                "UPDATE repository_source_statistics SET fulltext_count = ? WHERE id_repository = ?",
                fulltextCount,
                id_repository
        );
    }

    @Override
    public void setMetadataWithAttachmentsCount(int id_repository, int metadataWithAttachmentsCount) {
        this.jdbcTemplate.update(
                "UPDATE repository_source_statistics SET metadata_with_attachments_count = ? WHERE id_repository = ?",
                metadataWithAttachmentsCount,
                id_repository
        );
    }
}
