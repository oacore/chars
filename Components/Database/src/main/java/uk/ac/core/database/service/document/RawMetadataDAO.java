package uk.ac.core.database.service.document;

import uk.ac.core.common.model.legacy.DocumentRawMetadata;

/**
 *
 * @author lucasanastasiou
 */
public interface RawMetadataDAO {
    public  DocumentRawMetadata getDocumentRawMetadataByCoreID(Integer articleId);

    DocumentRawMetadata getDocumentRawMetadataByDrmId(Integer drmId);
}
