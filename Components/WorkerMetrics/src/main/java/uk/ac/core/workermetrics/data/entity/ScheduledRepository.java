package uk.ac.core.workermetrics.data.entity;

import uk.ac.core.workermetrics.data.state.RepositoryPriority;
import uk.ac.core.workermetrics.data.state.ScheduledState;
import uk.ac.core.workermetrics.data.state.WatchedRepository;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Status of repository scheduling.
 */
@Entity
@Table(name = "scheduled_repository")
public class ScheduledRepository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_repository")
    private Integer idRepository;

    @Column(name = "scheduled_state", nullable = false)
    private ScheduledState scheduledState;

    @Column(name = "repository_priority", nullable = false)
    private RepositoryPriority repositoryPriority;

    @Column(name = "last_time_scheduled", nullable = false)
    private LocalDateTime lastScheduled;

    @Column(name = "prevent_harvest_until", nullable = false)
    private LocalDateTime until;

    @Transient
    private long schedulingScore;

    @Column(name = "watched_repository", nullable = false)
    private WatchedRepository watchedRepository;

    @Override
    public String toString() {
        return "ScheduledRepository{" + "idRepository=" + idRepository + ", scheduledState=" + scheduledState + ", repositoryPriority=" + repositoryPriority + ", lastScheduled=" + lastScheduled + ", schedulingScore=" + schedulingScore + '}';
    }
    
    public Integer getIdRepository() {
        return idRepository;
    }

    public void setIdRepository(Integer idRepository) {
        this.idRepository = idRepository;
    }

    public ScheduledState getScheduledState() {
        return scheduledState;
    }

    public void setScheduledState(ScheduledState scheduledState) {
        this.scheduledState = scheduledState;
    }

    public RepositoryPriority getRepositoryPriority() {
        return repositoryPriority;
    }

    public void setRepositoryPriority(RepositoryPriority repositoryPriority) {
        this.repositoryPriority = repositoryPriority;
    }

    public LocalDateTime getLastScheduled() {
        return lastScheduled;
    }

    public void setLastScheduled(LocalDateTime lastScheduled) {
        this.lastScheduled = lastScheduled;
    }

    public long getSchedulingScore() {
        return schedulingScore;
    }

    public void setSchedulingScore(long schedulingScore) {
        this.schedulingScore = schedulingScore;
    }

    public WatchedRepository getWatchedRepository() {
        return watchedRepository;
    }

    public void setWatchedRepository(WatchedRepository watchedRepository) {
        this.watchedRepository = watchedRepository;
    }

    public LocalDateTime getUntil() {
        return until;
    }

    public void setUntil(LocalDateTime until) {
        this.until = until;
    }
}