package uk.ac.core.dataprovider.logic.service.history;

import uk.ac.core.dataprovider.logic.entity.RepositoryHistory;
import java.util.List;

/**
 * History Repository Service.
 */
public interface RepositoryHistoryService {

    List<RepositoryHistory> getHistoryRepositories();

    RepositoryHistory save(RepositoryHistory repositoryHistory);

    RepositoryHistory save(long repositoryId, String historicUrl);
}
