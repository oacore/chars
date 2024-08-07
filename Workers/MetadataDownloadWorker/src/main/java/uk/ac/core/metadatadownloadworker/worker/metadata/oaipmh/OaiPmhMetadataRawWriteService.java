package uk.ac.core.metadatadownloadworker.worker.metadata.oaipmh;

import org.xml.sax.SAXException;
import uk.ac.core.metadatadownloadworker.exception.MetadataDownloadException;
import uk.ac.core.metadatadownloadworker.exception.OAIPMHEndpointException;
import uk.ac.core.metadatadownloadworker.exception.ResumptionTokenException;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public interface OaiPmhMetadataRawWriteService {
    void identify() throws IOException,
            ParserConfigurationException, SAXException, TransformerException, OAIPMHEndpointException;

    void listMetadataFormats() throws IOException,
                    ParserConfigurationException, SAXException, TransformerException;

    void listSets(Integer repositoryId) throws IOException,
            ParserConfigurationException, SAXException, TransformerException, ResumptionTokenException;

    void listRecords(String from, String until, String metadataPrefix,
                     String setSpec, Integer repositoryId, String token) throws InterruptedException, IOException,
            ParserConfigurationException, SAXException, TransformerException, NoSuchFieldException, ResumptionTokenException, MetadataDownloadException;

    void listIdentifiers(String from, String until, String metadataPrefix,
                         String setSpec, OutputStream overridenOut, Integer repositoryId) throws InterruptedException, IOException,
                                            ParserConfigurationException, SAXException, TransformerException, NoSuchFieldException, ResumptionTokenException, MetadataDownloadException;

    void GetAllRecordsByGetRecord(String baseURL, String metadataPrefix, String identifier, OutputStream out) throws IOException,
                                                    ParserConfigurationException, SAXException, TransformerException;
}
