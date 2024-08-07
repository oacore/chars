package uk.ac.core.documentdownload.taskitem;

import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.issueDetection.util.IssueType;

/**
 *
 * @author mc26486
 */
public class DocumentDownloadTaskItemStatus extends TaskItemStatus {

    private IssueType issueType;
    private String oai;
    private boolean skipped;
    private String message = "";

    public IssueType getIssueType() {
        return issueType;
    }

    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
        super.setSuccess(false);
    }

    public String getOai() {
        return oai;
    }

    public void setOai(String oai) {
        this.oai = oai;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSkipped() {
        return skipped;
    }

    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

    @Override
    public void setSuccess(boolean success) {
        super.setSuccess(success);
        if (success) {
            this.issueType = null;
        }
    }
}
