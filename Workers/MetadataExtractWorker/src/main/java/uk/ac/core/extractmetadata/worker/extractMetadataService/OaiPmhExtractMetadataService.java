package uk.ac.core.extractmetadata.worker.extractMetadataService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.ac.core.database.service.updates.TaskUpdatesDAO;
import uk.ac.core.extractmetadata.worker.oaipmh.GetRecords.ArticleMetadataPersist;
import uk.ac.core.extractmetadata.worker.oaipmh.SAXReaderCorpus;
import uk.ac.core.extractmetadata.worker.oaipmh.XMLParser.*;
import uk.ac.core.filesystem.services.FilesystemDAO;
import java.io.File;

/**
 * Class to parse and check downloaded metadata.
 *
 * @author Tomas Korec, dh8835
 */
@Service
public class OaiPmhExtractMetadataService implements ExtractMetadataService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(OaiPmhExtractMetadataService.class);

    @Autowired
    private TaskUpdatesDAO rum;
    @Autowired
    private FilesystemDAO filesystemDAO;
    @Autowired
    private XMLMetadataParser xmlMetadataParser;
    @Autowired
    private MetadataFormatsParser metadataFormatsParser;


    private ArticleMetadataPersist persist;
    private Integer repositoryId;
    private Date fromDate;
    private Date untilDate;

    public OaiPmhExtractMetadataService() {
    }

    public OaiPmhExtractMetadataService init(ArticleMetadataPersist persist, Integer repositoryId, Date fromDate, Date untilDate) {
        this.persist = persist;
        this.repositoryId = repositoryId;
        this.fromDate = fromDate;
        this.untilDate = untilDate;
        return this;
    }

    /**
     * Main method of class runs all necessary methods.
     *
     * @return success flag
     */
    @Override
    public void run() {

        xmlMetadataParser.init(persist);
        SAXReaderCorpus corpus = null;
        try {
            corpus = getMetadataCorpus(new File(filesystemDAO.getMetadataPath(repositoryId, fromDate, untilDate)));

            xmlMetadataParser.initStats();

            xmlMetadataParser.updateMetadataToDatabase(corpus, repositoryId, fromDate, untilDate);

            // delete residue records in database ONLY if is a full harvesting,
            // in case of incremental this does not apply
            if (fromDate == null && untilDate == null) {
                xmlMetadataParser.deleteRemovedArticlesFromDatabase();
            }

        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }

        try {
            // Extract MetadataFormats and store in database
            metadataFormatsParser.updateMetadataToDatabase(repositoryId, fromDate, untilDate);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

        //and update the statics table of the repository
        // TODO statistics
        //Statistics.getInstance().updateStatistics(ActionType.METADATA_EXTRACT, repositoryId);
    }

    /**
     * Method returns metadata corpus.
     *
     * @return parsed metadata
     */
    protected SAXReaderCorpus getMetadataCorpus(File xmlFile) throws FileNotFoundException {
        if (!xmlFile.exists()) {
            throw new FileNotFoundException(xmlFile.toString());
        }
        SAXReaderCorpus corpus = new SAXReaderCorpus();
        XMLRepositoryMetadataReaderSAX rmr = new XMLRepositoryMetadataReaderSAX(
                repositoryId, xmlFile.getAbsolutePath(), XMLParseMethodEnum.ONDEMAND, false);
        corpus.setXMLreader(rmr);

        return corpus;
    }
}
