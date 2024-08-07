package uk.ac.core.database.service.document;

import uk.ac.core.common.model.legacy.PreviewStatus;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author lucasanastasiou
 */
public interface DocumentDAO {

    boolean setPreviewStatus(Integer documentId, PreviewStatus previewStatus);

    Boolean getPreviewStatus(String documentId);

    Long getPreviewCount();

    Integer getDocumentCount();
    
    List<RepositoryDocument> getArticlesByOaiAndRepo(String oaiSuffix, Long repositoryId);
    
    String getArticleDoiById(Integer documentId);

    Long countIndexedDocsSince(LocalDate date);

    void updateIndexedFieldForNotIndexedDocument(Integer documentId);
}
