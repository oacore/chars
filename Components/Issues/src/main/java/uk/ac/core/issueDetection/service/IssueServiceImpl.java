package uk.ac.core.issueDetection.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.legacy.ActionType;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.issueDetection.data.entity.Issue;
import uk.ac.core.issueDetection.data.repository.IssueRepository;
import uk.ac.core.issueDetection.data.repository.resetissue.BackwardCompatibilityIssueDao;
import uk.ac.core.issueDetection.model.CompactIssueBO;
import uk.ac.core.issueDetection.model.IssueBO;
import uk.ac.core.issueDetection.model.IssueConverter;
import uk.ac.core.issueDetection.util.GetIssueIdUtil;
import java.util.List;
import java.util.Optional;


public class IssueServiceImpl implements IssueService {

    private static final Logger logger = LoggerFactory.getLogger(IssueServiceImpl.class);

    private final IssueRepository elasticSearchIssueRepository;
    private final BackwardCompatibilityIssueDao backwardCompatibilityIssueDao;

    public IssueServiceImpl(IssueRepository elasticSearchIssueRepository, BackwardCompatibilityIssueDao backwardCompatibilityIssueDao) {
        this.elasticSearchIssueRepository = elasticSearchIssueRepository;
        this.backwardCompatibilityIssueDao = backwardCompatibilityIssueDao;
    }

    @Override
    public IssueBO saveIssue(IssueBO issue) {
        return IssueConverter.toIssueBO(elasticSearchIssueRepository.save(IssueConverter.toIssue(issue)));
    }

    @Override
    public void saveIssues(List<IssueBO> issues) {
        for (IssueBO issue : issues) {
            saveIssue(issue);
        }
    }

    @Override
    public Optional<IssueBO> getIssue(CompactIssueBO issueBO) {
        Optional<Issue> issue = this.elasticSearchIssueRepository.findById(GetIssueIdUtil.getIssueId(issueBO));
        return issue.map(IssueConverter::toIssueBO);
    }

    @Override
    public void deleteIssues(int repositoryId, TaskType actionType) {
        backwardCompatibilityIssueDao.deleteIssues(repositoryId, actionType);
    }
}