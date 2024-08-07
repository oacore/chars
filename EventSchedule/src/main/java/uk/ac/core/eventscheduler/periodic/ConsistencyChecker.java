package uk.ac.core.eventscheduler.periodic;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.core.common.constants.CHARSConstants;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.eventscheduler.monitoring.WorkersInfoService;
import uk.ac.core.queue.QueueInfoService;
import uk.ac.core.workermetrics.data.entity.ScheduledRepository;
import uk.ac.core.workermetrics.data.dao.scheduling.SchedulingRepositoryDAO;
import uk.ac.core.workermetrics.data.state.RepositoryPriority;
import uk.ac.core.workermetrics.data.state.ScheduledState;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author lucasanastasiou
 */
@Component
public class ConsistencyChecker {

    private static final Logger log = LoggerFactory.getLogger(ConsistencyChecker.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//
    @Autowired
    QueueInfoService queueInfoService;

    @Autowired
    WorkersInfoService workersInfoService;

    @Autowired
    SchedulingRepositoryDAO schedulingRepositoryDAO;

    @Scheduled(fixedRate = 120000)// every two minutes
    public void checkConsistency() {
        List<String> queuesInWorkflow = new ArrayList<String>() {
            {
                add(TaskType.METADATA_DOWNLOAD.getName() + CHARSConstants.QUEUE_SUFFIX);
                add(TaskType.EXTRACT_METADATA.getName() + CHARSConstants.QUEUE_SUFFIX);
                add(TaskType.DOCUMENT_DOWNLOAD.getName() + CHARSConstants.QUEUE_SUFFIX);
            }
        };

        List<String> inconsistencies = new ArrayList<>();

        Set<Integer> repositoriesInCHARSSystem = new HashSet<>();

        log.info("Consistency check");
        //discover each tasks workers
        for (String queue : queuesInWorkflow) {

            List<String> workersEndpoints = queueInfoService.getWorkers(queue);

            // and fetch their current status
            List<Integer> repositoriesUnderProcessing = workersInfoService.getRepositoriesInWorkers(workersEndpoints);

            //queue name = extract-metadata-queue , we want the extract-metadata
            String taskString = queue.substring(0, queue.lastIndexOf(CHARSConstants.QUEUE_SUFFIX));
            ScheduledState queueState = ScheduledState.fromTaskType(TaskType.fromString(taskString));
            if (repositoriesUnderProcessing != null) {

                //check consistency against database state
                for (Integer repositoryId : repositoriesUnderProcessing) {
                    ScheduledRepository dbScheduledRepository = schedulingRepositoryDAO.getScheduledRepositoryByRepositoryId(repositoryId);
                    
                    if (dbScheduledRepository!=null && !dbScheduledRepository.getScheduledState().equals(queueState)) {
                        inconsistencies.add(repositoryId + " queueState: " + queueState.name() + "\tdbState: " + dbScheduledRepository.getScheduledState().name());
//                    schedulingRepositoryDAO.updateStatus(dbScheduledRepository, queueState);
                    }

                    repositoriesInCHARSSystem.add(repositoryId);
                }
            }
            List<Message> queuedMessages = queueInfoService.lookMessagesInQueue(queue);

            for (Message m : queuedMessages) {
                String messageTaskString = new String(m.getBody());
                TaskDescription currentWorkingTask = new Gson().fromJson(messageTaskString, TaskDescription.class);
                
                RepositoryTaskParameters repositoryTaskParameters = new Gson().fromJson(currentWorkingTask.getTaskParameters(), RepositoryTaskParameters.class);
                Integer repositoryId = repositoryTaskParameters.getRepositoryId();
                
                // update scheduledRepository of repository id to
                // state as IN_EXTRACT_METADATA_QUEUE
                ScheduledRepository dbScheduledRepository = schedulingRepositoryDAO.getScheduledRepositoryByRepositoryId(repositoryId);

                if (dbScheduledRepository != null) {
                    ScheduledState beforeState = ScheduledState.queueStateFromProcessingState(queueState);

                    if (!dbScheduledRepository.getScheduledState().equals(beforeState)) {
                        inconsistencies.add(dbScheduledRepository.getIdRepository() + " queueState: " + beforeState.name() + "\tdbState: " + dbScheduledRepository.getScheduledState().name());
                        schedulingRepositoryDAO.updateStatus(dbScheduledRepository, beforeState);
                    }
                }

                repositoriesInCHARSSystem.add(repositoryId);

            }
        }

        log.info("Inconsistencies:");
        inconsistencies.forEach(log::info);

        // get all non-PENDING messages from DB
        List<ScheduledRepository> nonPendingRepositoriesFromDB = schedulingRepositoryDAO.getNonPendingRepositories();

        for (ScheduledRepository scheduledRepository : nonPendingRepositoriesFromDB) {
            if (!repositoriesInCHARSSystem.contains(scheduledRepository.getIdRepository())) {
                schedulingRepositoryDAO.updateStatus(scheduledRepository, ScheduledState.PENDING);
            }
        }
        List<Integer> newRepositories = schedulingRepositoryDAO.getUnscheduledRepositories();
        newRepositories.stream().filter((newRepository) -> (!repositoriesInCHARSSystem.contains(newRepository)))
                .forEachOrdered((newRepository) -> {
                    schedulingRepositoryDAO.insertDefaultSchedulingRepositoryEntry(newRepository, ScheduledState.PENDING, RepositoryPriority.NORMAL);
                    log.info(newRepository + " added to scheduled repositories");
                });

    }

}
