package uk.ac.core.database.service.document;

import uk.ac.core.common.model.legacy.DocumentTdmStatus;

/**
 * @author Giorgio Basile
 * @since 16/06/2017
 */
public interface DocumentTdmStatusDAO {

    DocumentTdmStatus getDocumentTdmStatus(Integer documentId);

    void insertOrUpdateTdmStatus(DocumentTdmStatus documentTdmStatus);

}
