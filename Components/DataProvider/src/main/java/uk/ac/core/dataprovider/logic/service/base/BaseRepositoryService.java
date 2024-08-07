package uk.ac.core.dataprovider.logic.service.base;

import uk.ac.core.dataprovider.logic.entity.BaseRepository;
import java.util.List;

/**
 * BASE Repositories Service.
 */
public interface BaseRepositoryService {

    /**
     * Saves BASE repositories.
     *
     * @param baseRepositoriesList base repositories list
     */
    void saveBaseRepositories(Iterable<BaseRepository> baseRepositoriesList);

    /**
     * Gets all BASE repositories.
     *
     * @return list of BASE repositories
     */
    List<BaseRepository> findAllBaseRepositories();
}