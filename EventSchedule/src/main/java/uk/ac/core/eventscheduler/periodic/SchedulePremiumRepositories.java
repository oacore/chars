package uk.ac.core.eventscheduler.periodic;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.supervisor.client.SupervisorClient;
import uk.ac.core.workermetrics.data.dao.scheduling.impl.MySqlSchedulingRepositoryDAO;

import java.util.List;

/**
 *
 * @author Samuel Pearce
 */
@Component
public class SchedulePremiumRepositories {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(SchedulePremiumRepositories.class);

    final MySqlSchedulingRepositoryDAO mySqlSchedulingRepositoryDAO;

    final SupervisorClient supervisorClient;

    public SchedulePremiumRepositories(MySqlSchedulingRepositoryDAO mySqlSchedulingRepositoryDAO, SupervisorClient supervisorClient) {
        this.mySqlSchedulingRepositoryDAO = mySqlSchedulingRepositoryDAO;
        this.supervisorClient = supervisorClient;
    }

    @Scheduled(cron = "0 31 4 ? * MON,THU")
    public void schedulePremiumRepositories() {
        List<Integer> premiumRepositoryIds = mySqlSchedulingRepositoryDAO.getPremiumRepositoriesOlderThan3Days();

        for (int repoId : premiumRepositoryIds) {
            logger.info("Scheduling Premium Repository {}", repoId);
            try {
                supervisorClient.sendHarvestRepositoryRequest(repoId);
            } catch (CHARSException e) {
                logger.error("Exception: ", e);
            }
        }
    }

}
