package uk.ac.core.workermetrics.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.core.workermetrics.data.entity.ScheduledRepository;
import uk.ac.core.workermetrics.data.state.ScheduledState;

/**
 * Scheduled repository.
 * <p>
 * All new data manipulation operations on scheduled repos should be added here.
 */
public interface ScheduledRepoRepository extends JpaRepository<ScheduledRepository, Integer> {

    long countAllByScheduledState(ScheduledState scheduledState);
}