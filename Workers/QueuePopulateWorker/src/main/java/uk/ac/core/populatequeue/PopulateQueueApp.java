package uk.ac.core.populatequeue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.item.TaskItemBuilder;
import uk.ac.core.queue.QueueItemService;

/**
 *
 * @author lucasanastasiou
 */
@SpringBootApplication
public class PopulateQueueApp implements CommandLineRunner {

    @Value("${queue}")
    String queue;
    @Value("${from}")
    String from;
    @Value("${to}")
    String to;
    @Value("${input-file-path}")
    String inputFilePath;

    @Autowired
    QueueItemService queueItemService;
    @Autowired
    TaskItemBuilder taskItemBuilder;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PopulateQueueApp.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        if (queue == null || queue.isEmpty()) {
            printUsage();
        }
        if ((inputFilePath == null || inputFilePath.isEmpty())){
//                && from != null && !from.isEmpty() && to != null && !to.isEmpty()) {
                
            try {
                Integer fromI = Integer.parseInt(from);
                Integer toI = Integer.parseInt(to);

                for (int i = fromI; i <= toI; i++) {

                    TaskDescription taskDescription = this.taskItemBuilder.buildSingleItemTask(TaskType.fromString(queue), i);

                    this.queueItemService.publish(taskDescription);
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Sorry cannot parse as integer an argument " + nfe.getMessage());
            }
        } else if (!inputFilePath.isEmpty()) {
            File file = new File(inputFilePath);
            if (!file.exists()) {
                System.out.println("File " + file.getAbsolutePath() + " does not exists.");
            }
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    try {
                        Integer coreId = Integer.parseInt(line);
                        TaskDescription taskDescription = this.taskItemBuilder.buildSingleItemTask(TaskType.fromString(queue), coreId);
                        this.queueItemService.publish(taskDescription);
                    } catch (NumberFormatException nfe) {
                        System.out.println("nfe = " + nfe);
                    }
                }
            } catch (IOException ioe) {
                System.out.println("ioe = " + ioe);
            } finally {

            }
        } else {
            printUsage();
        }
    }

    private void printUsage() {
        System.out.println("java -jar PopulateQueue-1.0-SNAPSHOT-shaded.jar --queue=grobid-extraction-item [--from=<FROM>] [--to=<TO>] [--input-file-path=<INPUT_FILE>]");
    }
}
