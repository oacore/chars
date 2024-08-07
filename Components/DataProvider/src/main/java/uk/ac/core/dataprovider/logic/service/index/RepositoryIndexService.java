package uk.ac.core.dataprovider.logic.service.index;

import uk.ac.core.dataprovider.logic.entity.*;

import java.util.List;
import java.util.function.Consumer;

/**
 * Retrieves all data about a repository and indexes it
 *
 * @author samuel
 */
public interface RepositoryIndexService {
    /**
     * Indexes a repository using Repository.getID. Note: This method only uses
     * repository.getID() to fetch the data from the database The data inside
     * Repository is not used
     *  @param dataProvider
     * @param dataProviderLocation
     * */
    void indexRepository(DataProvider dataProvider, DataProviderLocation dataProviderLocation, DashboardRepo dashboardRepo, RorData rorData);

    Iterable<IndexedDataProvider> saveAll(List<IndexedDataProvider> indexedDataProviders);

    int findAllViaConsumer(Consumer<IndexedDataProvider> consumer);

    void deleteById(Long id);

    void deleteByIdIsNull();

    void refresh();
}