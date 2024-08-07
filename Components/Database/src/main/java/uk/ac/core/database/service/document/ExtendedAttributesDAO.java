package uk.ac.core.database.service.document;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.ac.core.database.model.DocumentMetadataExtendedAttributes;

/**
 *
 */
public interface ExtendedAttributesDAO {

    /**
     * Saves Extended Attributes about a document, obtained via the repository
     *
     * @param idDocument
     * @param repositoryMetadataRecordPublishDate The DateTime when a Metadata
     * Record when public
     * @param attachmentCount The number of attachments related to the metadata
     * record. May include private attachments. Best not to make this public...
     */
    boolean save(int idDocument, LocalDateTime repositoryMetadataRecordPublishDate, Integer attachmentCount);

    /**
     * Saves Extended Attributes about a document, obtained via the repository
     * @param documentMetadataExtentedAttributes
     */
    boolean save(DocumentMetadataExtendedAttributes documentMetadataExtentedAttributes);
    
    /**
     * 
     * @param idDocument
     * @return 
     */
    Optional<DocumentMetadataExtendedAttributes> get(int idDocument);
}
