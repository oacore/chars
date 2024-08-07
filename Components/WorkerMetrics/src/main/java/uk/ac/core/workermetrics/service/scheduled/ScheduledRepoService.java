package uk.ac.core.workermetrics.service.scheduled;

import uk.ac.core.workermetrics.data.state.ScheduledState;

/**
 * Scheduled Repo Service.
 */
public interface ScheduledRepoService {

    long countAllByScheduledState(ScheduledState scheduledState);
}