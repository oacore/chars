package uk.ac.core.issueDetection.data.repository.resetissue;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.stereotype.Repository;
import uk.ac.core.common.model.legacy.ActionType;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.issueDetection.data.entity.Issue;


@Repository
public class BackwardCompatibilityIssueDaoImpl implements BackwardCompatibilityIssueDao {

    private static final String INDEX_ALIAS = "issues-alias";
    private static final String TYPE_NAME = "issue";

    private static final Logger LOG = LoggerFactory.getLogger(BackwardCompatibilityIssueDaoImpl.class);

    private final ElasticsearchTemplate elasticsearchTemplate;

    public BackwardCompatibilityIssueDaoImpl(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;}

    @Override
    public void deleteIssues(int repositoryId, TaskType actionType) {
        LOG.info(String.format("Deleting issues on %s for repository: %d", actionType, repositoryId));
        this.elasticsearchTemplate.delete(createDeleteQuery(repositoryId, actionType), Issue.class);
    }

    private DeleteQuery createDeleteQuery(int repositoryId, TaskType taskType){
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setScrollTimeInMillis(1000L);
        deleteQuery.setIndex(INDEX_ALIAS);
        BoolQueryBuilder query = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("repositoryId", repositoryId))
                .must(QueryBuilders.termQuery("actionType", taskType.getName()));
        deleteQuery.setQuery(query);
        deleteQuery.setPageSize(10);
        deleteQuery.setType(TYPE_NAME);
        return deleteQuery;
    }
}