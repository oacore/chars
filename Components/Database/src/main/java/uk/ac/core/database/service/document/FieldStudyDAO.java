package uk.ac.core.database.service.document;

import java.util.List;
import java.util.Optional;

public interface FieldStudyDAO {
    Optional<String> findFirstNormalizedNameByIdIn(List<Integer> docIds);
}