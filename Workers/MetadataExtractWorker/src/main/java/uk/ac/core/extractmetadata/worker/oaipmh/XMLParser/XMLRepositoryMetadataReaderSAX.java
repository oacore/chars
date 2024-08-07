package uk.ac.core.extractmetadata.worker.oaipmh.XMLParser;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;
import uk.ac.core.common.model.legacy.RepositoryMetadata;
import uk.ac.core.filesystem.services.FilesystemDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * Class will load a given XML file and parse it using XMLDocumentHandler.
 *
 * @author gp3237, dh8835, mk6353
 */
public class XMLRepositoryMetadataReaderSAX extends Thread {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(XMLRepositoryMetadataReaderSAX.class);

    protected Integer repositoryID;
    protected String xmlFile;
    protected XMLParseMethodEnum XMLParseMethod = XMLParseMethodEnum.PARSEALL;
    protected XMLDocumentHandler handler;
    /** Size of test collection. This number is used if XMLParseMethod is set to test_collection. */
    private Integer testSetSize = 2500;
    private boolean success = true;

    private ProgressInputStream progressInputStream;

    @Autowired
    private FilesystemDAO filesystemDAO;

    @Deprecated
    public XMLRepositoryMetadataReaderSAX() {
    }

    public XMLRepositoryMetadataReaderSAX(Integer repositoryID, String xmlFile,
            XMLParseMethodEnum XMLParseMethod) {
        this(repositoryID, xmlFile, XMLParseMethod, true);
    }

    public XMLRepositoryMetadataReaderSAX(Integer repositoryID, String xmlFile,
            XMLParseMethodEnum XMLParseMethod, boolean throwAwayNoURL) {
        this.repositoryID = repositoryID;
        this.xmlFile = xmlFile;
        this.XMLParseMethod = XMLParseMethod;

        handler = new XMLDocumentHandler(repositoryID, XMLParseMethod);
        if (this.XMLParseMethod == XMLParseMethodEnum.ONDEMAND_TEST
                || this.XMLParseMethod == XMLParseMethodEnum.PARSEALL_TEST) {
            handler.setTestSetSize(this.testSetSize);
        }
    }

    /**
     * Get list of all document classes parsed from metadata.
     *
     * @return
     */
    public List<String> getDocClasses() {
        return this.handler.getDocClassList();
    }

    /**
     * Get document classes with occurrence statistics.
     *
     * @return
     */
    public Map<String, Integer> getDocClassCounts() {
        return this.handler.getDocClassCounts();
    }

    /**
     * Set size of test collection.
     *
     * @param size
     */
    public void setTestSetSize(Integer size) {
        this.testSetSize = size;
        if (handler != null) {
            handler.setTestSetSize(this.testSetSize);
        }
    }

    /**
     * Get size of test collection.
     *
     * @return
     */
    public Integer getTestSetSize() {
        return this.testSetSize;
    }
    
    /**
     * Get metadata tags with the DOI occurrence counts.
     * @return 
     */
    public Map<String, Integer> getDoiCounts() {
        return this.handler.getDoiCount();
    }

    /**
     * Returns the handler of the SAX parser.
     *
     * @return
     */
    public XMLDocumentHandler getXMLDocumentHandler() {
        return handler;
    }

    public RepositoryMetadata load() {

        this.handler.setParseMethod(this.XMLParseMethod);
        this.parse();

        RepositoryMetadata rm = handler.getRepositoryMetadata();

        return rm;
    }

    public float getPercentage() {
        return progressInputStream.getPercentage();
    }

    public long getMaxNumBytes() {
        return progressInputStream.getMaxNumBytes();
    }

    public Long getTotalNumBytesRead(){
        return progressInputStream.getTotalNumBytesRead();
    }

    @Override
    public void run() {
        this.parse();
    }

    public Integer getRepositoryID() {
        return repositoryID;
    }

    protected void parse() {

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            File file = new File(xmlFile);
            progressInputStream = new ProgressInputStream(new FileInputStream(file), file.length());

            saxParser.parse(progressInputStream, handler);

            logger.debug("Successfully parsed: " + xmlFile, this.getClass());

        } catch (ParserConfigurationException | SAXException e) {
            logger.error(e.getMessage(), e);
            logger.debug("Incomplete xmlFile: " + xmlFile + e.getMessage(), this.getClass());
            success = false;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            logger.debug("Incomplete xmlFile: " + xmlFile + e.getMessage(), this.getClass());
            success = false;
        } finally {
            
            if (!success){
                logger.debug("Parsing failed. {}", this.getClass());
                this.interrupt();
            }
            
            if (handler.readerWaitSemaphore != null) {
                handler.readerWaitSemaphore.release();
            }
            if (handler.parserWaitSemaphore != null) {
                handler.parserWaitSemaphore.release();
            }
        }


    }

}
