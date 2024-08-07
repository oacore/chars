package uk.ac.core.documentdownload.worker;

import uk.ac.core.issueDetection.util.IssueType;

/**
 *
 * @author mc26486
 */
public class DocumentDownloadIssueException extends Exception {

    private IssueType issueType;
    private String message;

    public DocumentDownloadIssueException(IssueType issueType, String message) {
        super(message);
        this.issueType = issueType;
    }

    public IssueType getIssueType() {
        return issueType;
    }

    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
