package uk.ac.core.extractmetadata.worker.oaipmh.XMLParser;

import com.google.common.collect.Multimap;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.common.model.legacy.ArticleMetadata;
import uk.ac.core.common.model.legacy.RepositoryMetadata;
import uk.ac.core.common.util.TextToDateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class will process the downloaded metadata file -- parse it and create new
 * class for each document found in the metadata.
 *
 * @author gp3237, dh8835, mk6353
 */
public class XMLDocumentHandler extends DefaultHandler implements IDocumentHandler {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(XMLDocumentHandler.class);

    /* Attributes for parsing document classes. ************************************************** */
    /**
     * List of document classes acquired from metadata "listSets" section.
     */
    private List<String> classList = new ArrayList<String>();
    /**
     * Map of document classes with count of documents in each class.
     */
    private Map<String, Integer> classCounts = new HashMap<String, Integer>();
    /**
     * For marking the start of a record
     */
    private boolean startRecord = false;
    private StringBuilder rawRecord = new StringBuilder();
    /**
     * For marking start of "listSets" tag.
     */
    private boolean startListSets = false;
    /**
     * For marking start of "setSpec" within "listSets" tag.
     */
    private boolean startSetSpec = false;
    /**
     * For marking start of "setSpec" tag.
     */
    private boolean startSet = false;
    /**
     * For marking start of "dc:subject" tag.
     */
    private boolean startSubject = false;
    /**
     * How many documents should be retrieved.
     */
    private Integer testSetSize = 1500;

    /* DOI statistics *************************************************************************** */
    /**
     * Map of tags appearing in the metadata with count of DOIs found in each
     * tag.
     */
    private Map<String, Integer> doiCounts = new HashMap<String, Integer>();

    /**
     * *******************************************************************************************
     */
    // content of element (parsers may split data into several chunks)
    protected StringBuilder elementContent = new StringBuilder();
    /**
     * String name of the repository.
     */
    protected boolean startRepositoryName = false;
    /**
     * URL of the repository where the documents can be accessed.
     */
    protected boolean startBaseURL = false;
    /**
     * Document title.
     */
    protected boolean startTitle = false;
    /**
     * Document author(s).
     */
    protected boolean startAuthor = false;
    /**
     * Document contributor(s).
     */
    protected boolean startcontributor = false;
    /**
     * Document publisher.
     */
    protected boolean startPublisher = false;
    /**
     * Document publish date.
     */
    protected boolean startDate = false;
    /**
     * Type of the document -- e.g. Journal Article.
     */
    protected boolean startType = false;
    /**
     * Topic or subject of document.
     */
    protected boolean startTopic = false;
    /**
     * Document OAI (Open Archives Initiative) identifier.
     */
    protected boolean startIdentifier = false;
    /**
     * PDF Url Identifier
     */
    protected boolean startPdfUrl = false;
    /**
     * Document abstract.
     */
    protected boolean startDescription = false;
    /**
     * Relation to other sources.
     */
    protected boolean startRelation = false;
    /**
     * Digital object identifier.
     */
    protected boolean startDoi = false;
    /**
     * Unnormalised language of document
     */
    protected boolean startLanguage = false;
    /**
     * Journal identifier
     */
    protected boolean startJournalIdentifier = false;
    /**
     * Source url of metadata record
     */
    protected boolean startSourceUrl = false;
    /**
     * Mark if some document was deleted.
     */

    protected boolean startLicense = false;
    protected boolean deleted = false;
    /**
     * Information about the currently processed repository.
     */
    protected RepositoryMetadata repositoryMetadata = new RepositoryMetadata();
    /**
     * Class holding the information about the current parsed document.
     */
    protected ArticleMetadata articleMetadata = null; //new ArticleMetadata(repositoryMetadata);
    /**
     * Variable for measuring time taken by the parser.
     */
    protected long time;
    /**
     * Metadata element which is currently being processed.
     */
    protected String currentElement = "";

    protected XMLParseMethodEnum parseMethod = XMLParseMethodEnum.PARSEALL;
    public Semaphore parserWaitSemaphore;
    public Semaphore readerWaitSemaphore;
    private static final Pattern urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
            + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    public static final String GENERIC_EPRINTS_OAI = "oai:generic.eprints.org:";
    private boolean startHeader = false;
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat datetimeformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private boolean startDateStamp = false;

    /**
     * Constructor for the class. Sets the repository to be processed.
     *
     * @param repositoryID
     * @param parseMethod
     */
    public XMLDocumentHandler(Integer repositoryID, XMLParseMethodEnum parseMethod) {
        this.time = System.currentTimeMillis();
        this.repositoryMetadata.setRepositoryId(repositoryID);
        this.parseMethod = parseMethod;

        if (parseMethod == XMLParseMethodEnum.ONDEMAND
                || parseMethod == XMLParseMethodEnum.ONDEMAND_TEST) {

            parserWaitSemaphore = new Semaphore(1);
            readerWaitSemaphore = new Semaphore(1);
            try {
                readerWaitSemaphore.acquire();
                parserWaitSemaphore.acquire();
            } catch (InterruptedException ex) {
                // this should never happen
                logger.error(ex.toString(), this.getClass());
            }
        }

    }

    /**
     * Return map of document classes found in metadata and count of documents
     * in each of the classes.
     *
     * @return
     */
    public Map<String, Integer> getDocClassCounts() {
        return classCounts;
    }

    /**
     * Return list of all document classes in metadata.
     *
     * @return
     */
    public List<String> getDocClassList() {
        return classList;
    }

    /**
     * Set required size of test collection. Parser will only process and return
     * this number of documents.
     *
     * @param testSetSize
     */
    public void setTestSetSize(Integer testSetSize) {
        this.testSetSize = testSetSize;
    }

    /**
     * Get size of test collection.
     *
     * @return
     */
    public Integer getTestSetSize() {
        return testSetSize;
    }

    /**
     * Get DOI occurrence counts for all metadata tags.
     *
     * @return
     */
    public Map<String, Integer> getDoiCount() {
        return doiCounts;
    }

    /**
     * Get the repository metadata class.
     *
     * @return
     */
    public RepositoryMetadata getRepositoryMetadata() {
        return this.repositoryMetadata;
    }

    /**
     * Get metadata of currently processed article (record).
     *
     * @return
     */
    @Override
    public ArticleMetadata getArticleMetadata() {
        return articleMetadata;
    }

    /**
     *
     * @param parseMethod
     */
    public void setParseMethod(XMLParseMethodEnum parseMethod) {
        this.parseMethod = parseMethod;
    }

    /**
     * Try to increase docClass count. If testOnly attribute is set to true,
     * method will increase docClass count only if this number is still less
     * then required document count.
     *
     * That means -- if testOnly is set to true, we want this method to retrieve
     * maximum of testSetSize documents. These documents should be spread evenly
     * in all document classes. That's why we want only maximum testSetSize /
     * numberOfSubjects documents.
     *
     * @param docClass
     * @return
     */
    private boolean incClassCount(String docClass) {
        if (this.parseMethod == XMLParseMethodEnum.ONDEMAND_TEST
                || this.parseMethod == XMLParseMethodEnum.PARSEALL_TEST) {
            return this.incClassCount(docClass, true);
        } else {
            return this.incClassCount(docClass, false);
        }
    }

    /**
     * Try to increase subjects count. If testOnly attribute is set to false,
     * method will increase subjects count only if this number is still less
     * then required document count.
     *
     * That means -- if testOnly is set to true, we want this method to retrieve
     * maximum of testSetSize documents. These documents should be spread evenly
     * in all docClass classes. That's why we want only maximum testSetSize /
     * numberOfSubjects documents.
     *
     * @param docClass
     * @param testOnly
     * @return
     */
    private boolean incClassCount(String docClass, boolean testOnly) {

        if (docClass != null && !docClass.isEmpty()
                && this.classList.contains(docClass)) {
            Integer value = this.classCounts.get(docClass);
            if (value == null) {
                this.classCounts.put(docClass, 1);
                return true;
            } else {
                if ((testOnly && value < this.testSetSize / this.classList.size())
                        || !testOnly) {
                    this.classCounts.put(docClass, value + 1);
                    return true;
                } else {
                    return false;
                }
            }
        }

        return this.parseMethod != XMLParseMethodEnum.ONDEMAND_TEST
                && this.parseMethod != XMLParseMethodEnum.PARSEALL_TEST;
    }

    /**
     * Increase DOI counter for given tag.
     *
     * @param tag
     */
    private void incDoiCount(String tag) {
        Integer count = this.doiCounts.get(tag);
        if (count == null) {
            this.doiCounts.put(tag, 1);
        } else {
            this.doiCounts.put(tag, count + 1);
        }
    }

    /**
     * Method processing the start tag of an element.
     *
     * @param uri
     * @param localName
     * @param qName Name of the element being processed.
     * @param attributes Attributes of the element being processed.
     * @throws SAXException
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        /* Process listSets ********************************************************************** */
        if (qName.equalsIgnoreCase("ListSets")) {
            this.startListSets = true;
            this.startSetSpec = false;
        }

        if (this.startListSets && qName.equalsIgnoreCase("setSpec")) {
            this.startSetSpec = true;
        }

        if ((qName.equalsIgnoreCase("record")) || (qName.equalsIgnoreCase("oai:record"))) {
            this.startSet = false;
            this.startSubject = false;
        }

        if (!this.startListSets && qName.equalsIgnoreCase("setSpec")) {
            this.startSet = true;
        }

        if (qName.equalsIgnoreCase("dc:subject")) {
            this.startSubject = true;
        }

        /* Process records *********************************************************************** */
        // mark current element
        this.currentElement = qName;

        // beginning of a record -- reset everything
        if (qName.equalsIgnoreCase("ListSets")) {
        }
        if ((qName.equalsIgnoreCase("record")) || (qName.equalsIgnoreCase("oai:record"))) {
            // records tag marks start of a new record
            articleMetadata = new ArticleMetadata();

            articleMetadata.setRepositoryId(repositoryMetadata.getRepositoryId());
            articleMetadata.setRepository(repositoryMetadata.getRepositoryName());

            repositoryMetadata.incRecordsCount();
            // set all start tags to false
            startTitle = startAuthor = startcontributor = startPublisher = startDate = startIdentifier = startPdfUrl = startType = startRelation = startDoi = startSourceUrl = deleted = startLanguage = startLicense = false;
            this.startRecord = true;
        } else if ((qName.equalsIgnoreCase("identifier"))) {
            this.startIdentifier = true;
        }

        // check if article has status="deleted"
        if ((qName.equalsIgnoreCase("header")) || (qName.equalsIgnoreCase("oai:header"))) {
            startHeader = true;
            if (attributes.getLength() >= 1) {
                String value = attributes.getValue("status");
                if (value != null && value.equalsIgnoreCase("deleted")) {
                    this.deleted = true;
                    repositoryMetadata.decRecordsCount();
                    repositoryMetadata.incDeletedRecordsCount();
                }
            }
        }

        // if we are not only processing statistics -- parse the rest of the tags also
        if (parseMethod != XMLParseMethodEnum.STATISTICS) {


            if (qName.equalsIgnoreCase("repositoryName")) {
                this.startRepositoryName = true;
            } else if (qName.equalsIgnoreCase("dc:identifier")) {
                this.startDoi = true;
                this.startJournalIdentifier = true;                
                this.startPdfUrl = true;
            } else if (qName.equalsIgnoreCase("identifier")) {
                this.startDoi = true;
                this.startPdfUrl = true;
            } else if(qName.equalsIgnoreCase("doi"))
            {
                this.startDoi = true;
            } else if (qName.equalsIgnoreCase("baseURL")) {
                this.startBaseURL = true;
            } else if ((qName.equalsIgnoreCase("title"))
                    || (qName.equalsIgnoreCase("dc:title"))
                    || (qName.equalsIgnoreCase("datacite:title"))) {
                this.startTitle = true;
            } else if (qName.equalsIgnoreCase("publisher")
                    || qName.equalsIgnoreCase("dc:publisher")) {
                this.startPublisher = true;
            } else if(qName.equalsIgnoreCase("crm-item") &&
                    attributes.getValue(0) != null &&
                    attributes.getValue(0).equalsIgnoreCase("publisher-name")){
                this.startPublisher = true;
            } else if ((qName.equalsIgnoreCase("creator"))
                    || (qName.equalsIgnoreCase("dc:creator"))
                    || (qName.equalsIgnoreCase("datacite:creator"))
                    || (qName.equalsIgnoreCase("rioxxterms:author"))
                    || (qName.equalsIgnoreCase("author"))) {
                this.startAuthor = true;
            } else if ((qName.equalsIgnoreCase("contributor"))
                    || (qName.equalsIgnoreCase("dc:contributor"))
                    || (qName.equalsIgnoreCase("contributors"))) {
                this.startcontributor = true;
            } else if ((qName.equalsIgnoreCase("date"))
                    || (qName.equalsIgnoreCase("dc:date"))
                    || (qName.equalsIgnoreCase("rioxxterms:publication_date"))
                    || (qName.equalsIgnoreCase("publication_date"))) {
                this.startDate = true;
            } else if (qName.equalsIgnoreCase("dc:description")
                    || qName.equalsIgnoreCase("dcterms:abstract")
                    || qName.equalsIgnoreCase("description")) {
                this.startDescription = true;
            } else if ((qName.equalsIgnoreCase("dc:language"))) {
                this.startLanguage = true;
            } else if ((qName.equalsIgnoreCase("dc:type"))
                    || (qName.equalsIgnoreCase("type"))) {
                this.startType = true;
            } else if ((qName.equalsIgnoreCase("dc:subject"))
                    || (qName.equalsIgnoreCase("datacite:subject"))
                    || (qName.equalsIgnoreCase("subject"))) {
                this.startTopic = true;
            } else if ((qName.equalsIgnoreCase("dc:relation"))) {
                this.startRelation = true;
                this.startDoi = true;
            } else if (qName.equalsIgnoreCase("dc:identifier.doi")
                    || (qName.equalsIgnoreCase("oapen:identifierdoi"))
                    || qName.equalsIgnoreCase("rioxxterms:version_of_record")) {
                this.startDoi = true;
                this.startPdfUrl = true;
            } else if ((qName.equalsIgnoreCase("dc:source"))) {
                this.startSourceUrl = true;
            } else if (qName.equalsIgnoreCase("dc:rights")
                || qName.equalsIgnoreCase("datacite:rights")) {
                this.startLicense = true;
            } else if(qName.equalsIgnoreCase("ali:license_ref")){
                try {
                    String startDate = attributes.getValue("start_date");
                    if (startDate != null && new TextToDateTime(startDate.trim())
                            .asLocalDateTime().isAfter(LocalDateTime.now())) {
                        this.startLicense = false;
                    } else {
                        this.startLicense = true;
                    }
                } catch (DateTimeParseException | IndexOutOfBoundsException e) {
                    logger.error("Error converting start_date to LocalDateTime", e);
                    this.startLicense = false;
                }
            } else if (startHeader && qName.equalsIgnoreCase("datestamp")) {
                this.startDateStamp = true;
            } 
        }

        if (startRecord) {
            rawRecord.append("<");
            rawRecord.append(qName);

            if (attributes.getLength() > 0) {
                rawRecord.append(" ");
                for (int i = 0; i < attributes.getLength(); i++) {
                    rawRecord.append(attributes.getQName(i));
                    rawRecord.append("=");
                    rawRecord.append("\"");
                    rawRecord.append(attributes.getValue(i));
                    rawRecord.append("\" ");
                }
            }
            rawRecord.append(">");
        }
    }
    DateTime end = new DateTime();

    /**
     * Method processing the end of an element.
     *
     * @param uri
     * @param localName
     * @param qName Name of the tag being processed.
     * @throws SAXException
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("ListSets")) {
        }

        this.processElementContent();

        if (qName.equalsIgnoreCase("ListSets")) {
            this.startListSets = false;
        }

        if (startRecord) {
            rawRecord.append("</");
            rawRecord.append(qName);
            rawRecord.append(">");

        }

        if ((qName.equalsIgnoreCase("record")) || (qName.equalsIgnoreCase("oai:record"))) {

            startRecord = false;
            articleMetadata.setRawRecordXml(rawRecord.toString());
            rawRecord = new StringBuilder();

            // we are interested in retrieving documents, not just statistics
            if (parseMethod != XMLParseMethodEnum.STATISTICS) {

                boolean addArticle = true;

                assert (repositoryMetadata.getRepositoryName().equals(articleMetadata.getRepository()));
                //articleMetadata.setRepository(repositoryMetadata.getRepositoryName());

                assert (repositoryMetadata.getRepositoryId().equals(articleMetadata.getRepositoryId()));
                //articleMetadata.setRepositoryId(repositoryMetadata.getRepositoryId());

                // add the article to the corpus
                if (this.parseMethod != XMLParseMethodEnum.ONDEMAND_TEST
                        && this.parseMethod != XMLParseMethodEnum.PARSEALL_TEST && this.deleted) {

                    // if article was marked as deleted we add it to the corpus so it could be
                    // later marked as deleted also in database
                    articleMetadata.setDeleted((this.deleted) ? DeletedStatus.DELETED : DeletedStatus.ALLOWED);
                    this.incClassCount(this.articleMetadata.getDocClass());

                } else if (articleMetadata.getPdfUrl() != null) {
//                    System.out.println("url: " + articleMetadata.getPdfUrl());

                    // all cases when we have PDF link
                    addArticle = this.incClassCount(this.articleMetadata.getDocClass());

                } else if (this.parseMethod != XMLParseMethodEnum.ONDEMAND_TEST
                        && this.parseMethod != XMLParseMethodEnum.PARSEALL_TEST
                        && articleMetadata.getPdfUrl() == null) {

                    // we are interested in all records, even without PDF
                    repositoryMetadata.incNoUrlRecordsCount();
                    this.incClassCount(this.articleMetadata.getDocClass());

                } else {
                    addArticle = false;
                }

                if (addArticle) {
                    if (parseMethod == XMLParseMethodEnum.PARSEALL
                            || this.parseMethod == XMLParseMethodEnum.PARSEALL_TEST) {
                        repositoryMetadata.addArticleMetadata(articleMetadata);
                    } else if (parseMethod == XMLParseMethodEnum.ONDEMAND
                            || parseMethod == XMLParseMethodEnum.ONDEMAND_TEST) {
                        try {
//                            DateTime start = new DateTime();
//                            Period periodProcess = new Period(end, start);
//                            logger.debug("Seconds to process xml " + periodProcess.getMillis(), this.getClass());
                            // the reader may read articleMetadata now
                            readerWaitSemaphore.release();

                            // wait until the reader reads it
                            parserWaitSemaphore.acquire();

//                            end = new DateTime();
//                            Period period = new Period(start, end);
//                            logger.debug("Seconds Waiting xml" + period.getMillis(), this.getClass());
                            // to make sure the data won't be rewritten by this thread
                            articleMetadata = null;

                        } catch (InterruptedException ex) {
                            logger.debug("XML parser interrupted.", this.getClass());
                            articleMetadata = null;
                            parserWaitSemaphore.release();
                        }
                    }
                } else if (this.parseMethod == XMLParseMethodEnum.ONDEMAND_TEST) {
                    // after we finish adding documents to the test collection we need to
                    // tell the SAXReaderCorpus that we have no more documents
                    this.articleMetadata = null;
                }
            } else {
                if (articleMetadata.getPdfUrl() != null) {
                    if (articleMetadata.getPdfUrl().endsWith(".pdf")) {
                        repositoryMetadata.incPdfsCount();
                    }
                }
                // we are just interested in counting statistics
                if ((this.articleMetadata.getPdfUrl() != null)) {

                    // increase count of documents in this class
                    this.incClassCount(this.articleMetadata.getDocClass());
                }

            }

        } else if (qName.equalsIgnoreCase("harvest")) {
            if (parseMethod == XMLParseMethodEnum.ONDEMAND
                    || parseMethod == XMLParseMethodEnum.ONDEMAND_TEST) {
                readerWaitSemaphore.release();
            }
            // Log the repository statistics.
            logger.debug("Repository " + repositoryMetadata.getRepositoryId() + " contains: "
                    + repositoryMetadata.getRecordsCount() + " records and "
                    + repositoryMetadata.getPdfsCount() + " pdf urls.", this.getClass());
        } else
     /**
-     * Set the PDF URL of the current article.
+     * Parses string content and matches pdf url which adds to url list
      *
      * @param content
      */ if (qName.equalsIgnoreCase("header")) {
            startHeader = false;
        } else if (qName.equalsIgnoreCase("datestamp")) {
            startDateStamp = false;
        } else if (qName.equalsIgnoreCase("identifier") || qName.equalsIgnoreCase("dc:identifier")
                || qName.equalsIgnoreCase("dc:description") || qName.equalsIgnoreCase("dc:relation")) {
            this.startDoi = false;
            this.startJournalIdentifier = false;
        }

        if (qName.equalsIgnoreCase("dc:identifier.doi")
                || qName.equalsIgnoreCase("rioxxterms:version_of_record")) {
            this.startDoi = false;
            this.startPdfUrl = false;
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

        content = content.replace("\\ud", "\n");
        content = content.replace("\\u26", "&");
        content = content.replace("\\u22", " ");
        content = content.replace("Ã¢â‚¬Å", " ");

        elementContent.append(content);

    }

    /**
     * Process the content of the element.
     */
    protected void processElementContent() {

        if (startRecord) {
            rawRecord.append(elementContent.toString());
        }

        String content = elementContent.toString().trim();

        /* DOI statistics *********************************************************************** */
        // the parseDoi method returns false in case it finds DOI -- this tells the class that it
        // can end processing a given tag -- that's why the following code is checking
        // FALSE occurrence
        if (parseMethod == XMLParseMethodEnum.STATISTICS_DOI) {
            boolean isNotDoi = this.parseDoi(this.cleanPotentialDoi(content));
            if (isNotDoi == false) {
                this.incDoiCount(this.currentElement);
            }
        }

        /* Process document class *************************************************************** */
        if (this.startSetSpec) {
            this.classList.add(content);
            this.startSetSpec = false;
        }

        if (this.startSet) {
            this.articleMetadata.setDocClass(content);
            if(this.articleMetadata != null){
                this.articleMetadata.addSetName(content);
            }
            this.startSet = false;
        }

        if (this.startSubject) {
            if (this.articleMetadata != null) {
                if (this.articleMetadata.getSubjects() != null && !this.articleMetadata.getSubjects().contains(content)) {
                    this.articleMetadata.addSubject(content);
                }
            }
            this.startSubject = false;
        }

        /* Process record ************************************************************************ */
        if (articleMetadata != null) {

            if (this.startIdentifier && this.startHeader) {
                if (articleMetadata.getOAIIdentifier() == null) {
                    this.setOAI(content);
                }
                startIdentifier = false;
            }            
        }

        if (parseMethod != XMLParseMethodEnum.STATISTICS) {
            if (this.startRepositoryName) {
                repositoryMetadata.setRepositoryName(content);
                startRepositoryName = false;
            }

            if (this.startBaseURL) {
                repositoryMetadata.setBaseURL(content);
                startBaseURL = false;
            }

            if (articleMetadata != null) {

                if (this.startAuthor) {
                    articleMetadata.addAuthor(content);
                    startAuthor = false;
                }

                if (this.startcontributor) {
                    articleMetadata.addContributor(content);
                    startcontributor = false;
                }

                if (this.startLanguage) {
                    articleMetadata.setRawLanguage(content);
                    startLanguage = false;
                }

                if(this.startLicense) {
                    articleMetadata.setLicense(content);
                    startLicense = false;
                }

                if (this.startTitle) {
                    if (!content.isEmpty()) {
                        articleMetadata.setTitle(content);
                    }
                    startTitle = false;
                }

                if (this.startPublisher) {
                    articleMetadata.setPublisher(content);
                    startPublisher = false;
                }

                if (this.startDate) {
                    articleMetadata.setDate(content);
                    startDate = false;
                }

                if (this.startDescription) {
                    //
                    // some metadata files contain description
                    // in  multiple description tags
                    // concatenate them
                    articleMetadata.addToDescription(content);
                    this.startDescription = false;
                }

                if (this.startType) {
                    articleMetadata.addType(content);
                    this.startType = false;
                }

                if (this.startTopic) {
                    articleMetadata.addTopic(content);
                    this.startTopic = false;
                }

                if (this.startRelation) {
                    articleMetadata.addRelation(content);
                    this.startRelation = false;
                }

                if (this.startDoi) { // && !this.currentElement.equalsIgnoreCase("dc:relation")) {
                    this.startDoi = !this.parseDoi(this.cleanPotentialDoi(content));
                }

                if (this.startJournalIdentifier) {
                    this.parseJournalIdentifier(content);
                }
                if (this.startSourceUrl) {
                    this.setPdfUrl(content);
                    this.startSourceUrl = false;
                }
                if (this.startPdfUrl && !this.startHeader) {
                    if (content.startsWith("http")) {
                        articleMetadata.addPdfUrl(content);
                    } else {
                        this.setPdfUrl(content);
                    }
                    this.startPdfUrl = false;
                }

                // This is the raw DateStamp
                // Note: OAI-PMH does not return the deposited date but the last edited date
                // We infer the deposited date based on if this is the first time we have seen the document
                // This is calculated later in in the metadata extract process CORE-2091
                if (startHeader && startDateStamp) {
                    try {
                        // If length is 20, then attempt full date time parse
                        // else just get the date
                        if (content.length() == 20) {
                            articleMetadata.setDateStamp(datetimeformat.parse(content));
                        } else {
                            // first 10 characters should be always yyyy-MM-dd
                            if (content.length() >= 10) {
                                articleMetadata.setDateStamp(fmt.parse(content.substring(0, 10)));
                            } else {
                                articleMetadata.setDateStamp(fmt.parse(content));
                            }
                        }
                    } catch (ParseException ex) {
                        logger.error("Unsupported date format: '" + content + "'. Should be implemented.", this.getClass());
                    }
                }
            }
        }
        elementContent.setLength(0);
    }

    private void setOAI(String content) {
        if (content.startsWith(GENERIC_EPRINTS_OAI)) {
            assert (articleMetadata.getRepositoryId() != null);
            content += "/core" + articleMetadata.getRepositoryId();
        }
        articleMetadata.setIdentifier(content);
        articleMetadata.setOAIIdentifier(content);
    }

    /**
     * Parses string content and matches pdf url which adds to url list
     *
     * @param content
     */
    private void setPdfUrl(String content) {
        if (!content.contains("creativecommons.org/licenses")) {
            content = content.trim();

            // Figshare patch - see CORE-3172
            // We are also assuming that this method is being called from a dc:identifier field
            if (null != articleMetadata.getOAIIdentifier()
                    && articleMetadata.getOAIIdentifier().startsWith("oai:figshare.com")
                    && content.contains("/")) {
                content = new StringBuffer("http://hdl.handle.net/")
                        .append(content)
                        .toString();
            }

            Matcher matcher = urlPattern.matcher(content);

            List<String> urls = new ArrayList<String>();

            while (matcher.find()) {
                int urlStartIndex = matcher.start(1);
                int urlFinishIndex = matcher.end();
                String newUrl = content.substring(urlStartIndex, urlFinishIndex);                
                urls.add(newUrl);
            }
            articleMetadata.setPdfUrls(urls);
        }

    }

    private final List<String> doiPrefixList = Arrays.asList(
            "http://dx.doi.org/",
            "https://dx.doi.org/",
            "http://doi.org/",
            "https://doi.org/",
            "doi:",
            "DOI",
            "urn:doi:",
            "urn:DOI:"
    );

    /**
     * Removes url prefix from potential DOIs.
     *
     * @param content to check
     */
    public String cleanPotentialDoi(String content) {
        for (String replacingString : this.doiPrefixList) {
            content = content.replace(replacingString, "");
        }
        return content;
    }

    /**
     * Try to find DOI identifier.
     *
     * The pattern for DOI was created according to
     * http://www.doi.org/overview/DOI-ELIS-Paskin.pdf.
     *
     * @param content to check
     */
    public boolean parseDoi(String content) {
        // the pattern is more strict than the describtion of DOI syntax from
        // the above mentioned PDF, however this is necessary
        // to exclude false positives
        // Sam: We were adding lots of 'bad' DOI's where the DOI contained [:()]
        //      I have added them to the RegEx. See CORE-586 for further details
        Pattern doiPattern = Pattern.compile("10\\.\\d+/[\\.\\-\\w():/]+");

        Matcher doiMatcher = doiPattern.matcher(content);
        if (null == articleMetadata.getDoi() && doiMatcher.find()) {
            String doi = doiMatcher.group();
            articleMetadata.setDoi(doi);
            articleMetadata.setDoiMetadataTag(this.currentElement);
            return true;
        } else {
            return false;
        }
    }

    /* ****************************************************************************************** */
    /* DOI parsing experiment                                                                     */
    /* ****************************************************************************************** */
    /**
     * Build an output CSV table (which tabs as separators).
     *
     * @param tags
     * @param repos
     * @param results
     * @return
     */
    private static String buildOutput(Set<String> tags, Multimap<Integer, String> repos,
            Map<Integer, Map<String, Integer>> results) {

        // convert the set of tags into list (for easier iteration over the tags)
        List<String> tagsList = Arrays.asList(tags.toArray(new String[tags.size()]));

        // column names
        List<String> colNames = new LinkedList<String>(
                Arrays.asList(new String[]{"ID", "Name", "URI", "Downloaded",
                    "Metadata records", "DOIs found"}));
        colNames.addAll(tagsList);

        // the output table
        StringBuilder output = new StringBuilder(StringUtils.join(colNames, "\t"));
        output.append("\n");

        for (Integer repositoryId : repos.keySet()) {

            // repository info
            StringBuilder line = new StringBuilder(
                    StringUtils.join(repos.get(repositoryId).toArray(), "\t"));
            line.append("\t");

            // DOI counts
            for (String t : tagsList) {
                Integer v = results.get(repositoryId).get(t);
                line.append(v == null ? "0" : v.toString());
                line.append("\t");
            }

            output.append(line);
            output.append("\n");
        }

        return output.toString();
    }

    private void parseJournalIdentifier(String content) {
        String jIdentifier = XMLJournalHandler.parseIdentifier(content);
        // jIdentifier would be prefixed with url: or issn:
        if (jIdentifier.toLowerCase().startsWith("issn:")
                || (this.repositoryMetadata.getRepositoryId().equals(RepositoryType.DOAJ)
                && jIdentifier.toLowerCase().startsWith("url:"))) {
            this.articleMetadata.addJournalIdentifier(jIdentifier);
            String jil = jIdentifier.toLowerCase();
            if (jil.toLowerCase().startsWith("issn:") || jil.toLowerCase().startsWith("urn:issn:")) {
                this.articleMetadata.addJournalIssn(jil.replace("issn:", "").replace("urn:", ""));
            }
        }
    }

    /**
     * Only for testing!!!
     * @param am 
     */
    public void setArticleMetadata(ArticleMetadata am) {
        this.articleMetadata = am;
    }
}
