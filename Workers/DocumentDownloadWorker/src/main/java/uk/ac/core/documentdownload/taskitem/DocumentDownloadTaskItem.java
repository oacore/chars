package uk.ac.core.documentdownload.taskitem;

import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.legacy.RepositoryDocumentBase;

/**
 *
 * @author mc26486
 */
public final class DocumentDownloadTaskItem implements TaskItem {

    private RepositoryDocumentBase repositoryDocumentBase;

    public RepositoryDocumentBase getRepositoryDocumentBase() {
        return repositoryDocumentBase;
    }

    public void setRepositoryDocumentBase(RepositoryDocumentBase repositoryDocumentBase) {
        this.repositoryDocumentBase = repositoryDocumentBase;
    }

    @Override
    public String toString() {
        return "DocumentDownloadTaskItem{" + "repositoryDocumentBase=" + repositoryDocumentBase + '}';
    }

}
