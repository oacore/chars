package uk.ac.core.supervisor.controller.workers.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.TaskType;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class WorkerCreationService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(WorkerCreationService.class);

    public int createNew(TaskType taskType) {

        //
        // we are executing a script that executes the real worker process creation
        // 
        // java -jar bin/LegacyWorker-1.0-SNAPSHOT-shaded.jar -Djava.security.egd=file:/dev/./urandom --queueName=index > logs/legacy-ind-$ind.log 2>&1 &
        //
        int workerIndex = discoverWorkerIndex(taskType);

        String[] cmdArray = new String[]{
            "/data/chars/scripts/creator.sh",
            this.getExecutableJarFromTaskType(taskType),
            taskType.getName(),
            "" + getNodePort(taskType, workerIndex),
            getWorkerFileName(taskType, workerIndex)
        };
        logger.info(Arrays.deepToString(cmdArray));

        ProcessBuilder pb = new ProcessBuilder(cmdArray);
        try {
            Process p = pb.start();
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            System.out.println(result);
            p.waitFor();

        } catch (IOException | InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
        }

        try {
            Thread.currentThread().sleep(10000);
        } catch (InterruptedException ex) {
            Logger.getLogger(WorkerCreationService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    private String getExecutableJarFromTaskType(TaskType taskType) {
        Map<TaskType, String> conversionMap = new EnumMap<>(TaskType.class);
        conversionMap.put(TaskType.METADATA_DOWNLOAD, "LegacyWorker-1.0-SNAPSHOT-shaded.jar");
        conversionMap.put(TaskType.EXTRACT_METADATA, "LegacyWorker-1.0-SNAPSHOT-shaded.jar");
        conversionMap.put(TaskType.DOCUMENT_DOWNLOAD, "LegacyWorker-1.0-SNAPSHOT-shaded.jar");
        conversionMap.put(TaskType.EXTRACT_TEXT, "LegacyWorker-1.0-SNAPSHOT-shaded.jar");
        conversionMap.put(TaskType.INDEX, "LegacyWorker-1.0-SNAPSHOT-shaded.jar");
        conversionMap.put(TaskType.THUMBNAIL_GENERATION, "ThumbnailGenerationWorker-1.0-SNAPSHOT.jar");
        String execJar = conversionMap.get(taskType);
        return execJar;
    }

    private int discoverWorkerIndex(TaskType taskType) {
        int workerIndex = -1;
        String worker_N_LogFile;
        File logFile;
        do {
            workerIndex++;
            worker_N_LogFile = getWorkerFileName(taskType, workerIndex);
            logFile = new File(worker_N_LogFile);

        } while (logFile.exists());

        return workerIndex;
    }

    private String getWorkerFileName(TaskType taskType, int workerIndex) {
        String worker_N_LogFile;
        worker_N_LogFile = "/data/chars/logs/" + taskType.getName() + "_" + workerIndex + ".log";
        return worker_N_LogFile;
    }

    private int getNodePort(TaskType taskType, int workerIndex) {
        switch (taskType) {
            case METADATA_DOWNLOAD:
                return 1000 + workerIndex;
            case EXTRACT_METADATA:
                return 2000 + workerIndex;
            case DOCUMENT_DOWNLOAD:
                return 3000 + workerIndex;
            case EXTRACT_TEXT:
                return 4000 + workerIndex;
            case INDEX:
                return 5000 + workerIndex;
            case THUMBNAIL_GENERATION:
                return 6000 + workerIndex;
        }
        return 7777 + workerIndex;
    }
}
