package uk.ac.core.dataprovider.logic.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.core.dataprovider.logic.entity.BaseRepository;
import uk.ac.core.dataprovider.logic.entity.DashboardRepo;


@Repository
public interface DashboardRepoRepository extends JpaRepository<DashboardRepo, Long> {

}