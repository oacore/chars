package uk.ac.core.dataprovider.logic.service.origin.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.dataprovider.logic.converter.DataProviderConverter;
import uk.ac.core.dataprovider.logic.dto.DataProviderBO;
import uk.ac.core.dataprovider.logic.dto.SyncResult;
import uk.ac.core.dataprovider.logic.entity.*;
import uk.ac.core.dataprovider.logic.exception.DataProviderDuplicateException;
import uk.ac.core.dataprovider.logic.exception.DataProviderNotFoundException;
import uk.ac.core.dataprovider.logic.repository.DashboardRepoRepository;
import uk.ac.core.dataprovider.logic.repository.DataProviderLocationRepository;
import uk.ac.core.dataprovider.logic.repository.RepositoryHarvestPropertiesRepository;
import uk.ac.core.dataprovider.logic.repository.RorDataRepository;
import uk.ac.core.dataprovider.logic.repository.dataprovider.DataProviderRepository;
import uk.ac.core.dataprovider.logic.service.index.RepositoryIndexService;
import uk.ac.core.dataprovider.logic.service.origin.DataProviderService;
import uk.ac.core.dataprovider.logic.util.UrlOperations;
import uk.ac.core.dataprovider.logic.util.exception.UrlOperationsException;
import uk.ac.core.slack.client.SlackWebhookService;
import uk.ac.core.slack.client.model.SlackMessage;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class DataProviderServiceImpl implements DataProviderService {

    private final DataProviderRepository dataProviderRepository;
    private final RepositoryHarvestPropertiesRepository harvestPropertiesRepository;
    private final RepositoryIndexService repositoryIndexService;
    private final RepositoryHarvestPropertiesRepository repositoryHarvestPropertiesRepository;
    private final DataProviderLocationRepository dataProviderLocationRepository;
    private final DashboardRepoRepository dashboardRepoRepository;
    private final RorDataRepository rorDataRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final String NUMBER_OF_DUPLICATE_REPOS_MSG = "There were %d duplicate repositories.";
    private static final String URL_DOMAIN_NAME_RETRIEVAL_FAILURE_MSG = "Couldn't get a domain name for given url: %s \n. The deduplication check based on URL domain name wasn't performed.";
    private static final List<String> JOURNAL_IDENTIFIERS = Collections.unmodifiableList(Arrays.asList("journal", "index.php", "jurnal"));

    private static final String MOCK_REPO_NAME = "Mock Repo";

    private final List<String> allowedDuplicateOaiPmhUrls = Arrays.asList(
            "api.archives-ouvertes.fr",
            "zenodo.org",
            "api.figshare.com"
    );

    private static final Logger LOG = LoggerFactory.getLogger(DataProviderServiceImpl.class);

    @Autowired
    public DataProviderServiceImpl(DataProviderRepository dataProviderRepository,
                                   RepositoryHarvestPropertiesRepository harvestPropertiesRepository,
                                   DataProviderLocationRepository dataProviderLocationRepository,
                                   DashboardRepoRepository dashboardRepoRepository,
                                   RepositoryIndexService repositoryIndexService,
                                   RepositoryHarvestPropertiesRepository repositoryHarvestPropertiesRepository,
                                   RorDataRepository rorDataRepository, JdbcTemplate jdbcTemplate) {
        this.dataProviderRepository = dataProviderRepository;
        this.harvestPropertiesRepository = harvestPropertiesRepository;
        this.dataProviderLocationRepository = dataProviderLocationRepository;
        this.dashboardRepoRepository = dashboardRepoRepository;
        this.repositoryIndexService = repositoryIndexService;
        this.repositoryHarvestPropertiesRepository = repositoryHarvestPropertiesRepository;
        this.rorDataRepository = rorDataRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SyncResult syncAll() {

        List<DataProvider> dataProviders = dataProviderRepository.findAll();

        repositoryIndexService.deleteByIdIsNull();
        repositoryIndexService.refresh();

        List<IndexedDataProvider> indexed = (List<IndexedDataProvider>) repositoryIndexService.saveAll(dataProviders.stream()
                .map(elem -> new IndexedDataProvider(elem,
                        dataProviderLocationRepository.findById(elem.getId()).orElse(new DataProviderLocation()),
                        dashboardRepoRepository.findById(elem.getId()).orElse(new DashboardRepo()),
                        rorDataRepository.findById(elem.getId()).orElse(new RorData())))
                .collect(toList())
        );

        long addedToIndexCounter = indexed.size();

        int totalCount = repositoryIndexService.findAllViaConsumer(
                indexedDataProvider -> saveIfDataProviderExists(indexedDataProvider)
        );

        return new SyncResult(addedToIndexCounter, totalCount);
    }

    private void saveIfDataProviderExists(IndexedDataProvider indexedDataProvider) {
        if (!dataProviderRepository.findById(indexedDataProvider.getId()).isPresent()) {
            repositoryIndexService.deleteById(indexedDataProvider.getId());
        }
    }

    @Override
    public void syncOne(Long id) throws DataProviderNotFoundException {
        DataProvider dataProvider = dataProviderRepository.findById(id)
                .orElseThrow(() -> new DataProviderNotFoundException(id));

        Optional<DataProviderLocation> dataProviderLocation = dataProviderLocationRepository.findById(id);

        Optional<DashboardRepo> dashboardRepo = dashboardRepoRepository.findById(id);

        repositoryIndexService.indexRepository(dataProvider, dataProviderLocation.orElse(new DataProviderLocation()),
                dashboardRepo.orElse(new DashboardRepo()), rorDataRepository.findById(id).orElse(new RorData()));
    }

    @Override
    public DataProviderBO save(DataProviderBO dataProviderBO) throws DataProviderDuplicateException {

        if (dataProviderBO.getName().equals(MOCK_REPO_NAME)) {
            SlackMessage sm = new SlackMessage();
            sm.setText("Mock data provider arrived to the DataProviderService.save()");
            SlackWebhookService.sendMessage(sm, "data-provider-report-mock");
            return aggregateSave(updateJournalFlag(dataProviderBO));
        }

        List<DataProviderBO> duplicateDataProviders = this.findDuplicateRepositories(dataProviderBO);

        if (!duplicateDataProviders.isEmpty()) {
            throw new DataProviderDuplicateException(duplicateDataProviders);
        }

        return aggregateSave(updateJournalFlag(dataProviderBO));
    }

    @Override
    public DataProviderBO update(DataProviderBO patch) {
        return aggregateUpdate(patch);
    }

    private DataProviderBO aggregateSave(DataProviderBO dataProviderBO) {
        if (dataProviderBO.getName().equals(MOCK_REPO_NAME)) {
            SlackMessage sm = new SlackMessage();
            sm.setText("Mock data provider arrived to the DataProviderService.aggregateSave()");
            SlackWebhookService.sendMessage(sm, "data-provider-report-mock");
            return dataProviderBO;
        }
        DataProvider savedDataProvider = this.dataProviderRepository.save(DataProviderConverter.toDataProvider(dataProviderBO));
        DataProviderLocation dataProviderLocationToSave = DataProviderConverter.toDataProviderLocation(dataProviderBO);
        dataProviderLocationToSave.setId(savedDataProvider.getId());
        DataProviderLocation savedDataProviderLocation = dataProviderLocationRepository.save(dataProviderLocationToSave);
        repositoryIndexService.indexRepository(
                savedDataProvider,
                savedDataProviderLocation,
                dashboardRepoRepository.findById(savedDataProvider.getId()).orElse(new DashboardRepo()),
                rorDataRepository.findById(savedDataProvider.getId()).orElse(new RorData()));

        repositoryHarvestPropertiesRepository.save(new RepositoryHarvestProperties(savedDataProvider.getId()));

        SlackMessage sm = composeSlackMessage(savedDataProvider, savedDataProviderLocation);
        SlackWebhookService.sendMessage(sm, "data-provider-report");

        return DataProviderConverter.toCompleteDataProviderBO(savedDataProvider, savedDataProviderLocation);
    }

    private SlackMessage composeSlackMessage(
            DataProvider dataProvider,
            DataProviderLocation dataProviderLocation) {
        SlackMessage slackMessage = new SlackMessage();
        StringBuilder sb = new StringBuilder();

        final ZoneId zoneId = ZoneId.of("Europe/London");
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.now(), zoneId);
        String dateTime = zonedDateTime.format(DateTimeFormatter.ofPattern("EEE d MMM yyyy hh:mm:ss a O"));

        sb.append(String.format("[%s]%n", dateTime));
        sb.append(String.format("New data provider:%n"));

        sb.append(String.format(
                "ID: %d%n" +
                        "Name: %s%n" +
                        "Homepage: %s%n" +
                        "See more details on %s%n",
                dataProvider.getId(),
                dataProvider.getName(),
                dataProvider.getUrlHomepage(),
                "https://apple.core.ac.uk/dataproviders/" + dataProvider.getId()));

        slackMessage.setText(sb.toString());
        return slackMessage;
    }

    private DataProviderBO aggregateUpdate(DataProviderBO dataProviderBO) {
        DataProvider savedDataProvider = dataProviderRepository.save(DataProviderConverter.toDataProvider(dataProviderBO));

        DataProviderLocation location = setLocation(dataProviderBO);

        repositoryIndexService.indexRepository(savedDataProvider, location,
                dashboardRepoRepository.findById(dataProviderBO.getId()).orElse(new DashboardRepo()),
                this.rorDataRepository.findById(dataProviderBO.getId()).orElse(new RorData()));

        return DataProviderConverter.toCompleteDataProviderBO(savedDataProvider, location);
    }

    private DataProviderLocation setLocation(DataProviderBO dataProviderBO) {
        DataProviderLocation location = dataProviderLocationRepository.findById(dataProviderBO.getId()).orElseGet(DataProviderLocation::new);
        if (dataProviderBO.getCountryCode() != null) {
            location.setCountryCode(dataProviderBO.getCountryCode());
        }
        if (dataProviderBO.getLatitude() != null) {
            location.setLatitude(dataProviderBO.getLatitude());
        }
        if (dataProviderBO.getLongitude() != null) {
            location.setLongitude(dataProviderBO.getLongitude());
        }

        return location;
    }

    @Override
    public List<Long> saveAll(List<DataProviderBO> dataProvidersBO) {
        List<Long> savedRepos = new ArrayList<>();

        long savedReposCount = 0;
        for (DataProviderBO dataProviderBO : dataProvidersBO) {
            try {
                savedRepos.add(save(dataProviderBO).getId());
                savedReposCount++;
            } catch (DataProviderDuplicateException ignored) {

            }
        }

        if (savedReposCount > 0) {
            long duplicateReposCount = dataProvidersBO.size() - savedReposCount;
            LOG.debug(String.format(NUMBER_OF_DUPLICATE_REPOS_MSG, duplicateReposCount));
        }

        return savedRepos;
    }

    public List<DataProviderBO> findDuplicateRepositories(DataProviderBO dataProviderBO) {

        String oaiPmhEndpoint = dataProviderBO.getOaiPmhEndpoint();

        if (oaiPmhEndpoint != null) {
            List<DataProviderBO> duplicateRepositories = findAllReposByName(dataProviderBO.getName());
            duplicateRepositories.addAll(findAllReposWithSimilarOaiPmhEndpoint(oaiPmhEndpoint));
            duplicateRepositories.addAll(findAllReposOnExactOaiMatch(oaiPmhEndpoint));

            if (!isJournal(dataProviderBO) && !urlContainsAllowedDuplicateHostname(oaiPmhEndpoint)) {
                duplicateRepositories.addAll(findDuplicatesOnHostname(dataProviderBO));
            }

            if (!duplicateRepositories.isEmpty()) {
                Map<Long, DataProviderBO> mapWithoutDuplicates = new HashMap<>();
                duplicateRepositories.forEach(x -> mapWithoutDuplicates.put(x.getId(), x));
                mapWithoutDuplicates.remove(dataProviderBO.getId());

                return new ArrayList<>(
                        mapWithoutDuplicates.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                        (oldValue, newValue) -> oldValue, LinkedHashMap::new))
                                .values()
                );
            }
        }
        return Collections.emptyList();
    }

    public int disableAllDuplicates() throws DataProviderNotFoundException {
        LOG.info("Stared disabling all duplicates");
        List<Long> repositories = dataProviderRepository.findIds();
        int result = 0;
        for (Long repository : repositories) {
            if (!dataProviderRepository.isDisabled(repository)) {
                result += disableDataProviderDuplicates(repository);
            }
        }
        return result;
    }

    public int disableDataProviderDuplicates(Long repositoryId) throws DataProviderNotFoundException {
        try {
            LOG.info("Disabling duplicates for repository {} was started ", repositoryId);
            DataProviderBO dataProviderBO = findById(repositoryId);
            List<DataProviderBO> duplicates = findDuplicateRepositories(dataProviderBO);

            for (DataProviderBO dataProvider : duplicates) {
                if (!dataProviderRepository.isDisabled(dataProvider.getId())) {
                    disableRepository(dataProvider.getId());
                }
            }

            return duplicates.size();
        } catch (Exception e) {
            LOG.error("Error while disableDataProviderDuplicates for repository {}", repositoryId, e);
            return 0;
        }
    }


    public void disableRepository(Long repositoryId) {
        String sql = "UPDATE repository " +
                "set disabled = 1 where id_repository = ?";
        jdbcTemplate.update(sql, repositoryId);

        sql = "UPDATE repository_harvest_properties " +
                "set disabled = 1 where id_repository = ?";
        jdbcTemplate.update(sql, repositoryId);
    }

    private boolean urlContainsAllowedDuplicateHostname(String url) {
        return allowedDuplicateOaiPmhUrls.stream()
                .anyMatch(url::contains);
    }

    private DataProviderBO updateJournalFlag(DataProviderBO dataProviderBO) {
        if (isJournal(dataProviderBO)) {
            dataProviderBO.setJournal(true);
        }
        return dataProviderBO;
    }

    private List<DataProviderBO> findAllReposOnExactOaiMatch(String oaiPmhEndpoint) {
        return dataProviderRepository.findByUrlOaipmhIgnoreCase(oaiPmhEndpoint)
                .stream()
                .map(DataProviderConverter::toCompactDataProviderBOWithActiveStatus)
                .collect(toList());
    }

    private List<DataProviderBO> findAllReposByName(String name) {
        return dataProviderRepository.findByNameIgnoreCase(name).stream()
                .map(DataProviderConverter::toCompactDataProviderBOWithActiveStatus)
                .collect(toList());
    }

    private boolean isJournal(DataProviderBO dataProviderBO) {
        if (dataProviderBO.isJournal()) {
            return true;
        } else {
            return JOURNAL_IDENTIFIERS.stream()
                    .anyMatch(journalIdentifier -> dataProviderBO.getOaiPmhEndpoint().contains(journalIdentifier) || dataProviderBO.getName().toLowerCase().contains(journalIdentifier));
        }
    }

    private List<DataProviderBO> findDuplicatesOnHostname(DataProviderBO dataProviderBO) {
        try {
            String urlDomainName = UrlOperations.getUrlHost(dataProviderBO.getOaiPmhEndpoint());
            return dataProviderRepository.findByUrlOaipmhContainingOrderById(urlDomainName).stream()
                    .map(DataProviderConverter::toCompactDataProviderBOWithActiveStatus)
                    .collect(toList());
        } catch (UrlOperationsException ex) {
            LOG.error(URL_DOMAIN_NAME_RETRIEVAL_FAILURE_MSG, ex);
            return Collections.emptyList();
        }
    }

    // used in the scheduled worker apart from this class
    @Override
    public List<DataProviderBO> findAllReposWithSimilarOaiPmhEndpoint(String url) {
        return dataProviderRepository.findByUrlOaipmhContainingOrderById(url).stream()
                .map(DataProviderConverter::toCompactDataProviderBOWithActiveStatus)
                .collect(toList());
    }

    @Override
    public DataProviderBO findById(long id) throws DataProviderNotFoundException {
        return DataProviderConverter.toCompactDataProviderBOWithActiveStatus(dataProviderRepository.findById(id).orElseThrow(() -> new DataProviderNotFoundException(id)));
    }

    @Override
    public DataProvider legacyFindById(long id) {
        return dataProviderRepository.findById(id).orElseThrow(() -> new RuntimeException("Data provider wasn't found."));
    }

    @Override
    public Optional<DataProvider> findOneByOpenDoarId(String openDOARId) {
        return this.dataProviderRepository.findByOpenDoarId(Long.valueOf(openDOARId));
    }

    @Override
    public Page<DataProviderBO> findAll(Pageable page, boolean journal, boolean enabled) {
        return this.dataProviderRepository.findByDisabledAndJournal(!enabled, journal, page)
                .map(DataProviderConverter::toCompactDataProviderBOWithActiveStatus);
    }

    @Override
    public void delete(long id) throws DataProviderNotFoundException {
        DataProvider dataProvider = dataProviderRepository.findById(id).orElseThrow(() -> new DataProviderNotFoundException(id));
        dataProvider.setDisabled(true);
    }
}
