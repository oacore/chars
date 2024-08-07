package uk.ac.core.supervisor.controller.tasks;

import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.queue.QueueService;

/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
@RestController
public class GrobidController {

    @Autowired
    FilesystemDAO filesystemDAO;

    @Autowired
    QueueService queueService;

    Logger logger = LoggerFactory.getLogger("grobidController");

    @RequestMapping("/grobid/{taskType}/batch/from/{repositoryId}")
    public String startGrobidBatchProcess(@PathVariable(value = "taskType") String taskType, @PathVariable(value = "repositoryId") String repositoryId) throws UnsupportedEncodingException {

        if (!taskType.equalsIgnoreCase("affiliation")
                && !taskType.equalsIgnoreCase("citation")
                && !taskType.equalsIgnoreCase("all")) {
            return "Accepted types are 'affiliation', 'citation', 'all'";
        }

        logger.debug("Loading files ids...");
        List<Integer> teiFilesIds = filesystemDAO.getAllTeiFilesIDs(Integer.valueOf(repositoryId));
        logger.debug(teiFilesIds.size() + " files loaded.");
        int count = 1;
        if (teiFilesIds != null && !teiFilesIds.isEmpty()) {
            for (Integer articleId : teiFilesIds) {
                count++;
                TaskDescription taskDescription = new TaskDescription();

                List<TaskType> workflowTaskList = new ArrayList<>();
                if (taskType.equalsIgnoreCase("all")) {
                    workflowTaskList.add(TaskType.GROBID_CITATION_PARSER_ITEM);
                    workflowTaskList.add(TaskType.GROBID_AFFILIATION_PARSER_ITEM);
                    taskDescription.setType(TaskType.GROBID_CITATION_PARSER_ITEM);
                    taskDescription.setRoutingKey(TaskType.GROBID_CITATION_PARSER_ITEM.getName());
                } else {
                    taskDescription.setType(TaskType.fromString("grobid-" + taskType + "-parser-item"));
                    taskDescription.setRoutingKey(TaskType.fromString("grobid-" + taskType + "-parser-item").getName());
                }
                taskDescription.setTaskList(workflowTaskList);
                SingleItemTaskParameters taskParameters = new SingleItemTaskParameters(articleId);
                
                taskDescription.setTaskParameters(new Gson().toJson(taskParameters));
                queueService.publish(taskDescription);
                logger.debug((count / teiFilesIds.size() * 100) + " % message queued loaded.");
            }
        }
        return "Done";
    }
}
