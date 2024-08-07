package uk.ac.core.eventscheduler.periodic;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.core.workermetrics.data.dao.scheduling.SchedulingRepositoryDAO;

@Component
public class SyncNewRepositories {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(SyncNewRepositories.class);

    final SchedulingRepositoryDAO schedulingRepositoryDAO;

    public SyncNewRepositories(SchedulingRepositoryDAO schedulingRepositoryDAO) {
        this.schedulingRepositoryDAO = schedulingRepositoryDAO;
    }

    @Scheduled(fixedRate = 1200000)
    public void run() {
        int affectedRows = schedulingRepositoryDAO.syncNewRepositories();
        logger.debug("Synced {} repositories in scheduled repositories table", affectedRows);
    }
}
