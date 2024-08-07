package uk.ac.core.journalissnupdate.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.database.model.JournalISSN;
import uk.ac.core.database.service.journals.JournalsDAO;
import uk.ac.core.worker.ScheduledWorker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JournalIssnUpdate extends ScheduledWorker {

    private static final int PER_PAGE = 500;
    private static final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private JournalsDAO journalsDAO;

    protected static final Logger logger = LoggerFactory.getLogger(JournalIssnUpdate.class);


    @Override
    public List<TaskItemStatus> process(List<TaskItem> taskItems) {

        String uri = "http://api.crossref.org/journals?rows=%s&offset=%s";

        int currentPage = 0;
        int totalProcessed = 0;
        int all = 82282;

        List<JournalISSN> receivedIssns = new ArrayList<>();

        logger.info("Start updating issns");

        while (totalProcessed < all) {
            int offset = currentPage * PER_PAGE;

            ResponseEntity<Map> response =
                    restTemplate.getForEntity(String.format(uri, PER_PAGE, offset), Map.class);

            Map body = response.getBody();
            Map message = (Map) body.get("message");

            Integer total = ((Number) message.get("total-results")).intValue();
            if (total > all) {
                all = total;
            }

            List items = (List) message.get("items");

            for (Object item : items) {
                String title = (String) ((Map) item).get("title");
                List subjects = (List) ((Map) item).get("subjects");
                List<String> issns = (List) ((Map) item).get("ISSN");

                String subjectString = subjects.stream()
                        .map(subject -> ((Map) subject).get("name"))
                        .map(s -> s.toString())
                        .collect(Collectors.joining(",")).toString();

                if (issns != null && !issns.isEmpty()) {
                    receivedIssns.add(new JournalISSN(title, subjectString, issns));
                }
            }
            currentPage++;
            totalProcessed += PER_PAGE;
        }

        logger.info("Loaded all issns.");
        logger.info("Result size: {}", receivedIssns.size());

        List<String> dbIssn = journalsDAO.findAllIssns().stream()
                .flatMap(i -> i.getIssnList().stream())
                .collect(Collectors.toList());

        List<JournalISSN> toSave = receivedIssns.stream()
                .filter(n -> !dbIssn.containsAll(n.getIssnList()))
                .collect(Collectors.toList());

        if(!toSave.isEmpty()) {
            journalsDAO.saveAll(toSave);
            logger.info("Issn was saved");
        } else {
            logger.info("There are nothing to save");
        }

        logger.info("Issn was updated");

        return Collections.emptyList();
    }


    @Override
    public String generateReport(List<TaskItemStatus> results, boolean taskOverallSuccess) {
        return "Issns are updated";
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.JOURNALS_ISSN_UPDATE;
    }

    @Override
    @Scheduled(cron = "0 0 0 1 * *")
    public void scheduledStart() {
        this.start();
    }

    @Override
    public TaskDescription generateTaskDescription() {
        TaskDescription task = new TaskDescription();
        task.setCreationTime(System.currentTimeMillis());
        task.setStartTime(System.currentTimeMillis());
        task.setType(getTaskType());
        return task;    }

    @Override
    public List<TaskItem> collectData() {
        return Collections.emptyList();
    }

    @Override
    public void collectStatistics(List<TaskItemStatus> results) {

    }

}
