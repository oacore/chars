package uk.ac.core.extractmetadata.periodic.crossref.runnables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.core.extractmetadata.periodic.crossref.model.CrossrefMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ReportMalformedRecordsTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ReportMalformedRecordsTask.class);

    private static final String LINE_HEADER = "drm_id,doc_id,oai\n";
    private static final String LINE_TEMPLATE = "%d,%d,%s\n";
    private final File malformedReport;
    private final List<CrossrefMetadata> toBeReported;

    public ReportMalformedRecordsTask(File malformedReport, List<CrossrefMetadata> toBeReported) {
        this.malformedReport = malformedReport;
        this.toBeReported = toBeReported;
    }

    @Override
    public void run() {
        try {
            log.info("Writing malformed report to the file ...");

            Files.write(
                    malformedReport.toPath(),
                    LINE_HEADER.getBytes(),
                    StandardOpenOption.WRITE
            );

            log.info("There are {} records to report", toBeReported.size());
            for (CrossrefMetadata cm: toBeReported) {
                Files.write(
                        malformedReport.toPath(),
                        String.format(LINE_TEMPLATE, cm.getId(), cm.getDocId(), cm.getOai()).getBytes(),
                        StandardOpenOption.APPEND
                );
            }

            log.info("Done");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
