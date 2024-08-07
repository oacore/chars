package uk.ac.core.database.service.document;

import java.util.List;
import java.util.Map;
import uk.ac.core.common.model.article.PDFUrlSource;
import uk.ac.core.common.model.legacy.DocumentUrl;

/**
 *
 * @author lucasanastasiou
 */
public interface DocumentUrlDAO {

    List<DocumentUrl> load(Integer repositoryId);

    boolean insertDocumentUrl(DocumentUrl documentUrl);

    void synchroniseUrlsAsString(Integer documentId, Map<String, PDFUrlSource> urls);

    List<String> getUrlByDocId(Integer docId);
}
