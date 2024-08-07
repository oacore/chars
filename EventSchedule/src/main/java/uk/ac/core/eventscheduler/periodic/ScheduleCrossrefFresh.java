package uk.ac.core.eventscheduler.periodic;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.supervisor.client.SupervisorClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class ScheduleCrossrefFresh {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(ScheduleCrossrefFresh.class);

    private final SupervisorClient supervisorClient;


    public ScheduleCrossrefFresh(SupervisorClient supervisorClient) {
        this.supervisorClient = supervisorClient;
    }



    //@Scheduled(cron = "0 0 0 */2 * ?")
    public void scheduleCrossrefFresh() throws CHARSException {
        logger.info("CROSSREF FRESH HARVESTING STARTED");
        Date fromDate = Date.from(LocalDate.now().minusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date toDate = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        logger.info("Sending harvest request with fromDate {} and toDate {}", fromDate, toDate);
        supervisorClient.sendHarvestRepositoryRequest(4786, fromDate, toDate);
        logger.info("CROSSREF FRESH request is finished");
    }



}
