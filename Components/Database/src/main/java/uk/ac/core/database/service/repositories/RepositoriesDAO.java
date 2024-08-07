package uk.ac.core.database.service.repositories;

import uk.ac.core.common.model.legacy.LegacyRepository;
import java.util.List;

/**
 * No longer valid to perform operations on core repos,
 * replaced by {@literal RepositoryRepository} for {@literal DataProvider}.
 */
@Deprecated
public interface RepositoriesDAO {

    List<Integer> getNewRepositories();

    boolean repositoryExists(Integer repositoryId);

    boolean isRepositoryEnabled(Integer repositoryId);

    String getRepositoryName(Integer idRepository);

    LegacyRepository getRepositoryByOpenDOARId(String openDOARId);

    LegacyRepository getRepositoryByROARId(Integer id);

    LegacyRepository getRepositoryWithSimilarUri(String similarUri);

    void disableRepository(String id);

    LegacyRepository getRepositoryById(String repoId);

    <R extends LegacyRepository> R getRepositoryByIdAndType(String repoId, Class<R> r);

    void updateUri(String id, String newUri);

    LegacyRepository getRepositoryByBaseId(Integer id);

    List<Integer> getBigRepositories();
}
