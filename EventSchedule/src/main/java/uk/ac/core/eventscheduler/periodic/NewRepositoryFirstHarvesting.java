package uk.ac.core.eventscheduler.periodic;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.core.eventscheduler.database.NewRepositoryDao;

@Component
public class NewRepositoryFirstHarvesting {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(NewRepositoryFirstHarvesting.class);

    private final NewRepositoryDao newRepositoryDao;

    public NewRepositoryFirstHarvesting(NewRepositoryDao newRepositoryDao) {
        this.newRepositoryDao = newRepositoryDao;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleNewRepositories() {
        logger.info("Start inserting of unharvested repositories to the scheduled_repository table");
        newRepositoryDao.insertUnharvestedIntoScheduledRepository();
        logger.info("Inserting of unharvested repositories to the scheduled_repository table was finished");
    }
}
