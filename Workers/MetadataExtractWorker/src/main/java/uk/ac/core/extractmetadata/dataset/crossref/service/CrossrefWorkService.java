package uk.ac.core.extractmetadata.dataset.crossref.service;

import uk.ac.core.crossref.json.CrossRefDocument;

import java.util.Optional;

public interface CrossrefWorkService {
    Optional<Integer> matchCrossrefWork(CrossRefDocument work);

    void updateExistingCoreRecord(Integer coreId, CrossRefDocument work);

    void addNewCoreRecord(CrossRefDocument work);
}
