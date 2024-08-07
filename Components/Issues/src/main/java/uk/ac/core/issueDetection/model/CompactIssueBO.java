package uk.ac.core.issueDetection.model;

import org.springframework.lang.Nullable;
import uk.ac.core.issueDetection.util.IssueType;

public class CompactIssueBO {
    private final long repositoryId;
    private final Long documentId;
    private final IssueType type;

    public CompactIssueBO(long repositoryId, @Nullable Long documentId, IssueType type) {
        this.repositoryId = repositoryId;
        this.documentId = documentId == null ? 0 : documentId;
        this.type = type;
    }

    public long getRepositoryId() {
        return repositoryId;
    }

    public long getDocumentId() {
        return documentId;
    }

    public IssueType getType() {
        return type;
    }
}
