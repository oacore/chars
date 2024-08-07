package uk.ac.core.metadatadownloadworker.worker.metadata.oaipmh;

import uk.ac.core.common.util.datastructure.Tuple;

/**
 * Metadata download failure issue callback.
 */
@FunctionalInterface
public interface MetadataDownloadFailureIssueCallback {
    void reportIssue(String message, Tuple<String, String> details);
}
