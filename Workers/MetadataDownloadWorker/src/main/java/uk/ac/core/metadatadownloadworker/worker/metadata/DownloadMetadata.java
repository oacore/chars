
package uk.ac.core.metadatadownloadworker.worker.metadata;

import uk.ac.core.metadatadownloadworker.worker.metadata.oaipmh.MetadataDownloadFailureIssueCallback;
import java.io.OutputStream;

/**
 *
 * @author samuel
 */
public interface DownloadMetadata {
    // Actually does the downloading of metadata
    void downloadMetadata(MetadataDownloadFailureIssueCallback metadataDownloadFailureIssueCallback, OutputStream outputStream) throws Exception;
}
