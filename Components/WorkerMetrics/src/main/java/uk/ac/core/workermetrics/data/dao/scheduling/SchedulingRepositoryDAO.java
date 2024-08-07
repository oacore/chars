package uk.ac.core.workermetrics.data.dao.scheduling;

import uk.ac.core.workermetrics.data.entity.ScheduledRepository;
import uk.ac.core.workermetrics.data.state.RepositoryPriority;
import uk.ac.core.workermetrics.data.state.ScheduledState;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author lucasanastasiou
 */
public interface SchedulingRepositoryDAO {

    public void insertSchedulingRepository(Integer idRepository, ScheduledState scheduledState, RepositoryPriority repositoryPriority, LocalDateTime lastScheduled);

    public void insertDefaultSchedulingRepositoryEntry(Integer idRepository, ScheduledState scheduledState, RepositoryPriority repositoryPriority);

    public List<ScheduledRepository> getAllOrderedBySchedulingScore();

    public void updateTimeLastScheduledToNow(ScheduledRepository scheduledRepository);
    
    public void updatePreventReharvest(ScheduledRepository scheduledRepository);

    public void updateStatus(ScheduledRepository scheduledRepository, ScheduledState scheduledState);

    public List<ScheduledRepository> getOngoingOrderedBySchedulingScore();

    public List<ScheduledRepository> getNextInLineOrderedBySchedulingScore();

    public List<ScheduledRepository> getNextInLineOrderedBySchedulingScore(int howMany);

    public ScheduledRepository getScheduledRepositoryByRepositoryId(Integer repositoryId);

    public void updateWatchedStatus(int idRepository, int watchedRepositoryFlag);

    public List<ScheduledRepository> getNonPendingRepositories();

    public List<Integer> getUnscheduledRepositories();

    List<Integer> getPremiumRepositoriesOlderThan3Days();

    /**
     * Updates ScheduledRepository to contain newly added repositories
     * @return count of effected records 
     */
    int syncNewRepositories();

    void updatePriority(int repoId, RepositoryPriority priority);
}
