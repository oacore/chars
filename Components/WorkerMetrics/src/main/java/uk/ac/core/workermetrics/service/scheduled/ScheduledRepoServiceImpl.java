package uk.ac.core.workermetrics.service.scheduled;

import org.springframework.stereotype.Service;
import uk.ac.core.workermetrics.data.repo.ScheduledRepoRepository;
import uk.ac.core.workermetrics.data.state.ScheduledState;

@Service
public class ScheduledRepoServiceImpl implements ScheduledRepoService {

    private final ScheduledRepoRepository scheduledRepoRepository;

    public ScheduledRepoServiceImpl(ScheduledRepoRepository scheduledRepoRepository) {
        this.scheduledRepoRepository = scheduledRepoRepository;
    }

    @Override
    public long countAllByScheduledState(ScheduledState scheduledState) {
        return scheduledRepoRepository.countAllByScheduledState(scheduledState);
    }
}