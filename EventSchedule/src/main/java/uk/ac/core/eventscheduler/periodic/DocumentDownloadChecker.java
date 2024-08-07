package uk.ac.core.eventscheduler.periodic;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.eventscheduler.service.BigRepositoryService;
import uk.ac.core.supervisor.client.SupervisorClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
public class DocumentDownloadChecker {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(DocumentDownloadChecker.class);

    @Autowired
    private BigRepositoryService bigRepositoryService;

    @Autowired
    private SupervisorClient supervisorClient;

    @Scheduled(cron = "0 0 0 * * SUN")
    public void reharvestDocumentDownloadIncrementalTasks() {
        logger.info("Start checking unsuccessful document download tasks");

        List<Integer> repositories = bigRepositoryService.getBigRepositories();

        Date checkingDate = Date.from(LocalDate.now().minusDays(14).atStartOfDay(ZoneId.systemDefault()).toInstant());


        for (Integer repository: repositories){
            List<Pair<Date,Date>> allDates =
                    bigRepositoryService.getIncrementalHarvestingDatesForFailedDD(repository, checkingDate);
            if (allDates == null || allDates.isEmpty()) {
                logger.info("Can't find dates for document download harvesting");
                return;
            }

            for(Pair<Date,Date> date: allDates){
                Date fromDate = date.getKey();
                Date toDate = date.getValue();
                //logger.info("FromDate = {}, ToDate = {}", fromDate, toDate);
                try {
                    supervisorClient.sendPdfDownloadRepositoryRequest(repository, fromDate, toDate);
                    logger.info("Request was sent");
                } catch (Exception e) {
                    logger.error("Failed to send request", e);
                }
            }

        }

    }

}
