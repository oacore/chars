package uk.ac.core.database.service.repositories.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.legacy.RepositoryHarvestProperties;
import uk.ac.core.database.model.mappers.RepositoryHarvestPropertiesRowMapper;
import uk.ac.core.database.service.repositories.RepositoryDomainException;
import uk.ac.core.database.service.repositories.RepositoriesHarvestPropertiesDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author mc26486
 */
@Service
public class MySQLRepositoriesHarvestPropertiesDAO implements RepositoriesHarvestPropertiesDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void insertOrUpdate(RepositoryHarvestProperties repositoryHarvestProperties) {
        this.jdbcTemplate.update(
                "INSERT INTO repository_harvest_properties "
                        + "(`id_repository`, "
                        + "`harvest_level`, "
                        + "`try_only_pdf`, "
                        + "`disabled`, "
                        + "`same_domain_policy`, "
                        + "`algorithm_type`, "
                        + "`accepted_content_types`, "
                        + "`black_list`, "
                        + "`harvest_heuristic`, "
                        + "`skip_already_downloaded`, "
                        + "`prioritise_old_documents_for_download`, "
                        + "`tdm_only`,"
                        + "`pdfurl_search_pattern`, "
                        + "`pdfurl_replace_pattern`, "
                        + "`signposting`,"
                        + "`license_strategy`) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE id_repository=id_repository;",
                repositoryHarvestProperties.getRepositoryId(),
                repositoryHarvestProperties.getHarvestLevel().getLevel(),
                repositoryHarvestProperties.getTryOnlyPdf().getValue(),
                repositoryHarvestProperties.isDisabled(),
                repositoryHarvestProperties.isSameDomainPolicy(),
                1,
                String.join(",", repositoryHarvestProperties.getAcceptedContentTypes()),
                String.join(", ", repositoryHarvestProperties.getBlackList()),
                repositoryHarvestProperties.getHarvestHeuristic(),
                repositoryHarvestProperties.isSkipAlreadyDownloaded(),
                repositoryHarvestProperties.isPrioritiseOldDocumentsForDownload(),
                repositoryHarvestProperties.isTdmOnly(),
                repositoryHarvestProperties.getPdfUrlSearchPattern(),
                repositoryHarvestProperties.getPdfUrlReplacePattern(),
                repositoryHarvestProperties.isUseSignpost(),
                repositoryHarvestProperties.getLicenseStrategy());

    }

    @Override
    public RepositoryHarvestProperties load(Integer repositoryId) {
        String query = "SELECT * FROM repository_harvest_properties WHERE id_repository = ?";
        return this.jdbcTemplate.queryForObject(query, new Object[]{repositoryId}, new RepositoryHarvestPropertiesRowMapper());
    }

    @Override
    public List<RepositoryDomainException> getRepositoryDomainExceptions(String repositoryId) {
        String query = "SELECT * FROM `repository_exception_domains` WHERE id_repository = ?";
        return this.jdbcTemplate.query(query, new Object[]{repositoryId}, new RowMapper<RepositoryDomainException>() {
            @Override
            public RepositoryDomainException mapRow(ResultSet rs, int rowNum) throws SQLException {
                RepositoryDomainException rde = new RepositoryDomainException(
                        rs.getInt("id"),
                        rs.getInt("id_repository"),
                        rs.getString("url"));
                return rde;
            }
        });
    }

}
