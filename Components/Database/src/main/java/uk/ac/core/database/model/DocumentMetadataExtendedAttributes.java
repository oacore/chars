package uk.ac.core.database.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author samuel
 */
@Entity
@Table(name = "document_metadata_extended_attributes")
public class DocumentMetadataExtendedAttributes {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_document")
    private int idDocument;
    
    @Column(name = "repository_metadata_public_release_date")
    private LocalDateTime repositoryMetadataPublicReleaseDate;
    
    @Column(name = "attachment_count")
    private int attachmentCount;

    public DocumentMetadataExtendedAttributes(int idDocument) {
        this.idDocument = idDocument;
    }   
    
    public int getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(int idDocument) {
        this.idDocument = idDocument;
    }

    public LocalDateTime getRepositoryMetadataPublicReleaseDate() {
        return repositoryMetadataPublicReleaseDate;
    }

    public void setRepositoryMetadataPublicReleaseDate(LocalDateTime repositoryMetadataPublicReleaseDate) {
        this.repositoryMetadataPublicReleaseDate = repositoryMetadataPublicReleaseDate;
    }

    /**
     * Count of Attachments provided by the repository.
     * 
     * This may include non-public attachments. Must include non-pdfs if available
     * @return count of attachments
     */
    public int getAttachmentCount() {
        return attachmentCount;
    }
    
    /**
     * Count of Attachments provided by the repository.
     * 
     * This may include non-public attachments. Must include non-pdfs if available
     */
    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }
}
