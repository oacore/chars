package uk.ac.core.dataprovider.logic.service.base.impl;

import org.springframework.stereotype.Service;
import uk.ac.core.dataprovider.logic.repository.BaseRepositoryRepository;
import uk.ac.core.dataprovider.logic.entity.BaseRepository;
import uk.ac.core.dataprovider.logic.service.base.BaseRepositoryService;
import java.util.List;

@Service
public class BaseRepositoryServiceImpl implements BaseRepositoryService {

    private final BaseRepositoryRepository baseRepositoryRepository;

    public BaseRepositoryServiceImpl(BaseRepositoryRepository baseRepositoryRepository) {
        this.baseRepositoryRepository = baseRepositoryRepository;
    }

    @Override
    public void saveBaseRepositories(Iterable<BaseRepository> baseRepositoriesList) {
        baseRepositoryRepository.saveAll(baseRepositoriesList);
    }

    @Override
    public List<BaseRepository> findAllBaseRepositories() {
        return baseRepositoryRepository.findAll();
    }
}