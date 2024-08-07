package uk.ac.core.filesystemdocumentworker.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;
import uk.ac.core.elasticsearch.entities.ElasticSearchArticleMetadata;
import uk.ac.core.elasticsearch.services.ArticleMetadataService;
import uk.ac.core.elasticsearch.services.model.CompleteArticleBO;
import uk.ac.core.singleitemworker.SingleItemWorker;
import java.io.File;
import java.io.IOException;

public class DocumentFilesystemWorker extends SingleItemWorker {

    @Autowired
    private ArticleMetadataService articleMetadataService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentFilesystemWorker.class);

    @Override
    public TaskItemStatus process(TaskDescription taskDescription) {
        String params = taskDescription.getTaskParameters();
        SingleItemTaskParameters singleItemTaskParameters;
        boolean success = true;

        try {
                singleItemTaskParameters = objectMapper.readValue(params, SingleItemTaskParameters.class);

            Integer documentId = singleItemTaskParameters.getArticle_id();
            CompleteArticleBO articleBO = articleMetadataService.getArticleMetadata(String.valueOf(documentId));

            File folder = new File(String.valueOf(articleBO.getRepositories().stream().findFirst().get().getId()));
            if(!folder.exists()) {
                folder.mkdir();
            }
            File file = new File(String.format("%s/%s", articleBO.getRepositories().stream().findFirst().get().getId(),
                    articleBO.getCompactArticleBO().getDocumentId()));
            if (!file.exists()) {
                file.createNewFile();
            }
            objectMapper.writeValue(file, articleBO);

        } catch (JsonProcessingException e) {
            LOGGER.error("Can't read task description.", e);
            success = false;
        } catch (IOException e) {
            LOGGER.error("Can't create the target file.", e);
            success = false;
        }

        TaskItemStatus taskItemStatus = new TaskItemStatus();
        taskItemStatus.setSuccess(success);
        taskItemStatus.setTaskId(taskDescription.getUniqueId());
        return taskItemStatus;
    }
}
