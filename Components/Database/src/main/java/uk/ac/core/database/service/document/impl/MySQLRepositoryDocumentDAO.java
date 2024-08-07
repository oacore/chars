package uk.ac.core.database.service.document.impl;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.common.model.article.PDFUrlSource;
import uk.ac.core.common.model.legacy.DocumentUrl;
import uk.ac.core.common.model.legacy.Language;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.common.model.legacy.RepositoryDocumentBase;
import uk.ac.core.database.model.mappers.LanguageMapper;
import uk.ac.core.database.model.mappers.RepositoryDocumentBaseMapper;
import uk.ac.core.database.model.mappers.RepositoryDocumentMapper;
import uk.ac.core.database.service.document.DocumentUrlDAO;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.database.service.duplicates.DuplicatesDAO;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class MySQLRepositoryDocumentDAO implements RepositoryDocumentDAO {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MySQLRepositoryDocumentDAO.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DocumentUrlDAO documentUrlDAO;

    @Autowired
    DuplicatesDAO duplicatesDAO;

    @Override
    public void setDocumentTextStatus(final Integer articleId, final Integer statusFlag) {
        RepositoryDocument rd = this.getRepositoryDocumentById(articleId);
        String dateString = ", text_last_attempt = NOW()";

        if (rd.getTextFirstAttempt() == null) {
            dateString += ", text_first_attempt = NOW()";
        }

        if (rd.getTextFirstAttemptSuccessful() == null && statusFlag.equals(1)) {
            dateString += ", text_first_attempt_successful = NOW()";
        }

        if (statusFlag.equals(1)) {
            dateString += ", text_last_attempt_successful = NOW()";
        }

        String SQL = "UPDATE document SET "
                + "text_status = ?" + dateString + " WHERE id_document = ?";

        PreparedStatementSetter preparedStatementSetter = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, statusFlag);
                ps.setInt(2, articleId);
            }
        };

        jdbcTemplate.update(SQL, preparedStatementSetter);
    }

    @Override
    public RepositoryDocument getRepositoryDocumentById(final Integer articleId) {
        String GET_REPOSITORY_DOCUMENT_BY_ID_SQL = "SELECT d.*,du.url AS du_url "
                + "FROM document d LEFT join document_urls du ON(d.id_document=du.id_document) "
                + "WHERE d.id_document = ?";
        PreparedStatementSetter preparedStatementSetter = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, articleId);
            }
        };
        RowMapper<RepositoryDocument> rowMapper = new RepositoryDocumentMapper();
        try {
            List<RepositoryDocument> results = jdbcTemplate.query(GET_REPOSITORY_DOCUMENT_BY_ID_SQL, preparedStatementSetter, rowMapper);
            if (results.isEmpty()) {
                return null;
            } else {
                return results.get(0);
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Language getDocumentLanguage(Integer id) {
        String sql = "SELECT l.id_language AS id_language, l.code AS code, l.name AS name "
                + " FROM `language` l inner join document d on l.id_language = d.id_language "
                + " WHERE d.id_document = ?";
        RowMapper<Language> rowMapper = new LanguageMapper();
        try {
            Language language = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return language;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public DeletedStatus getDeletedStatus(Integer id) {
        String sql = "SELECT `deleted` FROM `document` WHERE `id_document` = ?";
        try {
            DeletedStatus amd = jdbcTemplate.queryForObject(sql, new RowMapper<DeletedStatus>() {
                @Override
                public DeletedStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
                    DeletedStatus amd = DeletedStatus.fromInteger(rs.getInt("deleted"));
                    return amd;
                }
            }, id);
            return amd;
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public void setDocumentIndexStatus(Integer documentId, boolean succeeded) {
        RepositoryDocument rd = this.getRepositoryDocumentById(documentId);
        String dateString = ", index_last_attempt = NOW()";

        if (rd.getIndexFirstAttempt() == null) {
            dateString += ", index_first_attempt = NOW()";
        }

        if (rd.getIndexFirstAttemptSuccessful() == null && succeeded) {
            dateString += ", index_first_attempt_successful = NOW()";
        }

        if (succeeded) {
            dateString += ", index_last_attempt_successful = NOW()";
        }

        String sql = "UPDATE document SET "
                + "indexed = ?" + dateString + " WHERE id_document = ?";

        jdbcTemplate.update(sql, succeeded, documentId);
    }

    @Override
    public Optional<LocalDateTime> getIndexLastAttempt(Integer documentId) {
        String sql = "SELECT index_last_attempt FROM document WHERE "
                + "id_document = ?";

        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new Object[] { documentId }, LocalDateTime.class));
    }

    @Override
    public List<RepositoryDocumentBase> getDocuments(String repositoryId, boolean prioritiseOldDocumentsForDownload, Date fromDate, Date toDate, Long offset, Long limit) {
        StringBuilder sqlSb = new StringBuilder("SELECT d.id_document, d.oai, du.url, du.source, d.pdf_last_attempt, d.pdf_status, d.metadata_updated\n" +
                        "FROM document d \n" +
                        "LEFT JOIN document_urls du ON (d.id_document=du.id_document) \n" +
                        "LEFT JOIN document_metadata dm ON dm.id_document = d.id_document \n" +
                        "WHERE d.id_repository = ? AND d.deleted = 0 ");

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

        if (null != fromDate) {
            // Optimise query based on indexed field - we can assume no metadata is lower than an id of 200m
            // All new content will be processed, content updated where the id is lower will not be updated unless a
            // full harvest is scheduled
            logger.info("From date is {}", fromDate);
            sqlSb.append(" AND dm.datestamp >= \"" + dateFormat.format(fromDate) + "\" ");
        }

        if (null != toDate) {
            logger.info("To date is {}", toDate);
            sqlSb.append(" AND dm.datestamp < \"" + dateFormat.format(toDate) + "\" ");
        }

        if (prioritiseOldDocumentsForDownload) {
            sqlSb.append("ORDER BY d.pdf_last_attempt ");
        } else {
            sqlSb.append("ORDER BY d.id_document ");
        }
        if (offset != null && offset >= 0 && limit != null && limit > 0){
            sqlSb.append("LIMIT ").append(offset).append(", ").append(limit);
        }

        try {
            List<Object> paramsList = new ArrayList<>();
            paramsList.add(repositoryId);

            logger.info("Begin executing query getRepositoryDocumentsForPdfDownload");
            logger.info("Query is {}", sqlSb.toString());
            List<RepositoryDocumentBase> documents = this.jdbcTemplate.query(sqlSb.toString(),
                    new RepositoryDocumentBaseMapper(), paramsList.toArray());
            logger.info("End executing query getRepositoryDocumentsForPdfDownload");

            return documents;
        } catch (EmptyResultDataAccessException ex) {
            return new LinkedList<>();
        }
    }

    @Override
    public Integer countRepositoryDocumentsWithFulltext(int repositoryId) {
        StringBuilder sqlSb = new StringBuilder("SELECT COUNT(*) ");
        sqlSb.append("FROM document d ");
        sqlSb.append("WHERE d.id_repository = ? AND d.deleted = 0 AND d.text_status = 1 ");

        return this.jdbcTemplate.queryForObject(sqlSb.toString(), new Object[] { repositoryId }, Integer.class);
    }

    /**
     * Insert new document into database.
     *
     * @param updateId
     * @param repositoryId
     * @param oai
     * @param url
     * @param docClass
     * @return
     */
    @Override
    public Integer addDocument(final Long updateId, final Integer repositoryId, final String oai, final String url, final String docClass) {

        Integer docId = null;

        String docClassStringCol = (docClass != null) ? ", doc_class" : "";
        String docClassStringVal = (docClass != null) ? ", ?" : "";

        final String sql = "INSERT INTO document "
                + "(id_update, oai, id_repository, "
                + "url" + docClassStringCol + ") "
                + "VALUES (?, ?, ?, ?" + docClassStringVal + ")";

        PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, 0);
                ps.setString(2, oai);
                if (repositoryId != null) {
                    ps.setInt(3, repositoryId);
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                ps.setString(4, url);

                if (docClass != null) {
                    ps.setString(5, docClass);
                }
                return ps;
            }
        };

        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(preparedStatementCreator, holder);

        docId = holder.getKey().intValue();

        return docId;
    }

    /**
     * Insert new document - hanlde multiple urls
     *
     * @param updateId
     * @param repositoryId
     * @param oai
     * @param urls
     * @param docClass
     * @return
     */
    @Override
    public Integer addDocument(Long updateId, Integer repositoryId, String oai, Map<String, PDFUrlSource> urls, String docClass) {

        // add as normal to `document` table ...
        String furl = null;
        //override the default url with a better one if possible
        if (urls != null && !urls.isEmpty()) {
            Entry<String, PDFUrlSource> betterUrl = urls.entrySet().stream().filter(entry -> (PDFUrlSource.DIT == entry.getValue() || PDFUrlSource.UNPAYWALL == entry.getValue())).findFirst().orElse(urls.entrySet().iterator().next());
            furl = betterUrl.getKey();
        }

        Integer docId = addDocument(updateId, repositoryId, oai, furl, docClass);
        if (urls != null && !urls.isEmpty()) {
            urls.forEach((url, source) -> {
                DocumentUrl documentUrl = new DocumentUrl(null, docId, url, 0, source);
                documentUrlDAO.insertDocumentUrl(documentUrl);
            });
        }
        return docId;
    }

    @Override
    public void setDocumentMetadataStatus(final Integer documentId) {
        RepositoryDocument rd = this.getRepositoryDocumentById(documentId);
        if (rd == null) {
            logger.warn("Document (ID: {}) not found in `document` table", documentId);
            return;
        }
        String dateString = "";

        if (rd.getMetadataAdded() == null) {
            dateString += ", metadata_added = NOW()";
        }

        String sql = "UPDATE document SET metadata_updated = NOW() " + dateString + " WHERE id_document = ?";

        PreparedStatementSetter preparedStatementSetter = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, documentId);
            }
        };

        jdbcTemplate.update(sql, preparedStatementSetter);

    }

    @Override
    public void setDocumentDateStamp(final Integer documentId, final Date datestamp) {
        if (datestamp == null) {
            return;
        }

        String sql = "UPDATE `document` SET `date_time_stamp` = ? WHERE `id_document` = ?";

        PreparedStatementSetter preparedStatementSetter = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setTimestamp(1, new java.sql.Timestamp(datestamp.getTime()));
                ps.setInt(2, documentId);
            }
        };

        jdbcTemplate.update(sql, preparedStatementSetter);

    }

    /**
     * Update existing document information in database.
     *
     * @param documentId
     * @param updateId
     * @param repositoryId
     * @param oai
     * @param urls
     * @param docClass
     */
    @Override
    public void updateDocuments(Integer documentId, Long updateId, Integer repositoryId, String oai, Map<String, PDFUrlSource> urls, String docClass) {

        // first update document urls
        documentUrlDAO.synchroniseUrlsAsString(documentId, urls);

        updateDocument(documentId, updateId, repositoryId, oai, docClass);
    }

    public void updateDocument(final int documentId, final Long updateId, final Integer repositoryId, final String oai,
            final String docClass) {

        String docClassString = (docClass != null) ? ", doc_class = ?" : "";

        String sql = "UPDATE document SET "
                //            + "id_update = ?, oai = ?, id_repository = ?"//, url = ?, url_tr = ? "
                + "oai = ?, id_repository = ?"//, url = ?, url_tr = ? "

                + docClassString + " "
                + "WHERE id_document = ?";

        PreparedStatementSetter preparedStatementSetter = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                Integer index = 1;
                //ps.setLong(index++, updateId);
                ps.setString(index++, oai);
                ps.setInt(index++, repositoryId);

                if (docClass != null) {
                    ps.setString(index++, docClass);
                }

                ps.setInt(index++, documentId);
            }
        };

        jdbcTemplate.update(sql, preparedStatementSetter);

    }

    /**
     * Set document deleted column to val. Set Parent document as deleted if
     * appropriate
     *
     * @param documentId
     * @param val
     */
    @Override
    public void setDocumentDeleted(final Integer documentId, final DeletedStatus val) {
        logger.debug("updateDocumentStatus" + documentId, this.getClass());

        String deletedDateTimeUpdate = ", date_time_record_deleted = NOW()";
        String sql;
        if (val.equals(DeletedStatus.DELETED)) {
            sql = "UPDATE document SET deleted = ?" + deletedDateTimeUpdate + " WHERE id_document = ?";
        } else {
            sql = "UPDATE document SET deleted = ? WHERE id_document = ?";
        }

        PreparedStatementSetter preparedStatementSetter = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, val.getValue());
                ps.setInt(2, documentId);
            }
        };

        jdbcTemplate.update(sql, preparedStatementSetter);

//        // If document is disabled (i.e. not allowed)
//        // Check parent and if all child documents are disabled, mark parent as disabled
//        if (val != DeletedStatus.ALLOWED) {
//
//            // Get parent ID
//            Integer parentId = duplicatesDAO.getParentId(documentId);
//            if (parentId != null) {
//                // Get list of child documents based on parent ID
//                List<Integer> childIds = duplicatesDAO.getChildrenIds(parentId);
//
//                // Iterate over all child ID's, if one of them is accessible, the
//                // bool will be set to false.
//                Boolean DisableParent = true;
//                for (int childId : childIds) {
//                    int documentStatus = this.getDocumentDeletedStatus(childId);
//                    logger.debug("Child ID: " + childId, this.getClass());
//                    if (documentStatus == 0) {
//                        logger.debug("Document " + childId + " is accessible, do not disable parent", this.getClass());
//                        DisableParent = false;
//                        break;
//                    }
//                }
//
//                if (DisableParent) {
//                    // disable parent
//                    logger.debug("Deleting Parent ID: " + parentId, this.getClass());
//                    this.setDocumentDeleted(parentId, DeletedStatus.DELETED);
//                }
//            }
//        }    
    }

    /**
     * Get document deleted column.
     *
     * @param documentId
     *
     * @return true if document is marked as deleted
     */
    @Override
    public Integer getDocumentDeletedStatus(final Integer documentId) {
        try {
            String sql = "SELECT deleted FROM document WHERE id_document = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{documentId}, Integer.class);
        } catch (org.springframework.dao.EmptyResultDataAccessException exc) {
            return 1;
        }
    }

    /**
     * Get all documents from certain repository.
     *
     * @param repositoryId
     * @return
     */
    @Override
    public List<RepositoryDocument> getRepositoryDocumentsByRepositoryId(Integer repositoryId) {

        return getRepositoryDocumentsByRepositoryId(repositoryId, null);

    }

    /**
     * Get all documents from certain repository. Only documents with specified pdf
     * status will be returned.
     *
     * @param repositoryId
     * @param status The PDF status
     * @return
     */
    @Override
    public List<RepositoryDocument> getRepositoryDocumentsByRepositoryId(
            final Integer repositoryId, final Integer status) {

        String sql = "SELECT * "
                + "FROM document d "
                + "WHERE id_repository = ?";
        if (status != null) {
            sql += " AND d.pdf_status = ?";
        }

        PreparedStatementSetter preparedStatementSetter = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, repositoryId);
                if (status != null) {
                    ps.setInt(2, status);
                }
            }
        };

        RowMapper<RepositoryDocument> rowMapper = new RepositoryDocumentMapper();
        List<RepositoryDocument> documents = jdbcTemplate.query(sql, preparedStatementSetter, rowMapper);

        return documents;
    }

    @Override
    public List<RepositoryDocument> getRepositoryDocumentsByRepositoryId(Integer repositoryId, Integer offset, Integer limit) {
        String sql = "SELECT *  FROM document d  WHERE id_repository = ? order by id_document LIMIT ?, ?";

        return jdbcTemplate.query(sql, new Object[]{repositoryId, offset, limit}, new RepositoryDocumentMapper());
    }

    /**
     * Get all documents from certain repository. Only documents with specified 
     * status will be returned
     * 
     * @param repositoryId
     * @param status
     * @param consumer
     */
    @Override
    public void streamRepositoryDocumentsByRepositoryId(
            final Integer repositoryId, final DeletedStatus status, Consumer<RepositoryDocument> consumer) {

        logger.info("Start streaming repository documents ...");
        logger.info("Repository ID = {}", repositoryId);
        if (status != null) {
            logger.info("Deleted status = {}", status.getValue());
        }
        long start, end;

        int limit = 1000;
        int offset = 0;
        List<RepositoryDocument> documents;
        do {
            StringBuilder sql = new StringBuilder("SELECT * FROM document d WHERE d.id_repository = ? ");

            if (status != null) {
                sql.append(" AND d.deleted = ? ");
            }

            sql.append(" ORDER BY id_document ASC  LIMIT " + offset + ", " + limit + " ");

            PreparedStatementSetter preparedStatementSetter = new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setInt(1, repositoryId);
                    if (status != null) {
                        ps.setInt(2, status.getValue());
                    }
                }
            };

            RowMapper<RepositoryDocument> rowMapper = new RepositoryDocumentMapper();
            logger.info("Executing SQL: {}", sql);
            start = System.currentTimeMillis();
            documents = jdbcTemplate.query(sql.toString(), preparedStatementSetter, rowMapper);
            end = System.currentTimeMillis();
            logger.info("Fetched {} rows in {} ms", documents.size(), end - start);
            documents.forEach(consumer::accept);
            offset += documents.size();
        } while (documents.size() >= limit);
        logger.info("Streaming repository documents finished");
    }

    @Override
    public Integer countRepositoryDocuments(int repositoryId) {
        String sql = "select count(*) as cnt from document d where d.id_repository = ?";
        return this.jdbcTemplate.queryForObject(sql, Integer.class, repositoryId);
    }
}
