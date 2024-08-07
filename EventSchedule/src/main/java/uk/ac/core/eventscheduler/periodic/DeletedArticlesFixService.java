package uk.ac.core.eventscheduler.periodic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.common.model.legacy.DocumentRawMetadata;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.database.service.document.RawMetadataDAO;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.eventscheduler.database.DeletedArticlesFixDao;
import uk.ac.core.supervisor.client.SupervisorClient;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class used to be doing the next job:
 * <p>
 *     Get batch of documents that have deleted status and check if it's `truly` deleted.
 * </p>
 * <p>
 *     The reason this job launched is a bug in CORE data.
 *     By now, March 5, 2024, the bug seems to be gone.
 *     That's why the periodic task is no longer needed.
 * </p>
 */
@Deprecated
@Service
public class DeletedArticlesFixService {
    private static final Logger log = LoggerFactory.getLogger(DeletedArticlesFixService.class);
    private static final int BATCH_SIZE = 200_000;
    private static final String HEADER_STATUS_DELETED_REGEX = "<header\\s+status\\s*=\\s*\"deleted\"\\s*>";

    private final RepositoryDocumentDAO repositoryDocumentDAO;
    private final RawMetadataDAO rawMetadataDAO;
    private final SupervisorClient supervisorClient;
    private final DeletedArticlesFixDao dao;

    private int totalCount;
    private int fixedCount;
    private int deletedCount;
    private int failedCount;

    @Autowired
    public DeletedArticlesFixService(RepositoryDocumentDAO repositoryDocumentDAO, RawMetadataDAO rawMetadataDAO, SupervisorClient supervisorClient, DeletedArticlesFixDao dao) {
        this.repositoryDocumentDAO = repositoryDocumentDAO;
        this.rawMetadataDAO = rawMetadataDAO;
        this.supervisorClient = supervisorClient;
        this.dao = dao;
        this.totalCount = 0;
        this.fixedCount = 0;
        this.deletedCount = 0;
        this.failedCount = 0;
    }

//    @Scheduled(cron = "0 0 3/5 * * *")
    public void invoke() {
        this.process(this.dao.getBatch(BATCH_SIZE));
    }

    public void process(List<Integer> documentIds) {
        try {
            for (Integer docId: documentIds) {
                this.totalCount++;
                DocumentRawMetadata rawMetadata = this.rawMetadataDAO.getDocumentRawMetadataByCoreID(docId);
                Boolean recordDeleted = this.isRecordDeleted(rawMetadata.getMetadata());
                RepositoryDocument article = this.repositoryDocumentDAO.getRepositoryDocumentById(docId);
                log.info("Article {}: deleted status = {}", docId, article.getDeletedStatus());
                if (recordDeleted != null) {
                    if (recordDeleted) {
                        // article raw metadata says this article should not be deleted
                        // so set it ALLOWED
                        this.repositoryDocumentDAO.setDocumentDeleted(docId, DeletedStatus.ALLOWED);
                        this.supervisorClient.sendIndexItemRequest(docId, DeletedStatus.ALLOWED);
                        log.info("Article {}: changed deleted status from {} to {}",
                                docId, DeletedStatus.DELETED, DeletedStatus.ALLOWED);
                        this.fixedCount++;
                    } else {
                        // article raw metadata says this article is deleted
                        // so do nothing
                        log.info("Article {}: deleted status valid", docId);
                        this.deletedCount++;
                    }
                } else {
                    this.failedCount++;
                    log.warn("Unable to check article raw metadata due to an exception");
                    log.warn("Check the logs above for the details");
                }
            }
        } catch (Exception e) {
            log.error("Exception during sending SupervisorClient request", e);
        }
    }

    private Boolean isRecordDeleted(String rawXml) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder();
            Document document = documentBuilder.parse(new ByteArrayInputStream(rawXml.getBytes()));
            Element recordTag = document.getDocumentElement();
            Element headerTag = (Element) recordTag.getElementsByTagName("header").item(0);
            if (headerTag.hasAttribute("status")) {
                String value = headerTag.getAttribute("status");
                return value.equals("deleted");
            } else {
                return false;
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("Exception while checking if metadata record is deleted", e);
            log.info("XML parser failed to read the raw metadata");
            log.info("Try alternative method");
            return this.isRecordDeletedRegexMethod(rawXml);
        }
    }

    private Boolean isRecordDeletedRegexMethod(String rawXml) {
        Pattern pattern = Pattern.compile(HEADER_STATUS_DELETED_REGEX);
        Matcher matcher = pattern.matcher(rawXml);
        return matcher.matches();
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getFixedCount() {
        return fixedCount;
    }

    public int getDeletedCount() {
        return deletedCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void resetNumbers() {
        this.totalCount = 0;
        this.fixedCount = 0;
        this.deletedCount = 0;
        this.failedCount = 0;
    }
}
