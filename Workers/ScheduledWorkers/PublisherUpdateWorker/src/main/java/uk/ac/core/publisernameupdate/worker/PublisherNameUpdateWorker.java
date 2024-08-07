package uk.ac.core.publisernameupdate.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.database.model.PublisherName;
import uk.ac.core.database.service.publishername.PublisherNameDAO;
import uk.ac.core.worker.ScheduledWorker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PublisherNameUpdateWorker extends ScheduledWorker {
    private static final int PER_PAGE = 500;
    private static final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private PublisherNameDAO publisherNameDAO;

    @Override
    public TaskType getTaskType() {
        return TaskType.PUBLISHER_NAME_UPDATE;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * MON")
    public void scheduledStart() {
        this.start();
    }


    @Override
    public List<TaskItemStatus> process(List<TaskItem> taskItems) {
        String uri = "https://api.crossref.org/members?rows=%s&offset=%s";

        int currentPage = 0;
        int totalProcessed = 0;
        int all = 16168;
        List<PublisherName> receivedNames = new ArrayList<>();

        logger.info("Start updating publisher names");

        while(totalProcessed < all) {
            int offset = currentPage * PER_PAGE;

            ResponseEntity<Map> response =
                    restTemplate.getForEntity(String.format(uri, PER_PAGE, offset), Map.class);

            logger.info("Got {} page. Code: {}", currentPage + 1, response.getStatusCode());
            Map body = response.getBody();
            Map message = (Map) body.get("message");

            Integer total = ((Number) message.get("total-results")).intValue();
            if(total > all) {
                all = total;
            }

            List items = (List)message.get("items");
            logger.info("Inside {} items", items.size());
            for (Object item : items) {
                String primaryName = (String) ((Map) item).get("primary-name");
                List<String> names = (List) ((Map) item).get("names");
                List<String> prefixes = (List) ((Map) item).get("prefixes");
                if (prefixes.size() != 1) {
                    for (String prefix : prefixes) {
                        receivedNames.add(new PublisherName(prefix, primaryName, names));
                    }
                } else {
                    receivedNames.add(new PublisherName(prefixes.get(0), primaryName, names));
                }
            }
            currentPage++;
            totalProcessed += PER_PAGE;
        }

        logger.info("Loaded all names.");
        logger.info("Result size: {}", receivedNames.size());

        List<String> localNames = publisherNameDAO.findAll()
                .stream()
                .map(PublisherName::getDoiPrefix)
                .collect(Collectors.toList());

        List<PublisherName> toSave = receivedNames.stream()
                .filter(n -> !localNames.contains(n.getDoiPrefix()))
                .collect(Collectors.toList());

        if(!toSave.isEmpty()) {
            publisherNameDAO.saveAll(toSave);
            logger.info("Publisher names were saved");
        } else {
            logger.info("There are nothing to save");
        }

        logger.info("Publisher names were updated");

        return Collections.emptyList();
    }


    @Override
    public List<TaskItem> collectData() {
        return Collections.emptyList();
    }

    @Override
    public TaskDescription generateTaskDescription() {
        TaskDescription task = new TaskDescription();
        task.setCreationTime(System.currentTimeMillis());
        task.setStartTime(System.currentTimeMillis());
        task.setType(getTaskType());
        return task;
    }

    @Override
    public String generateReport(List<TaskItemStatus> results, boolean taskOverallSuccess) {
        return "All names updated";
    }

    @Override
    public void collectStatistics(List<TaskItemStatus> results) {

    }

    @Override
    public boolean evaluate(List<TaskItemStatus> results, List<TaskItem> taskItems) {
        return true;
    }

}
