package uk.ac.core.extractmetadata.worker.oaipmh.XMLParser;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.ac.core.database.service.repositories.RepositoryMetadataFormatsDAO;
import uk.ac.core.extractmetadata.worker.oaipmh.metadataformats.MetadataFormatPersist;
import uk.ac.core.extractmetadata.worker.oaipmh.metadataformats.MetadataFormatSaxHandler;
import uk.ac.core.filesystem.services.FilesystemDAO;

/**
 * Configures and then parses a Repositories XML for metadata formats
 *
 * @author Samuel Pearce <samuel.pearce@open.ac.uk>
 */
@Service
public class MetadataFormatsParser {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MetadataFormatsParser.class);

    @Autowired
    private RepositoryMetadataFormatsDAO metadataFormatsDAO;

    @Autowired
    private FilesystemDAO filesystemDAO;

    public MetadataFormatsParser() {
    }

    public void updateMetadataToDatabase(Integer repositoryId, Date fromDate, Date untilDate) throws ParserConfigurationException, SAXException, IOException {
        logger.debug("Updating metadataFormats to database", this.getClass());

        File inputFile = new File(this.filesystemDAO.getMetadataPath(repositoryId, fromDate, untilDate));
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        // Remove all metadata formats stored for this repository
        // When we process them, we we will readd them again...
        metadataFormatsDAO.removeAllMetadataFormats(repositoryId);
        MetadataFormatPersist metadataFormatPersist = new MetadataFormatPersist(repositoryId, metadataFormatsDAO);

        DefaultHandler userhandler = new MetadataFormatSaxHandler(metadataFormatPersist);
        saxParser.parse(inputFile, userhandler);

    }
}
