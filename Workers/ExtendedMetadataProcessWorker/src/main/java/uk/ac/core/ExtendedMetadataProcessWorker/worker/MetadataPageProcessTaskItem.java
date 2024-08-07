package uk.ac.core.ExtendedMetadataProcessWorker.worker;

import uk.ac.core.common.model.task.TaskItem;

/**
 *
 * @author samuel
 */
public class MetadataPageProcessTaskItem implements TaskItem {

    private final int documentId;
    private final String oai;

    public MetadataPageProcessTaskItem(int documentId, String oai) {
        this.documentId = documentId;
        this.oai = oai;
    }

    public int getDocumentId() {
        return documentId;
    }

    public String getOai() {
        return oai;
    }
}
