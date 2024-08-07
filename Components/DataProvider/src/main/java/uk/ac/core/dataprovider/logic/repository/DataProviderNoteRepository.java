package uk.ac.core.dataprovider.logic.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.ac.core.dataprovider.logic.entity.DataProviderNote;

public interface DataProviderNoteRepository extends CrudRepository<DataProviderNote, Long> {

}