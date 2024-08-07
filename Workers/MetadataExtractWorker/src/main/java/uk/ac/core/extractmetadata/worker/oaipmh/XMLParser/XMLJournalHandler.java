package uk.ac.core.extractmetadata.worker.oaipmh.XMLParser;

import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.ac.core.common.model.legacy.Journal;
import uk.ac.core.common.model.legacy.RepositoryMetadata;

/**
 * Process journal metadata.
 *
 * Supported parse methods: PARSEALL ONDEMAND
 *
 * If the XMLJournalHandler is used with any other parse method, it will be
 * processed in the same way as with the PARSEALL method.
 *
 * @author Drahomira Herrmannova <d.herrmannova@gmail.com>
 */
public class XMLJournalHandler extends DefaultHandler {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(XMLJournalHandler.class);

    /*
     * For synchronising parsing and reading
     */
    private XMLParseMethodEnum parseMethod = XMLParseMethodEnum.ONDEMAND;
    public Semaphore parserWaitSemaphore;
    public Semaphore readerWaitSemaphore;
    /*
     * For storing parsed metadata
     */
    private RepositoryMetadata repositoryMetadata = new RepositoryMetadata();
    private Journal journal;
    /*
     * For appending content of each element -- the parser sometimes splits the content
     */
    protected StringBuilder elementContent = new StringBuilder();
    /*
     * For marking position within the metadata document
     */
    private Boolean startRecord = false;
    private Boolean startTitle = false;
    private Boolean startIdentifier = false;
    private Boolean startSubject = false;
    private Boolean startLanguage = false;
    private Boolean startRights = false;
    private Boolean startPublisher = false;
    /*
     * Patterns for matching different journal identifiers
     */
    private static final Pattern URL_PATTERN = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
            + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    protected static final Pattern ISSN_PATTERN = Pattern.compile(
            "^[0-9][0-9][0-9][0-9][-][0-9][0-9][0-9][X0-9]$",
            Pattern.CASE_INSENSITIVE);

    /* ********************************************************************************************/
    /* CONSTRUCT                                                                                  */
    /* ********************************************************************************************/
    /**
     * Constructor. Sets parse method and repository to be processed.
     *
     * @param repositoryID
     * @param parseMethod
     */
    public XMLJournalHandler(Integer repositoryID, XMLParseMethodEnum parseMethod) {

        this.repositoryMetadata.setRepositoryId(repositoryID);
        this.parseMethod = parseMethod;

        if (parseMethod == XMLParseMethodEnum.ONDEMAND) {

            this.parserWaitSemaphore = new Semaphore(1);
            this.readerWaitSemaphore = new Semaphore(1);

            try {
                this.readerWaitSemaphore.acquire();
                this.parserWaitSemaphore.acquire();
            } catch (InterruptedException ex) {
                // this should never happen
                logger.error(ex.getMessage(), this.getClass());
            }
        }
    }

    /* ********************************************************************************************/
    /* SET & GET METHODS                                                                          */
    /* ********************************************************************************************/
    /**
     * Get repository metadata. It contains the list of parsed journals.
     *
     * @return
     */
    public RepositoryMetadata getRepositoryMetadata() {
        return repositoryMetadata;
    }

    /**
     *
     * @return
     */
    public Journal getJournal() throws InterruptedException {
        return journal;
    }

    /* ****************************************************************************************** */
    /* PARSE METHODS                                                                              */
    /* ****************************************************************************************** */
    /**
     *
     * @param qName
     */
    private void processRecord(String qName) {
        if (qName.equalsIgnoreCase("title") || qName.equalsIgnoreCase("dc:title")) {
            this.startTitle = true;
        }
        if (qName.equalsIgnoreCase("identifier") || qName.equalsIgnoreCase("dc:identifier")) {
            this.startIdentifier = true;
        }
        if (qName.equalsIgnoreCase("subject") || qName.equalsIgnoreCase("dc:subject")) {
            this.startSubject = true;
        }
        if (qName.equalsIgnoreCase("language") || qName.equalsIgnoreCase("dc:language")) {
            this.startLanguage = true;
        }
        if (qName.equalsIgnoreCase("rights") || qName.equalsIgnoreCase("dc:rights")) {
            this.startRights = true;
        }
        if (qName.equalsIgnoreCase("publisher") || qName.equalsIgnoreCase("dc:publisher")) {
            this.startPublisher = true;
        }
    }

    /**
     *
     * @param uri
     * @param localName
     * @param qName
     * @param attributes
     * @throws SAXException
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if ((qName.equalsIgnoreCase("record") || qName.equalsIgnoreCase("oai:record"))) {

            this.journal = new Journal();
            this.journal.setRepositoryId(this.repositoryMetadata.getRepositoryId());

            this.repositoryMetadata.incJournalsCount();

            this.startTitle
                    = this.startIdentifier
                    = this.startSubject
                    = this.startLanguage
                    = this.startRights
                    = this.startPublisher
                    = false;

            this.startRecord = true;
        }

        if (this.startRecord) {
            this.processRecord(qName);
        }
    }

    /**
     *
     * @param uri
     * @param localName
     * @param qName
     * @throws SAXException
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        this.processElementContent();

        if (qName.equalsIgnoreCase("record") || qName.equalsIgnoreCase("oai:record")) {

            this.startRecord = false;

            if (parseMethod == XMLParseMethodEnum.ONDEMAND) {

                try {
                    // the reader may read journal now
                    readerWaitSemaphore.release();

                    // wait until the reader reads it
                    parserWaitSemaphore.acquire();

                    // to make sure the data won't be rewritten by this thread
                    this.journal = null;

                } catch (InterruptedException ex) {
                    logger.error("XML parser interrupted: " + ex.getMessage(), this.getClass());
                    this.journal = null;
                    parserWaitSemaphore.release();
                }
            } else {
                this.repositoryMetadata.addJournal(this.journal);
                this.journal = null;
            }
        }
    }

    /**
     * Saves content into private variable (composes chunks)
     *
     * @param ch characters inside of the element
     * @param start
     * @param length
     * @throws SAXException
     */
    @Override
    public void characters(char ch[], int start, int length) throws SAXException {

        String content = new String(ch, start, length);

        this.elementContent.append(content);
    }

    /**
     * Stores content
     */
    protected void processElementContent() {

        String content = this.elementContent.toString().trim();

        if (content == null || content.equals("")) {
            return;
        }

        if (this.journal != null) {
            if (this.startTitle) {
                this.journal.setTitle(content);
                this.startTitle = false;
            }
            if (this.startIdentifier) {
                this.journal.addIdentifier(XMLJournalHandler.parseIdentifier(content));
                this.startIdentifier = false;
            }
            if (this.startSubject) {
                this.journal.addSubject(content);
                this.startSubject = false;
            }
            if (this.startLanguage) {
                this.journal.setLanguage(content);
                this.startLanguage = false;
            }
            if (this.startRights) {
                this.journal.setRights(content);
                this.startRights = false;
            }
            if (this.startPublisher) {
                this.journal.setPublisher(content);
                this.startPublisher = false;
            }
        }

        this.elementContent.setLength(0);
    }

    /**
     * Prepends the identifier type (issn or url) in front of the identifier
     * string.
     *
     * @param content
     * @return
     */
    protected static String parseIdentifier(String content) {

        Matcher issnMatcher = XMLJournalHandler.ISSN_PATTERN.matcher(content);
        if (issnMatcher.find()) {
            StringBuilder issn = new StringBuilder("issn:");
            issn.append(issnMatcher.group());
            return issn.toString();
        }

        Matcher urlMatcher = XMLJournalHandler.URL_PATTERN.matcher(content);
        if (urlMatcher.find()) {
            StringBuilder url = new StringBuilder("url:");
            url.append(urlMatcher.group());
            return url.toString();
        }

        return content;
    }

    /* ****************************************************************************************** */
    /* EOF                                                                                        */
    /* ****************************************************************************************** */
}
