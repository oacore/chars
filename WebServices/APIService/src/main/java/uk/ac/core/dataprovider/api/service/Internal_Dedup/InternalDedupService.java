package uk.ac.core.dataprovider.api.service.Internal_Dedup;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.util.InputOutput;
import uk.ac.core.dataprovider.api.model.internal_dedup.DeduplicationReport;
import uk.ac.core.dataprovider.api.model.internal_dedup.DuplicateItem;
import uk.ac.core.dataprovider.api.model.internal_dedup.DuplicateList;
import uk.ac.core.filesystem.services.FilesystemDAO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class InternalDedupService {
    private static final Logger log = LoggerFactory.getLogger(InternalDedupService.class);
    private static final long CACHE_THRESHOLD = 10;

    @Autowired
    FilesystemDAO filesystemDAO;

    @Autowired
    InternalDedupDAO internalDedupDAO;

    InputOutput inputOutput = new InputOutput();

    public DeduplicationReport generateReport(
            int idRepository, double confidence, boolean internalDuplicates, boolean forceCacheRefresh) {
        log.info("Generating deduplication report ...");
        log.info("Parameters: repo id = {}, confidence = {}, internal duplicates = {}, forceCacheRefresh = {}",
                idRepository, confidence, internalDuplicates, forceCacheRefresh);

        // try getting the report from the filesystem unless it's outdated
        try {
            DeduplicationReport previousReport = (DeduplicationReport) this.inputOutput.readFileToJsonObject(
                    filesystemDAO.getDeduplicationReportCachePath(idRepository), DeduplicationReport.class);
            if (previousReport != null && !forceCacheRefresh) {
                LocalDate lastGeneration = previousReport.getGenerationTime().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                long between = ChronoUnit.DAYS.between(lastGeneration, LocalDate.now());
                if (between < CACHE_THRESHOLD) {
                    log.info("Last report generated " + between + " days ago. Sending it through.");
                    return previousReport;
                }
            }
        } catch (JsonSyntaxException e) {
            log.warn("Found JSON syntax errors in the saved file of the report");
            log.warn("Regenerating the report ...");
        }

        DeduplicationReport report = this.generateNewReport(
                idRepository, confidence, internalDuplicates, forceCacheRefresh);

        // replace usage of InputOutput due to possible JSON corruption while saving the report
        this.saveReport(idRepository, new Gson().toJson(report));
//        inputOutput.writeObjectToJsonFile(report, filesystemDAO.getDeduplicationReportCachePath(idRepository));


        log.info("Results written into" + filesystemDAO.getDeduplicationReportCachePath(idRepository));

        return report;
    }

    private DeduplicationReport generateNewReport(
            int idRepository, double confidence, boolean internalDuplicates, boolean forceCacheRefresh) {
        long startTime = System.currentTimeMillis();

        DeduplicationReport report = new DeduplicationReport();
        DuplicateList list = new DuplicateList();
        List<DuplicateItem> results = null;

        log.info("Request to Database...");

        try {
            results = internalDedupDAO.getInternalDuplicates(idRepository, confidence);
        } catch (Exception e) {
            log.error("Exception occurred: ", e);
            log.error("Exception type: {}", e.getClass().getName());

            report.setErrorMessage(e.getMessage());
            report.setIdRepository(idRepository);

            return report;
        }

        log.info("Parsing results");

        for (DuplicateItem row : results) {
            if (internalDuplicates) {
                if (idRepository == row.getIdRepository()) {
                    list.getItems().add(row);
                }
            }
        }

        log.info("Results parsed, retuning report. Count: {} ", list.getItems().size());

        report.setDuplicateList(list);
        report.setCount(list.getItems().size());
        report.setIdRepository(idRepository);

        long endTime = System.currentTimeMillis();
        report.setMillis(endTime - startTime);

        return report;
    }

    private void saveReport(int idRepo, String jsonReport) {
        try {
            Files.write(
                    Paths.get(filesystemDAO.getDeduplicationReportCachePath(idRepo)),
                    jsonReport.getBytes(StandardCharsets.UTF_8)
            );
        } catch (IOException e) {
            log.error("I/O exception occurred while saving the report to the filesystem cache");
            throw new RuntimeException(e);
        }
    }
}
