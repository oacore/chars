package uk.ac.core.eventscheduler.service;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.common.tasks.metadataDownload.MetadataDownloadParameters;
import uk.ac.core.eventscheduler.database.CrossrefRepositoryDAO;
import uk.ac.core.supervisor.client.SupervisorClient;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CrossrefReharvestingMDService extends CrossrefReharvestingService<MetadataDownloadParameters> {
    private final Logger logger = LoggerFactory.getLogger(CrossrefReharvestingMDService.class);

    private final SupervisorClient supervisorClient;
    private final CrossrefRepositoryDAO crossrefRepositoryDAO;

    public CrossrefReharvestingMDService(SupervisorClient supervisorClient,
                                         CrossrefRepositoryDAO crossrefRepositoryDAO) {
        this.supervisorClient = supervisorClient;
        this.crossrefRepositoryDAO = crossrefRepositoryDAO;
    }

    @Scheduled(cron = "0 0 12 * * MON")
    public void rerunMetadataDownload() {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(7);
        logger.info("METADATA DOWNLOAD REHARVESTING WAS STARTED");

        rerunUnsuccessfulTaskParams(Date.valueOf(from), Date.valueOf(to));
    }

    @Override
    protected List<Pair<MetadataDownloadParameters, Date>> getUnsuccessfulTasks(Date fromDate, Date toDate) {
        return crossrefRepositoryDAO.getUnsuccessfulTasks("metadata_download", fromDate, toDate).stream()
                .map(i -> Pair.of(gson.fromJson(i.getLeft(), MetadataDownloadParameters.class), i.getRight()))
                .collect(Collectors.toList());
    }

    @Override
    protected void sendRequest(MetadataDownloadParameters parameter) {
        try {
            if (parameter.getFromDate() != null && parameter.getToDate() != null) {
                supervisorClient.sendMetadataDownloadRepositoryRequest(parameter.getRepositoryId(), parameter.getFromDate(), parameter.getToDate());
            } else {
                logger.error("Important fields wasn't set. RepositoryId {}, fromDate {}, toDate {}",
                        parameter.getRepositoryId(), parameter.getFromDate(), parameter.getToDate());
            }
        } catch (CHARSException e) {
            logger.error("Error sending metadata download request for repository {} with from date: {} and to date: {}",
                    parameter.getRepositoryId(), parameter.getFromDate(), parameter.getToDate(), e);
        }
    }
}
