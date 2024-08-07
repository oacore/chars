package uk.ac.core.extractmetadata.dataset.crossref.service;

import uk.ac.core.extractmetadata.dataset.crossref.exception.CrossrefDatasetLockException;

import java.io.File;
import java.io.IOException;

public interface CrossrefDatasetReader {
    String getNextEntryName() throws CrossrefDatasetLockException;

    File extractEntry(String name) throws IOException;

    void checkpoint(String entryName);

    void rereadCheckpoints();

    File setLock(String entryName) throws CrossrefDatasetLockException;

    void releaseLock(File lock) throws CrossrefDatasetLockException;
}
