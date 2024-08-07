/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.metadatadownloadworker.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.metadatadownloadworker.model.RepositoryMetadataDownloadAllowedSets;

/**
 *
 * @author samuel
 */
@Service
public class RepositoryMetadataDownloadAllowedSetsListRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<RepositoryMetadataDownloadAllowedSets> getByRepositoryId(Long repositoryId) {
        String SQL = "SELECT * FROM repository_metadata_download_allowed_sets WHERE id_repository = ?";
        List<RepositoryMetadataDownloadAllowedSets> list = new ArrayList<>();
        try {
            list = jdbcTemplate.query(SQL, new RepositoryMetadataDownloadAllowedSetsMapper(), repositoryId);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            list = new ArrayList<>();
        }
        return list;
    }
}
