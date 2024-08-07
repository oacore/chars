package uk.ac.core.dataprovider.logic.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.core.dataprovider.logic.entity.BaseRepository;

/**
 * Repository for BASE repositories table.
 */
@Repository
public interface BaseRepositoryRepository extends JpaRepository<BaseRepository, Integer> {

}