package uk.ac.core.workers.reindex;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.parameters.ReindexItemTaskParameters;
import uk.ac.core.elasticsearch.exceptions.IndexException;
import uk.ac.core.elasticsearch.services.legacy.ElasticSearchIndexLegacyService;
import uk.ac.core.singleitemworker.LightSingleItemWorker;
import java.util.List;

/**
 *
 * @author lucasanastasiou
 */
public class ReindexWorker extends LightSingleItemWorker {

    @Autowired
    ElasticSearchIndexLegacyService elasticSearchIndexService;

    Logger logger = LoggerFactory.getLogger("ReindexWorker");

    @Override
    public TaskItemStatus process(TaskDescription taskItemDescription) {
        String params = taskItemDescription.getTaskParameters();
        final ReindexItemTaskParameters reindexItemTaskParameters = new Gson().fromJson(params, ReindexItemTaskParameters.class);
        final Integer articleId = reindexItemTaskParameters.getArticle_id();
        final String indexName = reindexItemTaskParameters.getIndexName();

        try {
            elasticSearchIndexService.indexArticle(indexName, articleId);
        } catch (IndexException e) {
            logger.error("Index exception", e);
        }

        return null;
    }

    @Override
    public boolean evaluate(List<TaskItemStatus> results, List<TaskItem> taskItems) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drop() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void pause() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
