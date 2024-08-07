package uk.ac.core.baseimport.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.core.baseimport.exception.GenericBaseImportException;
import uk.ac.core.baseimport.flow.BaseImportReport;
import uk.ac.core.baseimport.model.BaseRepositoryTaskItem;
import uk.ac.core.baseimport.model.BaseRepositoryTaskStatus;
import uk.ac.core.baseimport.util.BaseRepositoriesImporter;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.dataprovider.logic.dto.BaseDataProviderBO;
import uk.ac.core.dataprovider.logic.dto.DataProviderBO;
import uk.ac.core.dataprovider.logic.entity.BaseRepository;
import uk.ac.core.dataprovider.logic.entity.IdentifyResponse;
import uk.ac.core.dataprovider.logic.entity.RepositoryHistory;
import uk.ac.core.dataprovider.logic.service.base.BaseRepositoryService;
import uk.ac.core.dataprovider.logic.service.history.RepositoryHistoryService;
import uk.ac.core.dataprovider.logic.service.oaipmhdiscovery.OaiPmhEndpointService;
import uk.ac.core.dataprovider.logic.service.origin.DataProviderService;
import uk.ac.core.worker.ScheduledWorker;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Import BASE worker.
 */
@Component
public class BaseImportWorker extends ScheduledWorker {

    private static final String URL_DELIMITER = "/";
    private static final int AUTHORITY_INDEX = 2;

    private static final String IMPORT_STARTED_MSG = "The import of BASE repositories has started.";
    private static final String TIMEOUT_ELAPSED_MSG = "The timeout for processing BASE urls has elapsed. Terminating the processing ...";
    private static final String NUMBER_OF_ENRICHED_BASE_REPOS_MSG = "%d BASE repos were enriched.";
    private static final String DATA_RETRIEVAL_ERROR_MSG = "The error has occurred during the data retrieval from BASE site.";
    private static final String NO_BASE_REPOS_WILL_BE_IMPORTED_MSG = "No BASE repos will be imported.";
    private static final String DOUBLE_MGS_PLACEHOLDER = "%s %s\n";

    private static final Logger LOG = LoggerFactory.getLogger(BaseImportWorker.class);
    private static final String FAILED_ENRICHMENT_MSG = "The enrichment of BASE repos has failed.";

    private final BaseRepositoryService baseRepositoryService;
    private final RepositoryHistoryService repositoryHistoryService;
    private final DataProviderService dataProviderService;
    private final OaiPmhEndpointService oaiPmhEndpointService;

    private static Map<String, String> errors = new HashMap<>();

    private static long importedBaseReposCount;

    public BaseImportWorker(BaseRepositoryService baseRepositoryService, RepositoryHistoryService historyRepositoryService,
                            DataProviderService dataProviderService, OaiPmhEndpointService oaiPmhEndpointService) {
        this.baseRepositoryService = baseRepositoryService;
        this.repositoryHistoryService = historyRepositoryService;
        this.dataProviderService = dataProviderService;
        this.oaiPmhEndpointService = oaiPmhEndpointService;
    }

    private final TaskType taskType = TaskType.BASE_IMPORT;

    //execute at midnight on Monday
    @Override
    @Scheduled(cron = "0 0 0 * * MON")
    public void scheduledStart() {
        this.start();
    }

    @Override
    public TaskDescription generateTaskDescription() {
        TaskDescription task = new TaskDescription();
        task.setCreationTime(System.currentTimeMillis());
        task.setStartTime(System.currentTimeMillis());
        task.setType(taskType);
        return task;
    }

    @Override
    public TaskType getTaskType() {
        return this.taskType;
    }

    @Override
    public List<TaskItem> collectData() {

        Set<BaseRepository> importedBaseRepos;
        try {
            importedBaseRepos = new BaseRepositoriesImporter()
                    .importBaseRepositories(baseRepositoryService.findAllBaseRepositories());
            importedBaseReposCount = importedBaseRepos.size();
        } catch (IOException e) {
            LOG.error(String.format(DOUBLE_MGS_PLACEHOLDER,
                    DATA_RETRIEVAL_ERROR_MSG, NO_BASE_REPOS_WILL_BE_IMPORTED_MSG));
            e.printStackTrace();
            return Collections.emptyList();
        }

        if (importedBaseRepos.isEmpty()) return Collections.emptyList();

        return importedBaseRepos.stream()
                .map(BaseRepositoryTaskItem::new)
                .collect(toList());
    }

    @Override
    public List<TaskItemStatus> process(List<TaskItem> taskItems) {

        LOG.debug(IMPORT_STARTED_MSG);

        if (taskItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<BaseRepository> importedBaseRepos = taskItems.stream()
                .map(taskItem -> (BaseRepositoryTaskItem) taskItem)
                .map(BaseRepositoryTaskItem::getBaseRepository)
                .collect(toList());

        // it is possible to perform repeated removal of the duplicates in order to decrease the time for oai pmh discovery,
        // but it will hurt encapsulation of the repository service because we don't want to expose findDuplicates method.
        Set<BaseRepository> enrichedNonHistoryBaseRepos = enrich(removeHistoricUrls(importedBaseRepos));

        Set<BaseRepository> allEnrichedBaseRepos = new HashSet<>(importedBaseRepos);
        if (enrichedNonHistoryBaseRepos != null) {
            allEnrichedBaseRepos.addAll(enrichedNonHistoryBaseRepos);
        }

        // save all base repos with and without oai-pmh endpoints
        baseRepositoryService.saveBaseRepositories(allEnrichedBaseRepos);

        List<BaseRepository> validBaseRepos = allEnrichedBaseRepos.stream()
                .filter(BaseRepository::hasOaiPmhEndpoint)
                .collect(toList());

        List<Long> savedRepos = new ArrayList<>();
        if (!validBaseRepos.isEmpty()) {
            // save only unique base repos with oai-pmh endpoints
            savedRepos = updateOriginRepositoryDataSource(validBaseRepos);
        }

        return new ArrayList<>(formBaseRepoStatuses(validBaseRepos, savedRepos));
    }

    private Set<BaseRepository> enrich(List<BaseRepository> nonHistoryBaseRepos) {
        try {
            return new HashSet<>(enrichWithOaiPmhEndpoints(nonHistoryBaseRepos));
        } catch (GenericBaseImportException e) {
            LOG.error(String.format(DOUBLE_MGS_PLACEHOLDER,
                    FAILED_ENRICHMENT_MSG, NO_BASE_REPOS_WILL_BE_IMPORTED_MSG));
            e.printStackTrace();
            // the only way to return the failure status for the whole execution without making changes
            return null;
        }
    }

    private List<BaseRepositoryTaskStatus> formBaseRepoStatuses(List<BaseRepository> validBaseRepos, List<Long> savedValidBaseRepos) {
        List<BaseRepositoryTaskStatus> baseRepositoryTaskStatuses = new ArrayList<>();

        for (BaseRepository baseRepo : validBaseRepos) {
            boolean isCoreRepoLinked = false;

            if (savedValidBaseRepos.contains(baseRepo.getId())) {
                isCoreRepoLinked = true;
            }

            if (isCoreRepoLinked) {
                baseRepositoryTaskStatuses.add(BaseRepositoryTaskStatus.newBuilder()
                        .duplicate(false)
                        .build());
            } else {
                baseRepositoryTaskStatuses.add(BaseRepositoryTaskStatus.newBuilder()
                        .oaiPmhEndpoint(baseRepo.getUrl())
                        .duplicate(true)
                        .build());
            }
        }

        return baseRepositoryTaskStatuses;
    }

    private List<BaseRepository> enrichWithOaiPmhEndpoints(List<BaseRepository> baseRepositories) throws GenericBaseImportException {

        Map<BaseRepository, Optional<IdentifyResponse>> searchResults = new HashMap<>();

        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(100);
        ThreadPoolExecutor findOaiPmhEndpointsExecutor = new ThreadPoolExecutor(3, 10, 30, TimeUnit.SECONDS, blockingQueue);
        findOaiPmhEndpointsExecutor.setRejectedExecutionHandler((runnable, executor) -> {
            try {
                executor.getQueue().put(runnable);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        AtomicInteger processedReposCounter = new AtomicInteger(0);

        for (BaseRepository baseRepository : baseRepositories) {
            findOaiPmhEndpointsExecutor.execute(() -> {
                Optional<IdentifyResponse> oaiResponse = null;
                try {
                    oaiResponse = oaiPmhEndpointService.findOaiPmhEndpoint(baseRepository.getUrl());

                    if (processedReposCounter.addAndGet(1) % 100 == 0) {
                        LOG.debug(String.format(NUMBER_OF_ENRICHED_BASE_REPOS_MSG, processedReposCounter.get()));
                    }
                    searchResults.put(baseRepository, oaiResponse);
                } catch (IOException e) {
                    LOG.error("Error in parsing url");
                    errors.put(baseRepository.getUrl(), e.getMessage());
                }
            });
        }

        findOaiPmhEndpointsExecutor.shutdown();

        while (true) {
            try {
                if (findOaiPmhEndpointsExecutor.awaitTermination(4, TimeUnit.HOURS)) break;
            } catch (InterruptedException e) {
                throw new GenericBaseImportException(TIMEOUT_ELAPSED_MSG);
            }
        }

        List<BaseRepository> baseReposWithOaiPmhEndpoints = new ArrayList<>();

        for (Map.Entry<BaseRepository, Optional<IdentifyResponse>> entry : searchResults.entrySet()) {
            BaseRepository baseRepository = entry.getKey();
            Optional<IdentifyResponse> possibleOaiPmhEndpoint = entry.getValue();
            if (possibleOaiPmhEndpoint.isPresent()) {
                baseRepository.setUrl(possibleOaiPmhEndpoint.get().getBaseUrl());
                baseRepository.setOaiPmhEndpointMarker(true);
            }
            baseReposWithOaiPmhEndpoints.add(baseRepository);

        }

        return baseReposWithOaiPmhEndpoints;
    }

    private List<Long> updateOriginRepositoryDataSource(List<BaseRepository> newBaseRepositories) {
        return dataProviderService.saveAll(
                newBaseRepositories.stream()
                        .map(this::convertToDataProviderBO)
                        .collect(toList())
        );
    }

    private DataProviderBO convertToDataProviderBO(BaseRepository baseRepository) {
        BaseDataProviderBO dataProviderBO = new BaseDataProviderBO(
                baseRepository.getUrl(),
                baseRepository.getName(),
                null,
                false,
                baseRepository.getCountryCode());
        dataProviderBO.setBaseId(baseRepository.getId());
        dataProviderBO.setLongitude(baseRepository.getLongitude());
        dataProviderBO.setLatitude(baseRepository.getLatitude());
        dataProviderBO.setCountryCode(baseRepository.getCountryCode());
        dataProviderBO.setSoftware(baseRepository.getSystem());
        dataProviderBO.setCreatedAt(baseRepository.getInCoreSince().atStartOfDay());
        return dataProviderBO;
    }

    private List<BaseRepository> removeHistoricUrls(List<BaseRepository> originalRepositories) {
        List<String> historicFormattedUrls = repositoryHistoryService.getHistoryRepositories().stream()
                .map(RepositoryHistory::getHistoricUrl)
                .map(this::getUppercaseUrlAuthority)
                .collect(toList());

        return originalRepositories.stream()
                .filter(baseRepository -> !historicFormattedUrls.contains(getUppercaseUrlAuthority(baseRepository.getUrl())))
                .collect(toList());
    }

    private String getUppercaseUrlAuthority(String url) {
        return url.toUpperCase().split(URL_DELIMITER)[AUTHORITY_INDEX];
    }

    @Override
    public boolean evaluate(List<TaskItemStatus> results, List<TaskItem> taskItems) {
        return true;
    }

    @Override
    public String generateReport(List<TaskItemStatus> results, boolean taskOverallSuccess) {
        return new BaseImportReport(
                importedBaseReposCount,
                results,
                taskOverallSuccess,
                this.getCurrentWorkingTask().getStartTime())
                .getMessage();
    }

    @Override
    public void collectStatistics(List<TaskItemStatus> results) {

    }

    public static Map<String, String> getErrors() {
        return errors;
    }
}