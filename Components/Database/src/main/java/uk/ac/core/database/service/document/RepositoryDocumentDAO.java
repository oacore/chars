package uk.ac.core.database.service.document;

import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.common.model.article.PDFUrlSource;
import uk.ac.core.common.model.legacy.Language;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.common.model.legacy.RepositoryDocumentBase;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 *
 * @author lucasanastasiou
 */
public interface RepositoryDocumentDAO {

    void setDocumentTextStatus(Integer articleId, Integer statusFlag);
    RepositoryDocument getRepositoryDocumentById(Integer articleId);

    Language getDocumentLanguage(Integer id);

    DeletedStatus getDeletedStatus(Integer id);
    
    void setDocumentIndexStatus(Integer documentId, boolean succeeded);

    Optional<LocalDateTime> getIndexLastAttempt(Integer documentId);

    List<RepositoryDocumentBase> getDocuments(String repositoryId, boolean prioritiseOldDocumentsForDownload,
                                              Date fromDate, Date toDate, Long offset, Long limit);

    Integer countRepositoryDocumentsWithFulltext(int repositoryId);

    Integer addDocument(Long updateId, Integer repositoryId, String oai, String url, String docClass);

    Integer addDocument(Long updateId, Integer repositoryId, String oaiIdentifier, Map<String, PDFUrlSource> pdfUrls, String docClass);

    void setDocumentMetadataStatus(Integer documentId);

    void setDocumentDateStamp(Integer documentId, Date dateStamp);

    void updateDocuments(Integer documentId, Long updateId, Integer repositoryId, String oaiIdentifier,Map<String, PDFUrlSource> pdfUrls, String docClass);

    void setDocumentDeleted(Integer documentId, DeletedStatus deleted);

    Integer getDocumentDeletedStatus(Integer documentId);

    List<RepositoryDocument> getRepositoryDocumentsByRepositoryId(Integer repositoryId, Integer status);

    List<RepositoryDocument> getRepositoryDocumentsByRepositoryId(Integer repositoryId);

    List<RepositoryDocument> getRepositoryDocumentsByRepositoryId(Integer repositoryId, Integer offset, Integer limit);
    
    /**
     * Streams the list of repository documents to the Consumer.
     * 
     * This interface method allows the implementation to stream results to the
     * user by running pagination internally. This prevents all results from
     * being loaded into memory. How it is done is decided by the implementer
     * 
     * @param repositoryId
     * @param status
     * @param consumer 
     */
    void streamRepositoryDocumentsByRepositoryId(Integer repositoryId, DeletedStatus status, Consumer<RepositoryDocument> consumer);

    Integer countRepositoryDocuments(int repositoryId);
}
