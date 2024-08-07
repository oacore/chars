package uk.ac.core.dataprovider.logic.repository.dataprovider;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.ac.core.dataprovider.logic.entity.DataProvider;
import java.util.List;
import java.util.Optional;

@Repository
public interface DataProviderRepository extends JpaRepository<DataProvider, Long> {

    List<DataProvider> findByNameIgnoreCase(String name);

    Optional<DataProvider> findByOpenDoarId(Long id);

    Page<DataProvider> findByDisabledAndJournal(Boolean disabled, Boolean journal, Pageable pageable);

    List<DataProvider> findByUrlOaipmhContainingOrderById(String oaiPmhUrlPart);

    List<DataProvider> findByUrlOaipmhIgnoreCase(String oaiPmhEndpoint);

//    @Modifying
//    @Query("update DataProvider d set d.disabled = true where d.id = :id")
//    void disableRepository(@Param(value = "id") Long repositoryId);

    @Query(value = "SELECT id_repository FROM repository ORDER BY id_repository ASC", nativeQuery = true)
    List<Long> findIds();


    @Query("SELECT d.disabled FROM DataProvider d WHERE d.id = :id")
    Boolean isDisabled(@Param(value = "id") Long repositoryId);
}
