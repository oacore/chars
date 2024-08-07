package uk.ac.core.issueDetection.model;

import uk.ac.core.issueDetection.data.entity.Issue;

/**
 * Issue converter.
 */
public final class IssueConverter {

    private IssueConverter() {

    }

    public static Issue toIssue(IssueBO issueBO) {
        return new Issue(
                issueBO.getRepositoryId(),
                issueBO.getDocumentId(),
                issueBO.getType(),
                issueBO.getMessage(),
                issueBO.getDetails(),
                issueBO.getOai());
    }

    public static IssueBO toIssueBO(Issue issue) {
        return new IssueBO.Builder(issue.getRepositoryId())
                .documentId(issue.getDocumentIdentifier())
                .issueType(issue.getType())
                .message(issue.getMessage())
                .oai(issue.getOai())
                .build();
    }
}
