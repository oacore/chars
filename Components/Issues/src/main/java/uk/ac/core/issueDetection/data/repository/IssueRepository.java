package uk.ac.core.issueDetection.data.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import uk.ac.core.issueDetection.data.entity.Issue;

public interface IssueRepository extends ElasticsearchRepository<Issue, String> {

}