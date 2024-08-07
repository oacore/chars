package uk.ac.core.extractmetadata.worker.extractMetadataService;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.ac.core.extractmetadata.worker.oaipmh.GetRecords.ArticleMetadataPersistFactory;
import uk.ac.core.extractmetadata.worker.oaipmh.XMLParser.MetadataFormatsParser;
import uk.ac.core.extractmetadata.worker.oaipmh.XMLParser.ProgressInputStream;
import uk.ac.core.extractmetadata.worker.taskitem.ExtractMetadataTaskItemStatus;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.worker.WorkerStatus;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Stream;

/**
 * Class to parse and check downloaded metadata.
 *
 */
@Service
public class ArticleMetadataExtractMetadataService implements ExtractMetadataService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ArticleMetadataExtractMetadataService.class);

    private ArticleMetadataPersistFactory articleMetadataPersistFactory;
    private FilesystemDAO filesystemDAO;
    private WorkerStatus workerStatus;
    private MetadataFormatsParser metadataFormatsParser;

    private Integer repositoryId;
    private Date fromDate;
    private Date untilDate;
    private DefaultHandler handler;

    @Autowired
    public ArticleMetadataExtractMetadataService(ArticleMetadataPersistFactory articleMetadataPersistFactory, FilesystemDAO filesystemDAO, WorkerStatus workerStatus, MetadataFormatsParser metadataFormatsParser) {
        this.articleMetadataPersistFactory = articleMetadataPersistFactory;
        this.filesystemDAO = filesystemDAO;
        this.workerStatus = workerStatus;
        this.metadataFormatsParser = metadataFormatsParser;
    }

    public ArticleMetadataExtractMetadataService init(DefaultHandler userHandler,
                     Integer repositoryId, Date fromDate, Date untilDate) {
        this.repositoryId = repositoryId;
        this.fromDate = fromDate;
        this.untilDate = untilDate;
        this.handler = userHandler;
        return this;
    }

    @Override
    public void run() {

        try {
            String inputFile = filesystemDAO.getMetadataPath(repositoryId, fromDate, untilDate);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            ExtractMetadataTaskItemStatus extractMetadataTaskStatus = (ExtractMetadataTaskItemStatus) workerStatus.getTaskStatus();

            try (Stream<Path> filePathStream = Files.walk(Paths.get(inputFile))) {
                filePathStream.forEach(filePath -> {
                    if (Files.isRegularFile(filePath)) {
                        try {
                            saxParser.parse(new ProgressInputStream(new FileInputStream(filePath.toString()), filePath.toFile().length()), handler);
                        } catch (SAXException e) {
                            logger.warn(e.getMessage(), e);
                        } catch (IOException e) {
                            logger.warn(e.getMessage(), e);
                        }
                    }
                });
            }

            // delete residue records in database ONLY if is a full harvesting,
            // in case of incremental this does not apply
            if (fromDate == null) {
                // TODO: delete documents in database which are not in metadata when full harvesting

                // IF NOT CROSSREF
                if (repositoryId != 4786) {
                    throw new RuntimeException("ArticleMetadataExtractMetadataService is not fully implemented to support normal metadata extract repositories. See class for details");
                }
            }

        } catch (ParserConfigurationException | IOException | SAXException ex) {
            logger.error(ex.getMessage(), ex);
        }

        try {
            // Extract MetadataFormats and store in database
            metadataFormatsParser.updateMetadataToDatabase(repositoryId, fromDate, untilDate);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

}
