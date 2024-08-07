package uk.ac.core.dataprovider.logic.service.origin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.core.dataprovider.logic.dto.DataProviderBO;
import uk.ac.core.dataprovider.logic.dto.SyncResult;
import uk.ac.core.dataprovider.logic.entity.DataProvider;
import uk.ac.core.dataprovider.logic.exception.DataProviderDuplicateException;
import uk.ac.core.dataprovider.logic.exception.DataProviderNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Data Provider Service.
 */
public interface DataProviderService {

    /**
     * Copies all repositories in the database to the index, and removes all
     * items in the index which ARE NOT in the database
     *
     * @return A Map with keys repositoriesAddedToIndex and
     * repositoriesRemovedFromIndex
     */
    SyncResult syncAll();

    // TODO: Add JavaDoc for following methods
    void syncOne(Long id) throws DataProviderNotFoundException;

    DataProviderBO save(DataProviderBO dataProviderBO) throws DataProviderDuplicateException;

    List<Long> saveAll(List<DataProviderBO> dataProvidersBO);

    DataProviderBO update(DataProviderBO dataProviderBO);

    DataProviderBO findById(long id) throws DataProviderNotFoundException;

    List<DataProviderBO> findDuplicateRepositories(DataProviderBO dataProviderBO);

    int disableAllDuplicates() throws DataProviderNotFoundException;

    int disableDataProviderDuplicates(Long repositoryId) throws DataProviderNotFoundException;

        /**
         * Use {@link DataProviderService#findById(long)} instead.
         */
    @Deprecated
    DataProvider legacyFindById(long id);

    Optional<DataProvider> findOneByOpenDoarId(String openDOARId);

    List<DataProviderBO> findAllReposWithSimilarOaiPmhEndpoint(String uri);

    Page<DataProviderBO> findAll(Pageable page, boolean journal, boolean enabled);

    void delete(long id) throws DataProviderNotFoundException;
}
