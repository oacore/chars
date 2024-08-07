package uk.ac.core.metadatadownloadworker.worker;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskStatus;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.common.util.datastructure.Tuple;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.issueDetection.model.IssueBO;
import uk.ac.core.issueDetection.service.IssueService;
import uk.ac.core.issueDetection.util.IssueType;
import uk.ac.core.metadatadownloadworker.exception.OAIPMHEndpointException;
import uk.ac.core.metadatadownloadworker.io.CrossrefFileOutputStream;
import uk.ac.core.metadatadownloadworker.worker.metadata.DownloadMetadata;
import uk.ac.core.metadatadownloadworker.worker.metadata.DownloadMetadataFactoryService;
import uk.ac.core.worker.QueueWorker;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.net.ssl.SSLHandshakeException;

/**
 *
 * @author mc26486
 */
public class MetadataDownloadWorker extends QueueWorker {

    private static final String METADATA_DOWNLOAD_ERROR_MSG = "The error during metadata download has occurred. \n";
    private final Logger logger = LoggerFactory.getLogger(MetadataDownloadWorker.class);

    private OutputStream outputStream;

    private Integer repositoryId;
    private RepositoryTaskParameters repositoryTaskParameters;
    private Date fromDate;
    private Date toDate;

    @Autowired
    private DownloadMetadataFactoryService downloadMetadataFactoryService;

    @Autowired
    private FilesystemDAO filesystemDAO;

    @Autowired
    private IssueService issueService;

    private static final String METADATA_DOWNLOAD_SUCCEEDED = "Metadata download has finished successfully";
    private static final String METADATA_DOWNLOAD_FAILED_MSG = "Metadata download has failed.";

    private static final int CROSSREF_REPOSITORY_ID = 4786;

    @Override
    public List<TaskItem> collectData() {
        this.repositoryTaskParameters = new Gson().fromJson(this.currentWorkingTask.getTaskParameters(), RepositoryTaskParameters.class);
        this.repositoryId = this.repositoryTaskParameters.getRepositoryId();
        this.fromDate = this.repositoryTaskParameters.getFromDate();
        this.toDate = this.repositoryTaskParameters.getToDate();
        if(this.toDate == null) {
            this.toDate = this.fromDate;
        }
        logger.info("fromDate set to : " + this.fromDate);

        return Collections.emptyList();
    }

    @Override
    public List<TaskItemStatus> process(List<TaskItem> taskItems) {
        TaskItemStatus taskStatus = new TaskItemStatus();
        try {
            if (fromDate != null) {
                filesystemDAO.makeDirectory(filesystemDAO.getIncrementalFolder(repositoryId));
            }
            File metadataFinalLocation = filesystemDAO.createPathToNewMetadataXmlFile(repositoryId);

            String metadataPath = metadataFinalLocation.getAbsolutePath();
            if (fromDate != null) {
                metadataPath = filesystemDAO.getMetadataPath(repositoryId, fromDate, toDate);
            }
            String metadataPathPart = filesystemDAO.getMetadataPathPart(repositoryId, fromDate, toDate);

            DownloadMetadata downloader = downloadMetadataFactoryService.createDownloader(repositoryId, fromDate, toDate);

            filesystemDAO.deleteFile(metadataPathPart);
            logger.info("Storing metadata file to: " + metadataPathPart);

            if(repositoryId == CROSSREF_REPOSITORY_ID) {
                outputStream = new CrossrefFileOutputStream(metadataPathPart);
            } else {
                outputStream = new FileOutputStream(metadataPathPart);
            }

            outputStream.write("<?xml version=\"1.1\" encoding=\"UTF-8\"?>\n".getBytes(StandardCharsets.UTF_8));
            outputStream.write("<harvest>\n".getBytes(StandardCharsets.UTF_8));

            if (fromDate == null) {
                issueService.deleteIssues(repositoryId, TaskType.METADATA_DOWNLOAD);
            }

            downloader.downloadMetadata(
                    (message, details) -> issueService.saveIssue(createIssue(IssueType.OAI_ENDPOINT, message, details)),
                    outputStream);

            try {
                outputStream.write("</harvest>\n".getBytes(StandardCharsets.UTF_8));
                outputStream.close();
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }


            boolean moveResult = filesystemDAO.moveFile(metadataPathPart, metadataPath);

            if (!moveResult) {
                taskStatus.setSuccess(false);
                return Collections.singletonList(taskStatus);
            }

            // If the current harvest is not incremental
            if (fromDate == null) {
                // Compress location of old metadata file
                File path = filesystemDAO.getLatestMetadataPath(repositoryId).getAbsoluteFile();
                if(path.exists()){
                    String readSymLink = Files.readSymbolicLink(path.toPath()).toString();
                    try {
                        filesystemDAO.compress(new File(readSymLink));
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
                filesystemDAO.createSymbolicLink(repositoryId, metadataPath);
            }

            taskStatus.setSuccess(true);
        } catch (OAIPMHEndpointException ex) {
            reportErrors(IssueType.INVALID_OAIPMH_ENDPOINT, ex);
        } catch (SSLHandshakeException ex) {
            reportErrors(IssueType.SSL_CERTIFICATE_ERROR, ex);
        } catch (Exception ex) {
            reportErrors(IssueType.OAI_ENDPOINT, ex);
        }
        return Collections.singletonList(taskStatus);
    }

    @Override
    public boolean evaluate(List<TaskItemStatus> results, List<TaskItem> taskItems) {
        return results.stream().allMatch(TaskStatus::isSuccess);
    }

    private void reportErrors(IssueType issueType, Exception ex) {
        logger.error(METADATA_DOWNLOAD_ERROR_MSG, ex);
        issueService.saveIssue(createIssue(issueType, METADATA_DOWNLOAD_FAILED_MSG, new Tuple<>("message", issueType.getDescription())));
    }

    private IssueBO createIssue(IssueType issueType, String message, Tuple<String, String> details) {
        return new IssueBO.Builder(repositoryId)
                .issueType(issueType)
                .message(message)
                .details(Collections.singletonMap(details.getX(), details.getY()))
                .build();
    }

    @Override
    public void collectStatistics(List<TaskItemStatus> results) {
        TaskStatus downloadMetadataTaskItemStatus = results.get(0);
        if (downloadMetadataTaskItemStatus.isSuccess()) {
            logger.info(METADATA_DOWNLOAD_SUCCEEDED);
        } else {
            logger.info(METADATA_DOWNLOAD_FAILED_MSG);
        }
    }
}
