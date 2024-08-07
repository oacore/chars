package uk.ac.core.extractmetadata.worker.edgecases;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.database.service.document.RepositoryMetadataDAO;
import uk.ac.core.extractmetadata.worker.oaipmh.GetRecords.ArticleMetadataPersist;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.supervisor.client.SupervisorClient;

@Service
public class DeleteStatusDocument {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ArticleMetadataPersist.class);

    private static final int CROSSREF_ID = 4786;

    private RepositoryDocumentDAO repositoryDocumentDAO;
    private RepositoryMetadataDAO repositoryMetadataDAO;
    private SupervisorClient supervisorClient;
    private FilesystemDAO filesystemDAO;

    @Autowired
    public DeleteStatusDocument(RepositoryDocumentDAO repositoryDocumentDAO, RepositoryMetadataDAO repositoryMetadataDAO, SupervisorClient supervisorClient, FilesystemDAO filesystemDAO) {
        this.repositoryDocumentDAO = repositoryDocumentDAO;
        this.repositoryMetadataDAO = repositoryMetadataDAO;
        this.supervisorClient = supervisorClient;
        this.filesystemDAO = filesystemDAO;
    }


    /**
     * Marks the document ID as deleted.
     * @param documentId
     * @param repositoryId
     */
    public void setStatus(DeletedStatus status,int documentId, int repositoryId, boolean requestIndexUpdateImmediately) {
        repositoryDocumentDAO.setDocumentDeleted(documentId, status);
        filesystemDAO.updateDocumentStatus(documentId, repositoryId, status);
        if (requestIndexUpdateImmediately) {
            try {
                if (status == DeletedStatus.DELETED) {
                    this.supervisorClient.sendIndexItemRequest(documentId, DeletedStatus.DELETED);
                } else {
                    this.supervisorClient.sendIndexItemRequest(documentId);
                }
            } catch (CHARSException ex) {
                logger.warn(ex.getMessage(), ex);
            }
        }
    }
}