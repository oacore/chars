package uk.ac.core.ExtendedMetadataProcessWorker.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.ExtendedMetadataProcessWorker.worker.ExtendedMetadataProcessWorker;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.worker.QueueWorker;

import java.io.UnsupportedEncodingException;

@RestController
public class BatchController {
    private static final Logger log = LoggerFactory.getLogger(StarterController.class);

    @Autowired
    QueueWorker queueWorker;

    @GetMapping("/extended_metadata_process/batched")
    public void batchedMode(
            @RequestParam(name = "repositoryId") final Integer repositoryId,
            @RequestParam(name = "offset", required = false, defaultValue = "0") final int offset) {
        ExtendedMetadataProcessWorker batchedWorker = (ExtendedMetadataProcessWorker) this.queueWorker;
        batchedWorker.setBatchMode(true);

        log.info("Batch mode: {}", batchedWorker.isBatchMode() ? "YES" : "NO");

        if (offset != 0) {
            batchedWorker.setOffset(offset);
        }
        try {
            log.info("Repository size: {}", batchedWorker.getRepositorySize());
            do {
                log.info("Ready to process next batch ...");
                log.info("Offset: {}", batchedWorker.getOffset());

                TaskDescription taskDescription = new TaskDescription();
                RepositoryTaskParameters repositoryTaskParameters = new RepositoryTaskParameters(repositoryId);
                taskDescription.setTaskParameters(new Gson().toJson(repositoryTaskParameters));
                taskDescription.setType(TaskType.EXTENDED_METADATA_PROCESS);

                batchedWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
            } while (batchedWorker.hasNextBatch());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
