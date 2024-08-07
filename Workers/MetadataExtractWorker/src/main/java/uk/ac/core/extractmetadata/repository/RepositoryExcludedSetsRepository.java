package uk.ac.core.extractmetadata.repository;

import java.util.Set;

public interface RepositoryExcludedSetsRepository {

    Set<String> getSetSpecsForExclude(Long repositoryId);
}
