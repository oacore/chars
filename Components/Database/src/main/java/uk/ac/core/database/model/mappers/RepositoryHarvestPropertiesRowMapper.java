/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.model.mappers;

import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.common.model.article.LicenseStrategy;
import uk.ac.core.common.model.legacy.AlgorithmType;
import uk.ac.core.common.model.legacy.HarvestLevel;
import uk.ac.core.common.model.legacy.RepositoryHarvestProperties;
import uk.ac.core.common.model.legacy.TryOnlyPdf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author mc26486
 */
public class RepositoryHarvestPropertiesRowMapper implements RowMapper<RepositoryHarvestProperties> {

    @Override
    public RepositoryHarvestProperties mapRow(ResultSet rs, int rowNum) throws SQLException {
        RepositoryHarvestProperties repositoryHarvestProperties = new RepositoryHarvestProperties();

        String contentTypesString = rs.getString("accepted_content_types");
        List<String> acceptedContentTypes;
        if (contentTypesString == null || contentTypesString.isEmpty()) {
            acceptedContentTypes = new ArrayList<>();
        } else {
            acceptedContentTypes = new ArrayList<>(Arrays.asList(contentTypesString.split(",")));
        }
        repositoryHarvestProperties.setAcceptedContentTypes(acceptedContentTypes);

        AlgorithmType algorithmType = AlgorithmType.fromDbFlag(rs.getInt("algorithm_type"));

        repositoryHarvestProperties.setAlgorithmType(algorithmType);
        String blackListString = rs.getString("black_list");
        String[] blackList;
        if (blackListString == null || blackListString.isEmpty()) {
            blackList = new String[0];
        } else {
            blackList = blackListString.split(",");
        }
        repositoryHarvestProperties.setBlackList(blackList);

        repositoryHarvestProperties.setDisabled(rs.getBoolean("disabled"));
        repositoryHarvestProperties.setHarvestHeuristic(rs.getString("harvest_heuristic"));
        HarvestLevel harvestLevel = HarvestLevel.fromInt(rs.getInt("harvest_level"));
        repositoryHarvestProperties.setHarvestLevel(harvestLevel);
        TryOnlyPdf tryOnlyPdf = TryOnlyPdf.fromInt(rs.getInt("try_only_pdf"));
        repositoryHarvestProperties.setTryOnlyPdf(tryOnlyPdf);
        repositoryHarvestProperties.setPrioritiseOldDocumentsForDownload(rs.getBoolean("prioritise_old_documents_for_download"));
        repositoryHarvestProperties.setSameDomainPolicy(rs.getBoolean("same_domain_policy"));
        repositoryHarvestProperties.setSkipAlreadyDownloaded(rs.getBoolean("skip_already_downloaded"));
        repositoryHarvestProperties.setRepositoryId(rs.getString("id_repository"));
        repositoryHarvestProperties.setTdmOnly(rs.getBoolean("tdm_only"));
        repositoryHarvestProperties.setUseSignpost(rs.getBoolean("signposting"));
        repositoryHarvestProperties.setPdfUrlSearchPattern(rs.getString("pdfurl_search_pattern"));
        repositoryHarvestProperties.setPdfUrlReplacePattern(rs.getString("pdfurl_replace_pattern"));

        repositoryHarvestProperties.setLicenseStrategy(
                LicenseStrategy.fromDbFlag(rs.getInt("license_strategy")));

        return repositoryHarvestProperties;

    }

}
