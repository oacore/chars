package uk.ac.core.database.service.document.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.core.common.model.legacy.*;
import uk.ac.core.database.model.mappers.ArticleMetadataMapper;
import uk.ac.core.database.mucc.MUCCDocumentDAO;
import uk.ac.core.database.service.citation.CitationDAO;
import uk.ac.core.database.service.document.ArticleMetadataDAO;
import uk.ac.core.database.service.document.DocumentTypeDAO;
import uk.ac.core.database.service.document.RawMetadataDAO;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.database.service.duplicates.DuplicatesDAO;
import uk.ac.core.database.service.repositories.RepositoriesDAO;
import uk.ac.core.filesystem.services.FilesystemDAO;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class MySQLArticleMetadataDAO implements ArticleMetadataDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    RepositoryDocumentDAO repositoryDocumentDAO;

    @Autowired
    RepositoriesDAO repositoriesDAO;

    @Autowired
    FilesystemDAO filesystemDAO;

    @Autowired
    RawMetadataDAO rawMetadataDAO;

    @Autowired
    CitationDAO citationDAO;

    @Autowired
    DuplicatesDAO duplicatesDAO;

    @Autowired
    DocumentTypeDAO documentTypeDAO;

    @Autowired
    MySQLDocumentUrlDAO documentUrlDAO;

    @Autowired
    MUCCDocumentDAO mUCCDocumentDAO;
    
    

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MySQLArticleMetadataDAO.class);

    /**
     * Max size of a batch for writing data to database.
     */
    protected static final int BATCH_SIZE = 1000;

    /**
     * Batch list for inserting new DOIs to database. Correct access to the list
     * by multiple threads is assured by using synchronized methods.
     */
    private final List<Object[]> doiBatch = new LinkedList<Object[]>();

    /**
     * Batch list for inserting new article metadata into the database.
     */
    private final List<Object[]> articleBatch = new LinkedList<Object[]>();

    /**
     * Batch list for inserting new raw metadata records into the database.
     */
    private final List<ArticleMetadata> rawMetaBatch = new LinkedList<ArticleMetadata>();

    private final Map<Integer, String> repositoryNamesCache = new HashMap<>();

    // *** WARNING ***
    // We use an sql anti pattern: storing multiple values to one column as a string separated with a delimiter
    // Make sure our delimiter is unique enough that it will conflict with an arbritrary string
    // and be consistent to use the same delimiter when joining strings and when spliting
    public static final String DELIMITER = " #-#-# ";// avoid using '[',']','{','}' as it will mess up the regex used for splitting

    @Override
    @Transactional(readOnly = true)
    public ArticleMetadata getFullArticleMetadata(Integer articleId) {
        ArticleMetadata am = this.getArticleMetadata(articleId);

        if (am == null) {
            return null;
        }

        long startTime = System.currentTimeMillis();
        List<DocumentUrl> urlList = this.documentUrlDAO.load(articleId);


        am.setPdfUrls(urlList.stream().map(e -> Objects.toString(e.getUrl())).collect(Collectors.toList()));

        // get and set reporitory document belonging to this article
        RepositoryDocument repositoryDocument = repositoryDocumentDAO.getRepositoryDocumentById(articleId);
        long timeElapsed = System.currentTimeMillis() - startTime;
        logger.info("Loading RepositoryDocument {}", timeElapsed);

        if (repositoryDocument != null) {

            long startTime1 = System.currentTimeMillis();
            am.setRepositoryDocument(repositoryDocument);
            am.setRepository(getRepositoryName(repositoryDocument.getIdRepository()));
            am.setRepositoryId(repositoryDocument.getIdRepository());

            am.setPdfDownloadedFrom(repositoryDocument.getPdfUrl());

            // set text and pdf metadata
            // Around 30MB of text
            am = this.setTextAndPdfArticleMetadata(am.getId(), am, 31457280);

            long timeElapsed1 = System.currentTimeMillis() - startTime1;
            logger.info("RepositoryDocument load time: {}", timeElapsed1);
        } else {
            logger.warn("Repository document of article #" + articleId + " is NULL!!", this.getClass());
        }

        long startTime2 = System.currentTimeMillis();
        // set duplicates        
        am.setParentId(duplicatesDAO.getParentId(am.getId()));

        // set citations
        List<Citation> citations = citationDAO.getCitations(articleId);
        if (!citations.isEmpty()) {
            am.setCitations(citations);
        }

        // set language
        am.setLanguage(repositoryDocumentDAO.getDocumentLanguage(am.getId()));

        // set deleted status
        am.setDeleted(repositoryDocumentDAO.getDeletedStatus(am.getId()));

        // get raw xml metadata record
        DocumentRawMetadata xml = rawMetadataDAO.getDocumentRawMetadataByCoreID(articleId);
        if (xml != null) {
            am.setRawRecordXml(xml.getMetadata());
        }
        
        Pair<String, Double> documentTypeInfo = documentTypeDAO.getDocumentTypeInfo(am.getId());
        if(documentTypeInfo != null){
            am.setDocumentType(documentTypeInfo.getFirst());
            am.setDocumentTypeConfidence(documentTypeInfo.getSecond());
        }
        am.setDepositedDateStamp(this.getDepositedDateStamp(articleId));

        long timeElapsed2 = System.currentTimeMillis() - startTime2;
        logger.info("Setting additional data works {}", timeElapsed2);
        return am;
    }

    private String getRepositoryName(Integer repositoryId) {
        if(!repositoryNamesCache.containsKey(repositoryId)) {
            repositoryNamesCache.put(repositoryId, repositoriesDAO.getRepositoryName(repositoryId));
        }

        return repositoryNamesCache.get(repositoryId);
    }

    @Override
    public ArticleMetadata getArticleMetadata(Integer idDocument) {

        String sql = "SELECT * FROM document_metadata where id_document = ?";

        try {
            return this.jdbcTemplate.queryForObject(sql,
                    new ArticleMetadataMapper(), new Object[]{idDocument});
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    /**
     * Add full-text and PDF information to article metadata.
     *
     * Limits text to 30MB
     *
     * @param documentId
     * @param am
     * @return
     */
    private ArticleMetadata setTextAndPdfArticleMetadata(Integer documentId, ArticleMetadata am, int maxSizeInCharacters) {

        Integer repositoryId = am.getRepositoryId();
        File textFile = new File(filesystemDAO.getTextPath(documentId, repositoryId));

        StringBuilder builder = new StringBuilder();

        if (textFile.exists()) {
            AtomicInteger counter = new AtomicInteger(0);
            try (LineIterator it = FileUtils.lineIterator(textFile, Charset.defaultCharset().toString())) {
                while (it.hasNext() && counter.get() < maxSizeInCharacters) {
                    String line = it.nextLine();
                    int size = line.length();
                    builder.append(line);
                    counter.addAndGet(size);
                }
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }

            // set full text
            if (builder != null && builder.length() > 0) {
                am.setFullText(builder.toString());
                am.setTextExtracted(true);
            }
        }
        // Release memory asap after full text is extracted
        builder = null;

        // set size of PDF
        File fLocation = new File(filesystemDAO.getPdfPath(documentId, repositoryId));
        long lengthFile = (long) fLocation.length();
        am.setSize(lengthFile);

        return am;
    }

    @Override
    public String getArticleTitle(Integer articleId) {
        String query = "SELECT title FROM document_metadata WHERE id_document = ?";
        String title = null;
        try {
            title = this.jdbcTemplate.queryForObject(query, new Object[]{articleId}, String.class);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            title = null;
        }
        return title;
    }

    @Override
    public Optional<String> getArticleLanguage(Integer articleId) {
        String query = "SELECT `language` FROM document_metadata WHERE id_document = ?";
        try {
            return Optional.ofNullable(this.jdbcTemplate.queryForObject(query, new Object[]{articleId}, String.class));
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
        }
        return Optional.empty();
    }

    @Override
    public Timestamp getDepositedDateStamp(Integer articleId) {
        String query = "SELECT deposited_date FROM document_metadata WHERE id_document = ?";
        try {
            return this.jdbcTemplate.queryForObject(query, new Object[]{articleId}, Timestamp.class);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    /**
     * Method is synchronised to avoid inserting to batch by one thread while
     * another thread is already exporting the batch to the database.
     *
     * @param am
     */
    @Override
    public synchronized void addArticleMetadata(ArticleMetadata am) {

        if (am == null) {
            return;
        }

        logger.debug("Adding ArticleMetadata to batch: #" + am.getOAIIdentifier(), this.getClass());

        // CORE-2091
        // We want to store the created date AND last modified date
        // We determine the created date as the first date we see from <header><datestamp> field
        // If there isn't a field, or it us null, insert the current datestamp field date (this suggests the item is new)
        // If there is a field, then the datestamp is updated indicating that the the item 
        //        has been modified. 
        Timestamp createdDate = this.getDepositedDateStamp(am.getId());
        if (createdDate == null) {
            if (am.getDateStamp() == null) {
                createdDate = new Timestamp(System.currentTimeMillis());
            } else {
                createdDate = new Timestamp(am.getDateStamp().getTime());
            }
        }

        this.articleBatch.add(this.toParameterArray(am, createdDate));

        if (this.articleBatch.size() % 100 == 0) {
            logger.debug("Size of Batch (document_metadata') #" + this.articleBatch.size(), this.getClass());
        }

        if (this.articleBatch.size() >= BATCH_SIZE) {
            logger.info("Article batch larger that BATCH_SIZE, exporting.", this.getClass());
            this.flushArticles();
        }
    }

    /**
     * Create a string from a list by joining the items of the list with a
     * specified delimiter (constant ArticleMetadataHandler.DELIMITER).
     *
     * @param list
     * @return null if the list is null or empty, string with the joined items
     * otherwise
     */
    private String toDatabaseString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return String.join(DELIMITER, list);
    }

    /**
     * Check the given string, return null if it's null or empty, otherwise
     * return the string unchanged. This is just to avoid inserting empty
     * strings to the database (null will be inserted instead of empty string).
     *
     * @param item
     * @return null if item is null or empty, the unchanged string item
     * otherwise
     */
    private String toDatabaseString(String item) {
        if (item == null || item.isEmpty()) {
            return null;
        }
        return item;
    }

    /**
     * Convert article metadata into a 1D Object array. This method is used for
     * passing article metadata to the QueryRunner.insert method.
     *
     * @param am
     * @param depositedDatestamp the created date of the object to insert
     * @return
     */
    private Object[] toParameterArray(ArticleMetadata am, java.util.Date depositedDatestamp) {
        List<Object> params = new LinkedList<Object>();

        String license = am.getLicense();
        if (license == null) {
            params.add(this.toDatabaseString(license));
        } else {
            params.add(this.toDatabaseString((license.length() <= 100) ? license : license.substring(0, 99)));
        }
        params.add(this.toDatabaseString(am.getAuthors()));
        params.add(this.toDatabaseString(am.getContributors()));
        params.add(this.toDatabaseString(am.getIdentifiers()));
        params.add(this.toDatabaseString(am.getRawLanguage()));
        params.add(this.toDatabaseString(am.getRelations()));
        params.add(this.toDatabaseString(am.getSubjects()));
        params.add(this.toDatabaseString(am.getTopics()));
        params.add(this.toDatabaseString(am.getTypes()));
        params.add(this.toDatabaseString(am.getDate()));
        params.add((am.getDateStamp() != null) ? new java.sql.Timestamp(am.getDateStamp().getTime()) : null);
        params.add((depositedDatestamp != null) ? new java.sql.Timestamp(depositedDatestamp.getTime()) : null);
        params.add(this.toDatabaseString(am.getTitle()));
        String dbDescription = this.toDatabaseString(am.getDescription());
        String dbDescriptionTruncated = null;
        if (dbDescription != null) {
            dbDescriptionTruncated = dbDescription
                    .substring(0, Math.min(dbDescription.length() - 1, 21843))
                    .replaceAll("\\<.*?\\>", "");
        }
        params.add(dbDescriptionTruncated);
        params.add(this.toDatabaseString(am.getDoi()));
        params.add(this.toDatabaseString(am.getDoiMetadataTag()));
        params.add(this.toDatabaseString(am.getPublisher()));

        String journalDb = this.toDatabaseString(am.getJournalIdentifiers());
        params.add(journalDb);

        // the insert list needs all parameters duplicated, because the insert query uses
        // ON DUPLICATE KEY UPDATE
        List<Object> all = new LinkedList<Object>();
        all.add(am.getId()); // first goes ID
        all.addAll(params);  // parameters for INSERT
        all.addAll(params);  // parameters for ON DUPLICATE KEY UPDATE

        return all.toArray();
    }

    /**
     * Export all remaining articles to DB. Method is synchronized to avoid
     * multiple threads to do insert and or flush at the same time.
     */
    @Override
    public synchronized void flushArticles() {
        logger.info("Flushing articles.", this.getClass());
        this.exportArticles();
        this.articleBatch.clear();
    }

    /**
     * Insert a batch of document metadata into the database. If IDs already
     * exists, update the record.
     *
     */
    private void exportArticles() {

        logger.info("Inserting article metadata to database. Number of articles to be inserted: "
                + this.articleBatch.size(), this.getClass());

        String sql = "INSERT INTO document_metadata "
                + "(id_document,license,authors,contributors,"
                + "identifiers,`language`,relations,subjects,"
                + "topics,types,date,"
                + "datestamp,deposited_date,title,description,doi,doi_metadata_tag,publisher,journal_identifier) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE "
                + "license=?,authors=?,contributors=?,identifiers=?,language=?,"
                + "relations=?,subjects=?,topics=?,"
                + "types=?,date=?,datestamp=?,deposited_date=?,"
                + "title=?,description=?,"
                + "doi=?,doi_metadata_tag=?,publisher=?,"
                + "journal_identifier=?";

        try {
            this.jdbcTemplate.batchUpdate(sql, this.articleBatch);
        } catch (DataAccessException ex) {
            // if the batch fails, log the info but do nothing
            logger.warn(ex.getMessage(), this.getClass());
        }
    }

    /**
     * Method is synchronized to avoid inserting to batch by one thread while
     * another thread is already exporting the batch to the database.
     *
     * @param am
     * @return true if record changed, false if it didn't or in case of first
     * insertion
     */
    @Override
    public synchronized Boolean addRawArticleMetadata(ArticleMetadata am) {

        Boolean changed = false;

        if (am == null || am.getId() == null || am.getRawRecordXml() == null) {
            return false;
        }

        logger.debug("Adding raw metadata to batch: #" + am.getId(), this.getClass());

        String metaCurrent = this.getLatestRawArticleMetadata(am.getId());
        if (metaCurrent == null) {
            this.rawMetaBatch.add(am);
        } else if (!am.getRawRecordXml().equals(metaCurrent)) {
            this.rawMetaBatch.add(am);
            changed = true;
        } else {
            logger.debug("No Changes, do not add to raw_metadata #" + am.getId(), this.getClass());
        }

        if (this.rawMetaBatch.size() % 100 == 0) {
            logger.debug("Size of Batch (raw_metadata') #" + this.rawMetaBatch.size(), this.getClass());
        }

        if (this.rawMetaBatch.size() >= MySQLArticleMetadataDAO.BATCH_SIZE) {
            logger.info("Raw metadata batch larger that BATCH_SIZE, exporting.", this.getClass());
            this.exportRawMetadata();
            this.rawMetaBatch.clear();
        }

        return changed;
    }

    /**
     * Get the latest version of the raw document metatada.
     *
     * @param documentId
     * @return
     */
    private String getLatestRawArticleMetadata(Integer documentId) {

        StringBuilder sqlSb = new StringBuilder("SELECT metadata FROM document_raw_metadata ");
        sqlSb.append("WHERE id_document = ? ORDER BY `datetime` DESC LIMIT 1");

        try {
            return (String) this.jdbcTemplate.queryForObject(sqlSb.toString(),
                    String.class, new Object[]{documentId});
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    /**
     * Export all remaining raw metadata to DB. Method is synchronized to avoid
     * multiple threads to do insert and or flush at the same time.
     */
    public synchronized void flushRawMetadata() {
        logger.info("Flushing raw metadata.", this.getClass());
        this.exportRawMetadata();
        this.rawMetaBatch.clear();
    }

    /**
     * Insert a batch of document raw metadata into the database. Insert article
     * metadata only if the current version is different from the latest version
     * stored in the database.
     */
    private void exportRawMetadata() {

        logger.info("Inserting raw metadata to database. Number of articles to be inserted: "
                + this.rawMetaBatch.size(), this.getClass());

        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("INSERT INTO document_raw_metadata ");
        sqlSb.append("(id_document, `datetime`, metadata) ");
        sqlSb.append("VALUES (?, NOW(), ?)");

        List<Object[]> batch = new LinkedList<>();
        for (ArticleMetadata articleMetadata : this.rawMetaBatch) {
            List<Object> params = new LinkedList<>();
            params.add(articleMetadata.getId());
            params.add(articleMetadata.getRawRecordXml());
            batch.add(params.toArray());
        }

        try {
            this.jdbcTemplate.batchUpdate(sqlSb.toString(), batch);
        } catch (DataAccessException ex) {
            logger.warn(ex.getMessage(), this.getClass());
        }
    }

}
