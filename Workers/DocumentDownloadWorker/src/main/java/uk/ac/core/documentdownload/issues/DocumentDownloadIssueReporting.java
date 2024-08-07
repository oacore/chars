package uk.ac.core.documentdownload.issues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.core.issueDetection.model.IssueBO;
import uk.ac.core.issueDetection.service.IssueService;
import uk.ac.core.issueDetection.util.IssueType;

import java.util.Map;

public class DocumentDownloadIssueReporting {

    private final Logger logger = LoggerFactory.getLogger(DocumentDownloadIssueReporting.class);
    private static final String ISSUE_DETECTED_MSG = "An issue has been detected:\n IssueType: %s : %s";

    private IssueService issueService;
    private int repositoryId;

    public DocumentDownloadIssueReporting(IssueService issueService, int repositoryId) {
        this.issueService = issueService;
        this.repositoryId = repositoryId;
    }

    public IssueBO createIssue(long documentId, IssueType issueType, String message, Map<String, String> details, String oai) {
        return new IssueBO.Builder(this.repositoryId)
                .issueType(issueType)
                .documentId(documentId)
                .message(message)
                .details(details)
                .oai(oai)
                .build();
    }

    public void reportIssue(IssueBO issueBO) {
        logger.debug(String.format(ISSUE_DETECTED_MSG, issueBO.getType().toString(), issueBO.getMessage()));
        issueService.saveIssue(issueBO);
    }

}
