package uk.ac.core.metadatadownloadworker.worker.metadata;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.ac.core.common.model.legacy.LegacyRepository;
import uk.ac.core.database.service.repositories.RepositoriesDAO;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.metadatadownloadworker.exception.ResumptionTokenException;
import uk.ac.core.metadatadownloadworker.model.RepositoryMetadataDownloadAllowedSets;
import uk.ac.core.metadatadownloadworker.service.RepositoryMetadataDownloadAllowedSetsListService;
import uk.ac.core.metadatadownloadworker.worker.metadata.oaipmh.MetadataDownloadFailureIssueCallback;
import uk.ac.core.metadatadownloadworker.worker.metadata.oaipmh.OaiPmhMetadataRawWriteService;
import uk.ac.core.metadatadownloadworker.worker.metadata.oaipmh.OaiPmhMetadataRawWriteServiceImpl;
import uk.ac.core.metadatadownloadworker.worker.util.CleanUrl;
import uk.ac.core.supervisor.client.SupervisorClient;
import uk.ac.core.worker.WorkerStatus;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Tomas Korec
 */
@Service
public class OaiPmhDownloaderService implements DownloadMetadata {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(OaiPmhDownloaderService.class);

    @Value("${oaiPmhCrossrefToken}")
    private String oaiPmhCrossrefToken;

    private Integer repositoryId;
    
    private List<RepositoryMetadataDownloadAllowedSets> allowedSets;
    
    private LegacyRepository repository;
    @Autowired
    private RepositoriesDAO repositoryDAO;
    @Autowired
    private FilesystemDAO filesystemDAO;
    @Autowired
    private SupervisorClient supervisorClient;
    @Autowired
    private WorkerStatus metadataDownloadWorkerStatus;
    @Autowired
    private RepositoryMetadataDownloadAllowedSetsListService repositoryMetadataDownloadAllowedSetsListService;

    Date fromDate;
    Date toDate;
    private final String FROM_AND_TO_DATE_FORMAT = "yyyy-MM-dd";


    public void init(Integer repositoryId) {
        this.repositoryId = repositoryId;
        this.repository = repositoryDAO.getRepositoryById(String.valueOf(repositoryId));
        this.allowedSets = repositoryMetadataDownloadAllowedSetsListService.findByRepositoryId(repositoryId.longValue());
    }

    public void init(Integer repositoryId, Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.init(repositoryId);
    }

    @Override
    public void downloadMetadata(MetadataDownloadFailureIssueCallback metadataDownloadFailureIssueCallback, OutputStream outputStream) throws Exception {
        //String baseUrl = "https://digitallibrary.sissa.it/dspace-oai/request?verb=Identify";
        try {

            String from = null;
            String until = null;
            if (fromDate != null) {
                // format in OAI-PMH way, i.e. convert it to yyyy-MM-dd format
                SimpleDateFormat fromDateFormatter = new SimpleDateFormat(FROM_AND_TO_DATE_FORMAT);
                from = fromDateFormatter.format(fromDate);
                logger.info("fromDate: " + fromDate + " formatted to: " + from + " and passed as from parameter for incremental harvesting");
            }

            if(toDate != null){
                SimpleDateFormat toDateFormatter = new SimpleDateFormat(FROM_AND_TO_DATE_FORMAT);
                until = toDateFormatter.format(toDate);
                logger.info("toDate: " + toDate + " formatted to: " + until + " and passed as to parameter for incremental harvesting");

            }

            String baseUrl = checkRepositoryUri(repository);

            // putting already injected bean into the constructor should not be simulated
            // this serves only as a workaround for a needless bean declaration inside the service above
            OaiPmhMetadataRawWriteService oaiPmhMetadataRawWriteService = new OaiPmhMetadataRawWriteServiceImpl(
                    metadataDownloadWorkerStatus,
                    outputStream,
                    baseUrl);

            oaiPmhMetadataRawWriteService.identify();
            oaiPmhMetadataRawWriteService.listMetadataFormats();
            oaiPmhMetadataRawWriteService.listSets(repositoryId);

            if (repositoryId == 150) {
                // Create new custom FileOutput stream for Pubmed IDs
                // Delete old files but only replace 150-ids.xml if the download was 100% successful
                OutputStream pubmedIdentifiersXml = new FileOutputStream(this.filesystemDAO.getMetadataStoragePath("150/150-ids_part.xml"));
                pubmedIdentifiersXml.write("<?xml version=\"1.1\" encoding=\"UTF-8\"?>\n".getBytes("UTF-8"));
                pubmedIdentifiersXml.write("<harvest>\n".getBytes("UTF-8"));

                // Download all IDs from pubmed
                oaiPmhMetadataRawWriteService.listIdentifiers(
                        from, // From
                        until, // Until
                        repository.getMetadataFormat(),
                        null, // SetSpec
                        pubmedIdentifiersXml,
                        repositoryId
                );

                pubmedIdentifiersXml.write("</harvest>\n".getBytes("UTF-8"));

                pubmedIdentifiersXml.flush();
                pubmedIdentifiersXml.close();

                File oldF = new File(this.filesystemDAO.getMetadataStoragePath("150/150-ids_part.xml"));
                File newF = new File(this.filesystemDAO.getMetadataStoragePath("150/150-ids.xml"));
                oldF.renameTo(newF);

                List<String> identifiers = getListIdentifiers(this.filesystemDAO.getMetadataStoragePath("150/150-ids.xml"));
                for (String identifier : identifiers) {
                    oaiPmhMetadataRawWriteService.GetAllRecordsByGetRecord(new CleanUrl(repository.getUrlOaipmh()).toString(),
                            repository.getMetadataFormat(), identifier, outputStream);
                }

            } else {
                if (this.allowedSets.isEmpty()) {
                    this.allowedSets.add(new RepositoryMetadataDownloadAllowedSets(repositoryId, null, "All Records"));
                }
                for (RepositoryMetadataDownloadAllowedSets set : this.allowedSets) {
                    logger.debug("Harvesting from Set {}:{}", set.getSetSpec(), set.getSetName());
                    oaiPmhMetadataRawWriteService.listRecords(
                            from, // From
                            until, // Until
                            repository.getMetadataFormat(),
                            set.getSetSpec(), // SetSpec
                            repositoryId,
                            oaiPmhCrossrefToken
                    );
                }
            }

            // if it is less than 100 bytes then it is probably not what we want
            if(toDate == null){
                toDate = fromDate;
            }
            if (!(new File(filesystemDAO.getMetadataPathPart(repositoryId, fromDate, toDate)).length() > 100)) {
                logger.info("Metadata of repository " + repository.getId()
                        + " is probably not done, less than 100 bytes was downloaded!");
                throw new Exception("Harvest not Successful as metadata size was less than 100 bytes.");
            } else {
                logger.info("Metadata of repository " + repository.getId()
                        + " have been downloaded.");
                if ("rioxx".equals(repository.getMetadataFormat().toLowerCase())) {
                    supervisorClient.sendRioxxComplianceRepositoryRequest(repositoryId);
                }
            }
        } catch (ResumptionTokenException ex) {
            metadataDownloadFailureIssueCallback.reportIssue(ex.getMessage(), ex.getDetails());
        }
    }

    private List<String> getListIdentifiers(String filePath) throws ParserConfigurationException, IOException, SAXException {
        File xmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
        Document doc = dbBuilder.parse(xmlFile);

        List<String> result = new ArrayList<>();

        NodeList nodeList = doc.getElementsByTagName("identifier");
        for (int i = 0; i < nodeList.getLength(); i++) {
            result.add(nodeList.item(i).getTextContent());
        }

        return result;
    }

    private String checkRepositoryUri(LegacyRepository repository) {
        String repositoryUri = new CleanUrl(repository.getUrlOaipmh()).toString();
        if (!repositoryUri.startsWith("https") && repositoryUri.startsWith("http")) {
            Duration duration = Duration.ofSeconds(10);
            RestTemplate restTemplate = new RestTemplateBuilder()
                    .setConnectTimeout(duration)
                    .setReadTimeout(duration)
                    .build();

            ResponseEntity<String> response = null;

            try {
                String testUri = repositoryUri.replace("http://", "https://");
                response = restTemplate
                        .getForEntity(testUri, String.class);
                // Test was successful
                repositoryUri = testUri;
            } catch (ResourceAccessException | HttpClientErrorException e) {
                logger.warn("Repository {} doesn't accept https. Message: {}", repository.getId(), e.getMessage());
            }

            if (null != response && response.getStatusCodeValue() / 100 != 4) {
                repository.setUrlOaipmh(repositoryUri.replace("http://", "https://"));
                repositoryDAO.updateUri(repository.getId(), repository.getUrlOaipmh());
            }
        }
        return repositoryUri;
    }

}
