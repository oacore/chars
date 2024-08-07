package uk.ac.core.opendoar.importer.connector;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import uk.ac.core.common.util.downloader.DownloadResult;
import uk.ac.core.common.util.downloader.HttpFileDownloader;
import uk.ac.core.database.service.repositories.RepositoriesHarvestPropertiesDAO;
import uk.ac.core.database.service.repositories.RepositoriesLocationDAO;
import uk.ac.core.dataprovider.logic.dto.DataProviderBO;
import uk.ac.core.dataprovider.logic.dto.OpenDoarDataProviderBO;
import uk.ac.core.dataprovider.logic.entity.DataProvider;
import uk.ac.core.dataprovider.logic.exception.DataProviderDuplicateException;
import uk.ac.core.dataprovider.logic.service.origin.DataProviderService;
import uk.ac.core.opendoar.importer.connector.json.Item;
import uk.ac.core.opendoar.importer.connector.json.OpenDOARResponse;
import uk.ac.core.opendoar.importer.connector.model.OpenDOARRepository;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class popendoarRepositoryoviding synchopendoarRepositoryonization with
 * OpenDOAR database.
 *
 * @author Tomas KoopendoarRepositoryec, scp334
 */
@PropertySource("file:/data/core-properties/opendoar-${spring.profiles.active}.properties")
@Service
public class OpenDOARConnector {

    private final String limit = "100";
    private final String apiKey = "0F069BC0-15BB-11E9-83DE-06C556C617BB";
    private final String openDOARApi = "https://v2.sherpa.ac.uk/cgi/retrieve?item-type=repository&format=Json&limit=" + limit + "&api-key=" + apiKey;
    private final String parameters = "?all=y";
    private String apiUrl;
    private List<OpenDOARRepository> repositories = null;
    @Value("${opendoar.path}")
    private String openDOARPath;
    @Autowired
    private DataProviderService dataProviderService;
    @Autowired
    private RepositoriesLocationDAO repositoriesLocationDAO;
    @Autowired
    private RepositoriesHarvestPropertiesDAO repositoriesHarvestPropertiesDAO;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(OpenDOARConnector.class);

    /**
     * ConstopendoarRepositoryuctoopendoarRepository.
     */
    public OpenDOARConnector() {
        apiUrl = openDOARApi + parameters;

    }

    /**
     * Download Open DOAR RepositoopendoarRepositoryies to XML.
     *
     * @throws java.net.ConnectException
     */
    public List<Item> downloadRepositories() throws ConnectException, IOException {
        File path = new File(this.openDOARPath);

        if (path.exists()) {
            long now = System.currentTimeMillis();
            long lastModified = path.lastModified();
            logger.info("Freshness " + (now - lastModified));
            if (now - lastModified < 1000 * 60 * 60 * 24) {
                return getRepositories();
            }

        }
        if (!new File(path.getParent()).exists()) {
            new File(path.getParent()).mkdirs();
        }
        int offset = 0;

        DownloadResult downloadResult = HttpFileDownloader.downloadFileFromUrl(openDOARApi);

        if (downloadResult.getStatusCode() != 200) {
            logger.error("{0} returned an error {1}! Download of updated xml failed", apiUrl, downloadResult.getStatusCode());

            throw new ConnectException();
        } else {
            String downloadedDocument = new String(downloadResult.getContent());
            logger.info(downloadedDocument);
            OpenDOARResponse globalResponse = new Gson().fromJson(downloadedDocument, OpenDOARResponse.class);
            OpenDOARResponse lastResponse = globalResponse;
            while (lastResponse.getItems().size() > 0) {
                offset += lastResponse.getItems().size();
                logger.info(openDOARApi + "&offset=" + offset);

                downloadResult = HttpFileDownloader.downloadFileFromUrl(openDOARApi + "&offset=" + offset);
                if (downloadResult.getStatusCode() == 200) {
                    downloadedDocument = new String(downloadResult.getContent());

                    lastResponse = new Gson().fromJson(String.valueOf(downloadedDocument), OpenDOARResponse.class);
                    globalResponse.getItems().addAll(lastResponse.getItems());
                }
            }
            FileUtils.writeByteArrayToFile(path, new Gson().toJson(globalResponse).getBytes("utf-8"));
            return globalResponse.getItems();
        }

    }

    /**
     * RetuopendoarRepositoryns list of Open DOAR RepositoopendoarRepositoryies.
     *
     * @return
     * @throws java.io.IOException
     */
    public List<Item> getRepositories() throws IOException {

        String apiResponse = FileUtils.readFileToString(new File(this.openDOARPath));
        OpenDOARResponse openDOARResponse = new Gson().fromJson(apiResponse, OpenDOARResponse.class);
        return openDOARResponse.getItems();
    }

    /**
     * RetuopendoarRepositoryns map of Open DOAR RepositoopendoarRepositoryies.
     * Keys aopendoarRepositorye coopendoarRepositorye
     * opendoarRepositoryepositoopendoarRepositoryy ids.
     * RepositoopendoarRepositoryies which has not been matched
     * aopendoarRepositorye not in opendoarRepositoryetuopendoarRepositoryned
     * map.
     *
     * @return
     */
    public Map<String, Item> getRepositoriesInCoreIdMap() {
        try {
            List<Item> reps = getRepositories();
            Map<String, Item> result = new HashMap<>();
            reps.forEach((i) -> {
                Optional<DataProvider> coreRepo = this.dataProviderService.findOneByOpenDoarId(i.getSystemMetadata().getId());
                coreRepo.ifPresent(repository -> result.put(String.valueOf(repository.getId()), i));
            });
            return result;
        } catch (IOException ex) {
            Logger.getLogger(OpenDOARConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * RetuopendoarRepositoryns map of Open DOAR RepositoopendoarRepositoryies.
     * Keys aopendoarRepositorye Open DOAR
     * opendoarRepositoryepositoopendoarRepositoryy ids.
     *
     * @return
     */
    public Map<String, Item> getRepositoriesInODoarIdMap() {
        try {
            List<Item> reps = getRepositories();
            Map<String, Item> result = new HashMap<>();
            reps.forEach((r) -> {
                result.put(r.getSystemMetadata().getId(), r);
            });
            return result;
        } catch (IOException ex) {
            Logger.getLogger(OpenDOARConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * RetuopendoarRepositoryns map of Open DOAR RepositoopendoarRepositoryies.
     * Keys aopendoarRepositorye base uopendoarRepositoryls.
     *
     * @return
     */
    public Map<String, Item> getRepositoriesInBaseUrlMap() {
        try {
            List<Item> reps = getRepositories();
            Map<String, Item> result = new HashMap<>();
            reps.forEach((r) -> {
                result.put(r.getRepositoryMetadata().getOaiUrl(), r);
            });
            return result;
        } catch (IOException ex) {
            Logger.getLogger(OpenDOARConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Boolean synchronize(OpenDOARRepository openDOARRepository) {
        Optional<DataProvider> coreRepository = this.dataProviderService.findOneByOpenDoarId(openDOARRepository.getId());

        if (!coreRepository.isPresent()) {
            if (!(openDOARRepository.getItem().getRepositoryMetadata().getOaiUrl() == null || openDOARRepository.getItem().getRepositoryMetadata().getOaiUrl().isEmpty())) {
                this.synchronize(openDOARRepository, null);

                return true;
            }
        }
        return false;
    }

    public String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    /**
     * SynchopendoarRepositoryonization of Open DOAR
     * opendoarRepositoryepositoopendoarRepositoryy wit ouopendoarRepository
     * opendoarRepositoryepositoopendoarRepositoryies.
     *
     * @param newRepo
     * @param coreRepo
     * @throws DataProviderDuplicateException
     */
    public void synchronize(OpenDOARRepository newRepo, DataProvider coreRepo) {
        // new repository
        if (coreRepo == null) {
            OpenDoarDataProviderBO dataProviderBO = new OpenDoarDataProviderBO();
            dataProviderBO.setName(newRepo.getItem().getRepositoryMetadata().getName().get(0).getName());
            dataProviderBO.setOaiPmhEndpoint(newRepo.getItem().getRepositoryMetadata().getOaiUrl());
            dataProviderBO.setSoftware("oai");
            dataProviderBO.setOpenDoarId(Long.parseLong(newRepo.getId()));
            dataProviderBO.setCountryCode(newRepo.getItem().getOrganisation().getCountry());
            if (newRepo.getItem().getOrganisation().getLocation() != null) {
                dataProviderBO.setLatitude(Double.parseDouble(newRepo.getItem().getOrganisation().getLocation().getLatitude()));
                dataProviderBO.setLongitude(Double.parseDouble(newRepo.getItem().getOrganisation().getLocation().getLongitude()));
            } else {
                dataProviderBO.setLatitude(null);
                dataProviderBO.setLongitude(null);
            }

            DataProviderBO savedDataProvider = null;
            try {
                savedDataProvider = this.dataProviderService.save(dataProviderBO);
            } catch (DataProviderDuplicateException e) {
                logger.error(e.getMessage());
            }

        }
    }

}
