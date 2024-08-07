package uk.ac.core.dataprovider.logic.service;

import org.springframework.stereotype.Service;
import uk.ac.core.dataprovider.logic.entity.DataProviderNote;
import uk.ac.core.dataprovider.logic.repository.DataProviderNoteRepository;

@Service
public class DataProviderNoteServiceImpl implements DataProviderNoteService {

    private final DataProviderNoteRepository dataProviderNoteRepository;

    public DataProviderNoteServiceImpl(DataProviderNoteRepository dataProviderNoteRepository) {
        this.dataProviderNoteRepository = dataProviderNoteRepository;
    }

    @Override
    public void insert(DataProviderNote dataProviderNote) {
        dataProviderNoteRepository.save(dataProviderNote);
    }
}
