package uk.ac.core.issueDetection.data.repository.resetissue;

import uk.ac.core.common.model.task.TaskType;

/**
 * This DAO is special case for the backward compatibility with the indexes,
 * which are not used anymore, in the absence of alias value in {@link org.springframework.data.elasticsearch.annotations.Document}.
 */
public interface BackwardCompatibilityIssueDao {

    /**
     * Deletes issues by repository id.
     *
     * @param repositoryId repository id
     * @param actionType   action type
     */
    void deleteIssues(int repositoryId, TaskType actionType);
}