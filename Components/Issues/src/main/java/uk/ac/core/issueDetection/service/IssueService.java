package uk.ac.core.issueDetection.service;

import uk.ac.core.common.model.legacy.ActionType;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.issueDetection.model.CompactIssueBO;
import uk.ac.core.issueDetection.model.IssueBO;
import java.util.List;
import java.util.Optional;

/**
 * Issue service.
 */
public interface IssueService {
    IssueBO saveIssue(IssueBO issue);

    void saveIssues(List<IssueBO> issues);

    Optional<IssueBO> getIssue(CompactIssueBO compactIssueBO);

    void deleteIssues(int repositoryId, TaskType taskType);
}