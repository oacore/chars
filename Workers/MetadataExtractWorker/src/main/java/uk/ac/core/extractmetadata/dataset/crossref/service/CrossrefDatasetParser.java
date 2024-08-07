package uk.ac.core.extractmetadata.dataset.crossref.service;

import uk.ac.core.extractmetadata.dataset.crossref.exception.CrossrefDatasetLockException;

public interface CrossrefDatasetParser {
    boolean hasNextBatch() throws CrossrefDatasetLockException;

    void processBatch() throws CrossrefDatasetLockException;

    CrossrefDatasetReader getReader();
}
