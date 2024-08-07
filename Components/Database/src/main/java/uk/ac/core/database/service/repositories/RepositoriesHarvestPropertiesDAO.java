package uk.ac.core.database.service.repositories;

import java.util.List;

import uk.ac.core.common.model.legacy.RepositoryHarvestProperties;

/**
 * @author mc26486
 */
public interface RepositoriesHarvestPropertiesDAO {

    RepositoryHarvestProperties load(Integer repositoryId);

    void insertOrUpdate(RepositoryHarvestProperties repositoryHarvestProperties);

    List<RepositoryDomainException> getRepositoryDomainExceptions(String repositoryId);

}
