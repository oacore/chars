package uk.ac.core.dataprovider.logic.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.core.dataprovider.logic.entity.DashboardRepo;
import uk.ac.core.dataprovider.logic.entity.RorData;


@Repository
public interface RorDataRepository extends JpaRepository<RorData, Long> {

}