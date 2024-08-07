package uk.ac.core.dataprovider.logic.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.core.dataprovider.logic.entity.RepositoryHistory;

/**
 * Repository for history repository table.
 */
@Repository
public interface HistoryRepositoryRepository extends JpaRepository<RepositoryHistory, Integer> {

}