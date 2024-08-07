package uk.ac.core.eventscheduler.periodic;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.eventscheduler.service.BigRepositoryService;
import uk.ac.core.supervisor.client.SupervisorClient;


import java.util.Date;
import java.util.List;

@Component
public class BigRepositoriesIncrementalHarvestingFreshScheduler {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(BigRepositoriesIncrementalHarvestingFreshScheduler.class);

    @Autowired
    private BigRepositoryService bigRepositoryService;

    @Autowired
    private SupervisorClient supervisorClient;

    @Scheduled(cron = "0 0 0 */2 * ?")
    public void scheduleBigRepositories() {
        logger.info("Start harvesting big repositories");

        List<Integer> repositories = bigRepositoryService.getBigRepositories();
        for (Integer repository : repositories){
            Pair<Date,Date> dates = bigRepositoryService.getIncrementalHarvestingDates(repository);

            if (dates == null) {
                logger.info("Can't find dates for harvesting repository {}", repository);
                continue;
            }

            Date fromDate = dates.getKey();
            Date toDate = dates.getValue();
            try {
                supervisorClient.sendHarvestRepositoryRequest(repository, fromDate, toDate);
            } catch (Exception e) {
                logger.error("Failed to send a harvest request for repository {}", repository);
                logger.error("Exception", e);
                continue;
            }
            logger.info("Request was sent for repository {} with dates {}, {}", repository, fromDate, toDate);
        }
        logger.info("All large repositories done");
    }






}
