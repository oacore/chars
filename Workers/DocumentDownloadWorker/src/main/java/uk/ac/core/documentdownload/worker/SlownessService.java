package uk.ac.core.documentdownload.worker;

import org.slf4j.LoggerFactory;
import uk.ac.core.common.model.task.TaskStatus;

/**
 *
 * SlownessService - service to check if the documentdownload is going
 * embarassibly slow The ehuristic is quite simple, wait for the first 100
 * documents, if it takes more than TIME_PER_DOCUMENT_TRESHOLD to complete, it
 * means that the repository is going really slow Or that there are lots of
 * timeouts. In this case is advisable to stop and drop the repo
 *
 * @author mc26486
 */
public class SlownessService {

    private Long startTime;
    private TaskStatus taskStatus;
    private static final Double TIME_PER_DOCUMENT_TRESHOLD = 20000.0; //10 seconds
    private static final Double SUCCESS_TRESHOLD = 0.75;
    private static final Double WARMUP_PERIOD = 100 * TIME_PER_DOCUMENT_TRESHOLD; //it's 16 minutes
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(SlownessService.class);

    public SlownessService(TaskStatus documentDownloadWorkerStatus) {
        this.taskStatus = documentDownloadWorkerStatus;
        this.startTime = System.currentTimeMillis();
    }

    public Boolean isTheTaskSlow() {
        double duration = System.currentTimeMillis() - startTime;
        if (duration > WARMUP_PERIOD) {
            Double timePerDocument =  duration/taskStatus.getProcessedCount();
            Double percentOfSuccess = taskStatus.getSuccessfulCount() / (double) taskStatus.getProcessedCount();
            logger.warn("---SLOWNESS CHECK --- ");
            logger.warn("Duration: " + duration);
            logger.warn("Success rate: " + percentOfSuccess);
            logger.warn("Time per document: " + timePerDocument);
            if (timePerDocument > TIME_PER_DOCUMENT_TRESHOLD && percentOfSuccess < SUCCESS_TRESHOLD) {
                logger.error("---SLOWNESS DETECTED --- ");
                return true;
            }
        }
        return false;

    }

}
