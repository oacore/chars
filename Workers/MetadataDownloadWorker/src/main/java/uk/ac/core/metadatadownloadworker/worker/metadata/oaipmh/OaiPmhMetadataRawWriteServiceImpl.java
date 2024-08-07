package uk.ac.core.metadatadownloadworker.worker.metadata.oaipmh;

import ORG.oclc.oai.harvester2.verb.GetRecord;
import ORG.oclc.oai.harvester2.verb.Identify;
import ORG.oclc.oai.harvester2.verb.ListIdentifiers;
import ORG.oclc.oai.harvester2.verb.ListMetadataFormats;
import ORG.oclc.oai.harvester2.verb.ListRecords;
import ORG.oclc.oai.harvester2.verb.ListSets;
import org.apache.xpath.objects.XObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.ac.core.metadatadownloadworker.exception.MetadataDownloadException;
import uk.ac.core.metadatadownloadworker.exception.OAIPMHEndpointException;
import uk.ac.core.metadatadownloadworker.exception.ResumptionTokenException;
import uk.ac.core.worker.WorkerStatus;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class OaiPmhMetadataRawWriteServiceImpl implements OaiPmhMetadataRawWriteService {

    private final Logger logger = LoggerFactory.getLogger(OaiPmhMetadataRawWriteServiceImpl.class);

    private final WorkerStatus workerStatus;
    private final OutputStream out;
    private final String baseURL;

    private static final String RESUMPTION_TOKEN_LOOP_ISSUE_MSG = "Resumption Token (%s) has already been requested on %s Verb.\n" +
            "Stopping the download for that Verb\n" +
            "Harvesting loop detected.\n" +
            "Repository ID: %d";

    public OaiPmhMetadataRawWriteServiceImpl(WorkerStatus workerStatus, OutputStream out, String baseURL) {
        this.workerStatus = workerStatus;
        this.out = out;
        this.baseURL = baseURL;
    }

    private enum Verb {
        LIST_SETS("ListSets"), LIST_RECORD("ListRecord"), LIST_IDENTIFIERS("ListIdentifiers");

        private final String name;

        Verb(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    @Override
    public void identify() throws OAIPMHEndpointException {
        try {
            Identify identify = operationRetry(
                    () -> {
                        try {
                            return new Identify(baseURL);
                        } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
                            logger.error("Error",e);
                        }
                        return null;
                    },
                    e -> {},
                    5
            );
            String repositoryName = getRepositoryName(identify);
            if(repositoryName == null || repositoryName.isEmpty()) {
                throw new OAIPMHEndpointException();
            } else {
                logger.info("Harvesting " + repositoryName);
            }

            out.write(identify.toString().getBytes(StandardCharsets.UTF_8));
            out.write("\n".getBytes(StandardCharsets.UTF_8));
            logger.info("Harvesting Repository " + identify.getRequestURL());
        } catch (Exception e) {
            throw new OAIPMHEndpointException(e);
        }
    }

    private String getRepositoryName(Identify identify) {
        NodeList tagList = identify.getDocument().getElementsByTagName("repositoryName");
        if(tagList == null || tagList.getLength() < 1) {
            return "";
        }

        return tagList.item(0).getTextContent();
    }
    
    @Override
    public void listMetadataFormats() throws IOException,
            ParserConfigurationException, SAXException, TransformerException {

        // Get Metadata Formats
        ListMetadataFormats lmf = new ListMetadataFormats(baseURL);
        out.write(lmf.toString().getBytes(StandardCharsets.UTF_8));
        out.write("\n".getBytes(StandardCharsets.UTF_8));
    }
        
    @Override
    public void listSets(Integer repositoryId) throws IOException,
            ParserConfigurationException, SAXException, TransformerException, ResumptionTokenException {
    
        // Store all Resumption Tokens in this List. If we get a repeat, we should stop downloading.
        List<String> listSetsRTList = new ArrayList<>();
        ListSets listSets = operationRetry(
                () -> {
                    try {
                        return new ListSets(baseURL);
                    } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
                        logger.error("Error",e);
                    }
                    return null;
                },
                e -> {},
                5
        );
        while (listSets != null) {
            out.write(listSets.toString().getBytes(StandardCharsets.UTF_8));
            out.write("\n".getBytes(StandardCharsets.UTF_8));
            String resumptionToken = "";
            try {
                resumptionToken = listSets.getResumptionToken();
            } catch (NoSuchFieldException e) {
                logger.info("Website has only one page and doesn't have a resumption token");
            }
            if (resumptionToken == null || resumptionToken.length() == 0) {
                listSets = null;
            } else {
                // Check if we have already retrived the ResumptionToken
                // If we have, stop getting the sets and continue
                if (listSetsRTList.contains(resumptionToken)) {
                    String issueMessage = createResumptionTokenIssueMessage(resumptionToken, repositoryId, Verb.LIST_SETS.getName());
                    logger.error(issueMessage);
                    throw new ResumptionTokenException(Verb.LIST_SETS.getName());
                }

                // Checks if the resumptionToken is already URLEncoded
                // If the url is different after 
                String decodedResumptionToken = URLDecoder.decode(resumptionToken, "UTF-8");
                if (!resumptionToken.equals(decodedResumptionToken)) {
                    // If they are not then the resumptionToken is already encoded.
                    // Decode so that OAI library can encode it correctly.
                    resumptionToken = URLDecoder.decode(resumptionToken, "UTF-8");
                }

                // If not, then add the resumptionToken to the list and get the next page
                listSetsRTList.add(resumptionToken);
                // Use custom class to get ListSets with Resumption Token
                String finalResumptionToken = resumptionToken;
                listSets = operationRetry(
                        () -> {
                            try {
                                return new ListSetsWithResumptionToken(baseURL, finalResumptionToken);
                            } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
                                logger.error("Error",e);
                            }
                            return null;
                        },
                        e -> {},
                        5
                );
                try {
                    long delay = 500L;
                    logger.info(String.format("Sleeping for ms " + delay));
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {}

            }
        }
    }

        
    @Override
    public void listRecords(String from, String until, String metadataPrefix,
                            String setSpec, Integer repositoryId, String oaiPmhCrossrefToken) throws InterruptedException, IOException,
            ParserConfigurationException, SAXException, TransformerException, ResumptionTokenException, MetadataDownloadException {

        /**
         * *** Download Records ****
         */
        HashMap<String, String> additionalHeaders = new HashMap<>();
        if(repositoryId != null && repositoryId == 4786){
            additionalHeaders.put("Crossref-Plus-API-Token", "Bearer " + oaiPmhCrossrefToken);
        }
        ListRecords listRecords = new ListRecords(baseURL, from, until, setSpec, metadataPrefix, additionalHeaders);

        // Store all Resumption Tokens in this List. If we get a repeat, we should stop downloading.
        List<String> RTList = new ArrayList<>();
        while (listRecords != null) {

            logger.info("Request URL: " + listRecords.getRequestURL());

            if (Thread.interrupted()) {
                logger.error("The thread has been interrupted during harvesting.");
                throw new InterruptedException(Thread.currentThread().toString());
            }

            NodeList errors = listRecords.getErrors();
            if (errors != null && errors.getLength() > 0) {
                if(listRecords.toString().contains("<error code=\"noRecordsMatch\">")) {
                    logger.info("No data to process");
                    return;
                } else {
                    String errorMessage = String.format("Error record in listRecords: %s", listRecords.toString());
                    logger.error(errorMessage);
                    throw new MetadataDownloadException(errorMessage);
                }

           }

            out.write(listRecords.toString().getBytes(StandardCharsets.UTF_8));
            out.write("\n".getBytes(StandardCharsets.UTF_8));
            String resumptionToken = null;

            try {
                resumptionToken = listRecords.getResumptionToken();
            } catch (NoSuchFieldException e) {
                logger.info("Website has only one page and doesn't have a resumption token");
            }

            if (resumptionToken == null || resumptionToken.length() == 0) {
                listRecords = null;
            } else {
                //logger.info("resumptionToken: " + resumptionToken);
                // Check if we have already retrived the ResumptionToken
                // If we have, stop getting the sets and continue
                if (RTList.contains(resumptionToken)) {
                    String issueMessage = createResumptionTokenIssueMessage(resumptionToken, repositoryId, Verb.LIST_RECORD.getName());
                    logger.error(issueMessage);
                    throw new ResumptionTokenException(Verb.LIST_RECORD.getName());
                }
                // If not, then add the resumptionToken to the list and get the next page
                RTList.add(resumptionToken);
                String finalResumptionToken = resumptionToken;
                listRecords = operationRetry(
                        () -> {
                            try {
                                return new ListRecords(baseURL, finalResumptionToken, additionalHeaders);
                            } catch (Throwable e) {
                                throw new RuntimeException(e);
                            }
                        },
                        e -> {},
                        5);
            }
            workerStatus.getTaskStatus().incProcessed();
            workerStatus.getTaskStatus().incSuccessful();

            try {
                long delay = 500L;
                logger.info(String.format("Sleeping for ms " + delay));
                Thread.sleep(delay);
            } catch (InterruptedException ex) {}
        }
    }
    
    @Override
    public void listIdentifiers(String from, String until, String metadataPrefix,
                                String setSpec, OutputStream overridenOut, Integer repositoryId) throws InterruptedException, IOException,
            ParserConfigurationException, SAXException, TransformerException, NoSuchFieldException, ResumptionTokenException, MetadataDownloadException {

        /**
         * *** Download Records ****
         */
        ListIdentifiers listRecords = new ListIdentifiers(baseURL, from, until, setSpec, metadataPrefix);

        // Store all Resumption Tokens in this List. If we get a repeat, we should stop downloading.
        List<String> RTList = new ArrayList<>();
        while (listRecords != null) {

            logger.info("Request URL: " + listRecords.getRequestURL());

            if (Thread.interrupted()) {
                logger.error("The thread has been interrupted during harvesting.");
                throw new InterruptedException(Thread.currentThread().toString());
            }

            NodeList errors = listRecords.getErrors();
            if (errors != null && errors.getLength() > 0) {
                String errorMessage = String.format("Error record in listIdentifiers: %s", listRecords.toString());
                logger.error(errorMessage);
                throw new MetadataDownloadException(errorMessage);
            }

            overridenOut.write(listRecords.toString().getBytes(StandardCharsets.UTF_8));
            overridenOut.write("\n".getBytes(StandardCharsets.UTF_8));
            String resumptionToken = listRecords.getResumptionToken();

            //logger.info("resumptionToken: " + resumptionToken);

            if (resumptionToken == null || resumptionToken.length() == 0) {
                listRecords = null;
            } else {
                // Check if we have already retrived the ResumptionToken
                // If we have, stop getting the sets and continue
                if (RTList.contains(resumptionToken)) {
                    String issueMessage = createResumptionTokenIssueMessage(resumptionToken, repositoryId, Verb.LIST_IDENTIFIERS.getName());
                    logger.error(issueMessage);
                    throw new ResumptionTokenException(issueMessage);
                }
                // If not, then add the resumptionToken to the list and get the next page
                RTList.add(resumptionToken);
                listRecords = operationRetry(
                        () -> {
                            try {
                                return new ListIdentifiers(baseURL, resumptionToken);
                            } catch (Throwable e) {throw new RuntimeException(e);}
                        },
                        (e) -> {}, 5);
            }
            workerStatus.getTaskStatus().incProcessed();
            workerStatus.getTaskStatus().incSuccessful();

            try {
                long delay = 500L;
                logger.info(String.format("Sleeping for %d ms ", delay));
                Thread.sleep(delay);
            } catch (InterruptedException ex) {}
        }
    }


    private String createResumptionTokenIssueMessage(String resumptionToken, int repositoryId, String verb) {
        return String.format(RESUMPTION_TOKEN_LOOP_ISSUE_MSG,
                resumptionToken, verb, repositoryId);
    }


    @Override
    public void GetAllRecordsByGetRecord(String baseURL, String metadataPrefix, String identifier, OutputStream out) throws IOException,
            ParserConfigurationException, SAXException, TransformerException {
        GetRecord getRecord = new GetRecord(baseURL, identifier, metadataPrefix);
        out.write(getRecord.toString().getBytes(StandardCharsets.UTF_8));
        out.write("\n".getBytes(StandardCharsets.UTF_8));
    }

    private static <T> T operationRetry(Supplier<T> supplier, Consumer<Throwable> onError, int retryCount) throws IOException {
        int count = 0;
        long millis = 300000;
        double coefficient = 1.75;

        while (count++ < retryCount) {
            try {
                return supplier.get();
            } catch (Throwable e) {
                onError.accept(e);
                if (count == retryCount) {
                    throw new IOException("Attempted request " + retryCount + " times. Aborting", e);
                }
            }
            try {
                Thread.sleep(millis);
                millis *= coefficient;
            } catch (InterruptedException ignored) {}
        }
        throw new IllegalArgumentException("Can't receive result");
    }


}
