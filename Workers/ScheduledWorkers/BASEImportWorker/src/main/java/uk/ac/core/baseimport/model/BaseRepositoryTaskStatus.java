package uk.ac.core.baseimport.model;

import uk.ac.core.common.model.task.TaskItemStatus;

/**
 * Base repository task status.
 */
public class BaseRepositoryTaskStatus extends TaskItemStatus {

    private final boolean duplicate;
    private final String oaiPmhEndpoint;

    private BaseRepositoryTaskStatus(boolean isDuplicate, String oaiPmhEndpoint) {
        this.duplicate = isDuplicate;
        this.oaiPmhEndpoint = oaiPmhEndpoint;
    }

    public String getOaimPmhEndpoint() {
        return oaiPmhEndpoint;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private boolean duplicate;
        private String oaiPmhEndpoint;

        private Builder() {

        }

        public Builder duplicate(boolean duplicate) {
            this.duplicate = duplicate;
            return this;
        }

        public Builder oaiPmhEndpoint(String oaiPmhEndpoint) {
            this.oaiPmhEndpoint = oaiPmhEndpoint;
            return this;
        }

        public BaseRepositoryTaskStatus build() {
            return new BaseRepositoryTaskStatus(duplicate, oaiPmhEndpoint);
        }
    }
}