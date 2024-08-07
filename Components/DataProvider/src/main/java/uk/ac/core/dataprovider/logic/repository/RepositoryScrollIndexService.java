package uk.ac.core.dataprovider.logic.repository;

import uk.ac.core.dataprovider.logic.entity.IndexedDataProvider;

import java.util.function.Consumer;

public interface RepositoryScrollIndexService {

    int findAllViaConsumer(Consumer<IndexedDataProvider> consumer);

    void deleteByIdIsNull();

    void refresh();
}
