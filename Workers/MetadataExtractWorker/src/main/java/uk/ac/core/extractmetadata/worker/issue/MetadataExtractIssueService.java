package uk.ac.core.extractmetadata.worker.issue;

import uk.ac.core.common.model.legacy.ArticleMetadata;

/**
 * Metadata extract issue service.
 */
public interface MetadataExtractIssueService {

    /**
     * Cleans all stale repository specific metadata extract issues.
     * @param repositoryId target repository id
     */
    void cleanMetadataExtractIssuesForRepo(int repositoryId);

    /**
     * Reports metadata extract issues.
     *
     * @param repositoryId target repository id
     * @param articleMetadata article metadata to check for issues
     */
    void reportIssues(int repositoryId, ArticleMetadata articleMetadata);
}