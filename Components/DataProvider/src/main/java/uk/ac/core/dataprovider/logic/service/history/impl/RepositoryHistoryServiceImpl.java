package uk.ac.core.dataprovider.logic.service.history.impl;

import org.springframework.stereotype.Service;
import uk.ac.core.dataprovider.logic.repository.HistoryRepositoryRepository;
import uk.ac.core.dataprovider.logic.entity.RepositoryHistory;
import uk.ac.core.dataprovider.logic.service.history.RepositoryHistoryService;
import java.util.List;

@Service
public class RepositoryHistoryServiceImpl implements RepositoryHistoryService {

    private final HistoryRepositoryRepository historyRepositoryRepository;

    public RepositoryHistoryServiceImpl(HistoryRepositoryRepository historyRepositoryRepository) {
        this.historyRepositoryRepository = historyRepositoryRepository;
    }

    @Override
    public List<RepositoryHistory> getHistoryRepositories() {
        return historyRepositoryRepository.findAll();
    }

    @Override
    public RepositoryHistory save(RepositoryHistory repositoryHistory){
       return historyRepositoryRepository.save(repositoryHistory);
    }

    @Override
    public RepositoryHistory save(long repositoryId, String historicUrl) {
        RepositoryHistory repositoryHistory = new RepositoryHistory();
        repositoryHistory.setRepositoryId(repositoryId);
        repositoryHistory.setHistoricUrl(historicUrl);
        return save(repositoryHistory);
    }
}