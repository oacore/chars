/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.metadatadownloadworker.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.metadatadownloadworker.model.RepositoryMetadataDownloadAllowedSets;
import uk.ac.core.metadatadownloadworker.repository.RepositoryMetadataDownloadAllowedSetsListRepository;

/**
 *
 * @author samuel
 */
@Service
public class RepositoryMetadataDownloadAllowedSetsListService {

    @Autowired
    private RepositoryMetadataDownloadAllowedSetsListRepository repositoryMetadataDownloadAllowedSetsListRepository;

    
    public List<RepositoryMetadataDownloadAllowedSets> findByRepositoryId(Long repositoryId) {
        return repositoryMetadataDownloadAllowedSetsListRepository.getByRepositoryId(repositoryId);
    }
}
