package uk.ac.core.eventscheduler.periodic;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.eventscheduler.database.ArxivRepositoryDAO;
import uk.ac.core.supervisor.client.SupervisorClient;

import java.util.Date;

/**
 * @deprecated Not really sure why the scheduling is off. Probably there was a reason :)
 * @author Samuel Pearce
 */
@Deprecated
@Component
public class ScheduleArXiv {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(ScheduleArXiv.class);

    final ArxivRepositoryDAO arxivRepositoryDAO;

    final SupervisorClient supervisorClient;

    public ScheduleArXiv(ArxivRepositoryDAO arxivRepositoryDAO, SupervisorClient supervisorClient) {
        this.arxivRepositoryDAO = arxivRepositoryDAO;
        this.supervisorClient = supervisorClient;
    }

    //@Scheduled(cron = "0 0 0 * * ?")
    public void scheduleArXiv() throws CHARSException {
        Date lastUpdated = arxivRepositoryDAO.getLastUpdateTime();
        logger.info("Scheduling ArXiv, incremental since: {}", lastUpdated.toString());
        supervisorClient.sendHarvestRepositoryRequest(144, lastUpdated);
    }

}
