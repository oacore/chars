package uk.ac.core.baseimport.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.core.baseimport.model.BaseRepositoryTaskStatus;
import uk.ac.core.baseimport.worker.BaseImportWorker;
import uk.ac.core.common.model.task.TaskItemStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;
import static java.util.stream.Collectors.joining;

/**
 * Class, what encapsulates report generation.
 */
public final class BaseImportReport {

    private static final String SUCCESSFUL_IMPORT_MSG = "[CHARS] %d BASE repositories were imported on %s.<br>" +
            "Key information about imported BASE repos:<br>" +
            "<ul>" +
            "<li> %d BASE repos have OAI-PMH endpoint.</li>" +
            "<li> %d duplicate repos were detected among those which have OAI-PMH endpoint and weren't saved.<br>" +
            "</li>" +
            "</ul>";
    private static final String NO_REPOSITORIES_IMPORTED_MSG = "[CHARS] No new BASE repositories were imported.";
    private static final String UNSUCCESSFUL_IMPORT_MSG = "The error has occurred during the import of BASE repositories.";

    private final Long totalTime; // in seconds
    private final List<TaskItemStatus> importResults;
    private final boolean succeeded;
    private final long importedReposCount;

    private static final Logger LOG = LoggerFactory.getLogger(BaseImportReport.class);

    public BaseImportReport(long importedReposCount, List<TaskItemStatus> importResults, boolean succeeded, Long startTime) {
        this.importResults = importResults;
        this.succeeded = succeeded;
        this.importedReposCount = importedReposCount;
        this.totalTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime);
    }

    public String getMessage() {

        String emailMessageBody;

        if (importResults == null || !succeeded) {
            emailMessageBody = createUnsuccessfulMsg();
            return emailMessageBody;
        }

        if (importResults.isEmpty()) {
            emailMessageBody = NO_REPOSITORIES_IMPORTED_MSG;
        } else {
            emailMessageBody = createSuccessfulMsg();
        }



        return emailMessageBody;
    }

    private String performanceInfo() {

        long hours = this.totalTime / 3600;
        long minutes = (this.totalTime % 3600) / 60;
        long seconds = (this.totalTime % 60);

        StringBuffer sb = new StringBuffer("<br>Total time: ");

        sb.append((hours == 0) ? "" : hours + " h ");
        sb.append((minutes == 0) ? "" : minutes + " m ");
        sb.append((seconds == 0) ? "" : seconds + " s ");

        return sb.toString();
    }

    private String createUnsuccessfulMsg() {
        StringBuffer sb = new StringBuffer(UNSUCCESSFUL_IMPORT_MSG).append("<br>");
        sb.append(formErrorDetailsList());
        if (this.importResults == null) {
            sb.append("Details:<br>").append("Import results are null");
        }
        sb.append(performanceInfo());
        return sb.toString();
    }

    private String formErrorDetailsList() {
        StringBuffer sb = new StringBuffer();
        if (!BaseImportWorker.getErrors().isEmpty()) {
            sb.append("There were errors while processing some of the repositories.<br>");
            sb.append("Details:<br>");
            sb.append("<ul>");
            for (Map.Entry<String, String> err: BaseImportWorker.getErrors().entrySet()) {
                sb.append("<li>");
                sb.append("'").append(err.getKey()).append("'").append(": ");
                sb.append(err.getValue()).append("<br>");
                sb.append("</li>");
            }
            sb.append("</ul>");
        }
        return sb.toString();
    }

    private String createSuccessfulMsg() {
        Supplier<Stream<BaseRepositoryTaskStatus>> duplicates = getUniqueTaskItems();

        return populateSuccessfulMsgTemplate(
                getTaskItems().get().count(),
                duplicates.get().count(),
                extractDuplicateEndpoints(duplicates));
    }

    private String populateSuccessfulMsgTemplate(long validBaseReposCount, long duplicateCount,  String duplicateUrls) {
        return String.format(SUCCESSFUL_IMPORT_MSG,
                importedReposCount,
                LocalDate.now().toString(),
                validBaseReposCount,
                duplicateCount
//                ,duplicateUrls
                )
                .concat(formErrorDetailsList())
                .concat(performanceInfo());
    }

    private Supplier<Stream<BaseRepositoryTaskStatus>> getTaskItems() {
        return () -> importResults.stream()
                .map(taskItem -> (BaseRepositoryTaskStatus) taskItem);
    }

    private Supplier<Stream<BaseRepositoryTaskStatus>> getUniqueTaskItems() {
        return () -> getTaskItems().get().filter(BaseRepositoryTaskStatus::isDuplicate);
    }

    private String extractDuplicateEndpoints(Supplier<Stream<BaseRepositoryTaskStatus>> duplicates) {
        return duplicates.get()
                .map(BaseRepositoryTaskStatus::getOaimPmhEndpoint)
                .map(Object::toString)
                .collect(joining(", ", "", "."));
    }
}