package uk.ac.core.dataprovider.logic.service.index.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.dataprovider.logic.entity.*;
import uk.ac.core.dataprovider.logic.repository.elasticsearch.IndexDataProviderRepository;
import uk.ac.core.dataprovider.logic.service.index.RepositoryIndexService;
import uk.ac.core.dataprovider.logic.repository.RepositoryScrollIndexService;

import java.util.List;
import java.util.function.Consumer;

@Service
public class RepositoryIndexServiceImpl implements RepositoryIndexService {

    private final IndexDataProviderRepository indexDataProviderRepository;
    private final RepositoryScrollIndexService repositoryScrollIndexService;

    @Autowired
    public RepositoryIndexServiceImpl(IndexDataProviderRepository indexDataProviderRepository, RepositoryScrollIndexService repositoryScrollIndexService) {
        this.indexDataProviderRepository = indexDataProviderRepository;
        this.repositoryScrollIndexService = repositoryScrollIndexService;
    }

    @Override
    public void indexRepository(DataProvider dataProvider, DataProviderLocation dataProviderLocation, DashboardRepo dashboardRepo, RorData rorData) {
        this.indexDataProviderRepository.save(new IndexedDataProvider(dataProvider, dataProviderLocation, dashboardRepo, rorData));
    }

    @Override
    public Iterable<IndexedDataProvider> saveAll(List<IndexedDataProvider> indexedDataProviders) {
        return indexDataProviderRepository.saveAll(indexedDataProviders);
    }

    @Override
    public int findAllViaConsumer(Consumer<IndexedDataProvider> consumer) {
        return repositoryScrollIndexService.findAllViaConsumer(consumer);
    }

    @Override
    public void deleteById(Long id) {
        indexDataProviderRepository.deleteById(id);
    }

    @Override
    public void deleteByIdIsNull()
    {
        repositoryScrollIndexService.deleteByIdIsNull();
    }

    @Override
    public void refresh() {
        repositoryScrollIndexService.refresh();
    }
}
