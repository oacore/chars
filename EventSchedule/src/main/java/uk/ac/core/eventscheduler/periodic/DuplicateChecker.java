package uk.ac.core.eventscheduler.periodic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.core.common.constants.CHARSConstants;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.eventscheduler.monitoring.WorkersInfoService;
import uk.ac.core.eventscheduler.peeker.DeduplicatePeeker;
import uk.ac.core.queue.QueueInfoService;

/**
 *
 * @author lucasanastasiou
 */
@Component
public class DuplicateChecker {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(DuplicateChecker.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    QueueInfoService queueInfoService;

    @Autowired
    WorkersInfoService workersInfoService;

    @Scheduled(fixedRate = 240000)// every 4 minutes
    public void checkConsistency() {
        logger.debug("Run deduplicate in queue check");
        List<String> queuesInWorkflow = new ArrayList<String>() {
            {
                add(TaskType.METADATA_DOWNLOAD.getName() + CHARSConstants.QUEUE_SUFFIX);
                add(TaskType.EXTRACT_METADATA.getName() + CHARSConstants.QUEUE_SUFFIX);
                add(TaskType.DOCUMENT_DOWNLOAD.getName() + CHARSConstants.QUEUE_SUFFIX);
            }
        };
        
        //discover each tasks workers
        for (String queue : queuesInWorkflow) {                        
            queueInfoService.lookMessagesInQueue(queue, new DeduplicatePeeker());
        }
    }

}
