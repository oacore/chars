package uk.ac.core.issueDetection.model;

import uk.ac.core.issueDetection.util.IssueType;

import java.util.Map;

/**
 * Issue BO.
 */
public final class IssueBO {
    private final long repositoryId;
    private final long documentId;
    private final IssueType type;
    private final String message;
    private final Map<String, String> details;
    private final String oai;

    public IssueBO(long repositoryId, long documentId, IssueType type, String message, Map<String, String> details, String oai) {
        this.repositoryId = repositoryId;
        this.documentId = documentId;
        this.type = type;
        this.message = message;
        this.details = details;
        this.oai = oai;
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

    public String getMessage() {
        return message;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public String getOai() {
        return oai;
    }

    public final static class Builder {
        private final long repositoryId;
        private long documentId = 0L;
        private IssueType issueType;
        private String message;
        private Map<String, String> details;
        private String oai;

        public Builder(long repositoryId) {
            this.repositoryId = repositoryId;
        }

        public Builder documentId(long documentId) {
            this.documentId = documentId;
            return this;
        }

        public Builder issueType(IssueType issueType) {
            this.issueType = issueType;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder details(Map<String, String> details) {
            this.details = details;
            return this;
        }

        public Builder oai(String oai) {
            this.oai = oai;
            return this;
        }

        public IssueBO build() {
            return new IssueBO(repositoryId, documentId, issueType, message, details, oai);
        }
    }
}