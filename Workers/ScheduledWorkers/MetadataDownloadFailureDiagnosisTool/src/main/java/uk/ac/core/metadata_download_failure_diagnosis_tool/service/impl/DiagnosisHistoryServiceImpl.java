package uk.ac.core.metadata_download_failure_diagnosis_tool.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.metadata_download_failure_diagnosis_tool.model.FailureDiagnosisTaskItem;
import uk.ac.core.metadata_download_failure_diagnosis_tool.service.DiagnosisHistoryService;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

@Service
public class DiagnosisHistoryServiceImpl implements DiagnosisHistoryService {
    private static final Logger log = LoggerFactory.getLogger(DiagnosisHistoryServiceImpl.class);
    private static final String FOLDER_PATH = "/tmp/failure-diagnosis-history/";
    private static final String FILE_PATH = FOLDER_PATH + "history-log.txt";

    @PostConstruct
    private void init() {
        try {
            File rootFolder = new File(FOLDER_PATH);
            if (rootFolder.mkdirs()) {
                log.info("Folder created: {}", FOLDER_PATH);
            } else {
                log.info("Failed to create folder {}", FOLDER_PATH);
            }
            File historyLogFile = new File(FILE_PATH);
            if (historyLogFile.createNewFile()) {
                log.info("File created: {}", FILE_PATH);
            } else {
                log.info("File exists: {}", FILE_PATH);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveFirstAttemptDate(TaskItem ti) {
        FailureDiagnosisTaskItem taskItem = (FailureDiagnosisTaskItem) ti;
        if (this.recordExists(taskItem)) {
            return;
        }
        try (
                FileWriter fileWriter = new FileWriter(FILE_PATH, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)
        ) {
            String line = taskItem.getRepoId() + "," + LocalDate.now();
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            log.info("First attempt date for repository {} successfully written", taskItem.getRepoId());
        } catch (IOException e) {
            log.error("Exception while saving first attempt date to the file", e);
        }
    }

    private boolean recordExists(FailureDiagnosisTaskItem taskItem) {
        try (Scanner scanner = new Scanner(new File(FILE_PATH))) {
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(",");
                if (Integer.parseInt(line[0]) == taskItem.getRepoId()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Exception occurred", e);
            return false;
        }
    }

    /**
     *
     * @param repoId - repository ID
     * @return days past since first attempt, or -1 if there is no record about repoID
     */
    @Override
    public long daysSinceFirstAttempt(int repoId) {
        try (Scanner scanner = new Scanner(new File(FILE_PATH))) {
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(",");
                if (Integer.parseInt(line[0]) == repoId) {
                    LocalDate firstAttemptDate = LocalDate.parse(line[1]);
                    LocalDate now = LocalDate.now();
                    return ChronoUnit.DAYS.between(firstAttemptDate, now);
                }
            }
            return -1;
        } catch (Exception e) {
            log.error("Exception while calculating amount of days past since first attempt", e);
            return -1;
        }
    }
}
