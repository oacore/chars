package uk.ac.core.eventscheduler.service;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.common.model.task.parameters.MetadataExtractParameters;
import uk.ac.core.eventscheduler.database.CrossrefRepositoryDAO;
import uk.ac.core.supervisor.client.SupervisorClient;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CrossrefReharvestingMEService extends CrossrefReharvestingService<MetadataExtractParameters> {

    private final Logger logger = LoggerFactory.getLogger(CrossrefReharvestingMDService.class);

    private final SupervisorClient supervisorClient;
    private final CrossrefRepositoryDAO crossrefRepositoryDAO;

    public CrossrefReharvestingMEService(SupervisorClient supervisorClient,
                                         CrossrefRepositoryDAO crossrefRepositoryDAO) {
        this.supervisorClient = supervisorClient;
        this.crossrefRepositoryDAO = crossrefRepositoryDAO;
    }

    @Scheduled(cron = "0 0 13 * * MON")
    public void rerunMetadataExtract() {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(7);
        logger.info("METADATA EXTRACT REHARVESTING WAS STARTED");

        rerunUnsuccessfulTaskParams(Date.valueOf(from), Date.valueOf(to));
    }

    @Override
    protected List<Pair<MetadataExtractParameters, Date>> getUnsuccessfulTasks(Date fromDate, Date toDate) {
        return crossrefRepositoryDAO.getUnsuccessfulTasks("extract-metadata", fromDate, toDate).stream()
                .map(i -> Pair.of(gson.fromJson(i.getLeft(), MetadataExtractParameters.class), i.getRight()))
                .collect(Collectors.toList());
    }

    @Override
    protected void sendRequest(MetadataExtractParameters parameter) {
        try {
            if (parameter.getFromDate() != null && parameter.getToDate() != null) {
                supervisorClient.sendMetadataExtractRepositoryRequest(parameter.getRepositoryId(), parameter.getFromDate(), parameter.getToDate());
            } else {
                logger.error("Important fields wasn't set. RepositoryId {}, fromDate {}, toDate {}",
                        parameter.getRepositoryId(), parameter.getFromDate(), parameter.getToDate());
            }
        } catch (CHARSException e) {
            logger.error("Error sending metadata extract request for repository {} with from date: {} and to date: {}",
                    parameter.getRepositoryId(), parameter.getFromDate(), parameter.getToDate(), e);
        }
    }
}
