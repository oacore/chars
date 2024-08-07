package uk.ac.core.database.service.documetduplicates;

import uk.ac.core.database.model.WorksToDocumentDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface DocumentDuplicateDao {
    HashSet<Integer> findAllDuplicatesByDoi(String doi);

    String getDoiByDocumentId(Integer documentId);

    HashSet<Integer> getIdDocumentsByDOI(String doi, Integer repositoryId);

    void insertDocumentToExcludingTable(Integer excludedDocumentId, List<Integer> documentIds);

    boolean isDocumentExcluded(Integer documentId);

    List<Integer> getExclusionList(Integer documentId);

}
