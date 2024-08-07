package uk.ac.core.documentdownload.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.ac.core.common.model.article.PDFUrlSource;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.common.model.legacy.RepositoryHarvestProperties;
import uk.ac.core.common.model.task.*;
import uk.ac.core.common.model.task.parameters.DocumentDownloadParameters;
import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
import uk.ac.core.database.service.document.DocumentUrlDAO;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.documentdownload.taskitem.DocumentDownloadTaskItem;
import uk.ac.core.documentdownload.worker.DefaultDocumentDownloadWorker;
import uk.ac.core.documentdownload.worker.DocumentDownloadQueueWorkerWrapper;
import uk.ac.core.worker.QueueWorker;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author mc26486
 */
@RestController
public class StarterController {

    @Autowired
    QueueWorker queueWorker;

    @Autowired
    DocumentUrlDAO documentUrlDAO;

    @Autowired
    private RepositoryDocumentDAO repositoryDocumentDAO;

    @RequestMapping("/document_download/{repositoryId}")
    public String document_download_starter(
            @PathVariable(value = "repositoryId") final Integer repositoryId,
            @RequestParam(name = "slownessCheck", required = false, defaultValue = "true") boolean slownessCheck)
            throws UnsupportedEncodingException {
        TaskDescription taskDescription = new TaskDescription();
        DocumentDownloadParameters repositoryTaskParameters = new DocumentDownloadParameters(repositoryId);
        repositoryTaskParameters.setSlownessCheck(slownessCheck);
        taskDescription.setTaskParameters(new Gson().toJson(repositoryTaskParameters));
        taskDescription.setType(TaskType.DOCUMENT_DOWNLOAD);
        queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        return new Gson().toJson(taskDescription);
    }

    @RequestMapping("/document_download/{repositoryId}/{fromDate}")
    public String document_download_starter_from_limit(
            @PathVariable(value = "repositoryId") final Integer repositoryId,
            @PathVariable(value = "fromDate") final String fromDate,
            @RequestParam(name = "slownessCheck", required = false, defaultValue = "true") boolean slownessCheck)
            throws UnsupportedEncodingException, ParseException {
        TaskDescription taskDescription = new TaskDescription();
        DocumentDownloadParameters pdfDownloadParameters = new DocumentDownloadParameters(repositoryId);
        pdfDownloadParameters.setSlownessCheck(slownessCheck);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        pdfDownloadParameters.setFromDate(format.parse(fromDate));

        taskDescription.setTaskParameters(new Gson().toJson(pdfDownloadParameters));
        taskDescription.setType(TaskType.DOCUMENT_DOWNLOAD);
        queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        return new Gson().toJson(taskDescription);
    }

    @RequestMapping("/document_download/{repositoryId}/{fromDate}/{toDate}")
    public String metadata_extract_starter_with_date(
            @PathVariable(value = "repositoryId") final Integer repositoryId,
            @PathVariable(value = "fromDate") final String fromDate,
            @PathVariable(value = "toDate") final String toDate,
            @RequestParam(name = "slownessCheck", required = false, defaultValue = "true") boolean slownessCheck)
            throws UnsupportedEncodingException, ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDateDate = format.parse(fromDate);
        Date toDateDate = format.parse(toDate);

        TaskDescription taskDescription = new TaskDescription();
        DocumentDownloadParameters repositoryTaskParameters = new DocumentDownloadParameters(repositoryId);
        repositoryTaskParameters.setFromDate(fromDateDate);
        repositoryTaskParameters.setToDate(toDateDate);
        repositoryTaskParameters.setSlownessCheck(slownessCheck);

        taskDescription.setTaskParameters(new Gson().toJson(repositoryTaskParameters));
        taskDescription.setType(TaskType.DOCUMENT_DOWNLOAD);
        taskDescription.setStartTime(fromDateDate.getTime());
        taskDescription.setEndTime(toDateDate.getTime());
        queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        return new Gson().toJson(taskDescription);
    }

    @GetMapping("/document_download/force_redownload/{documentId}")
    public String forceSingleDocumentReDownload(
            @PathVariable("documentId") final Integer documentId
    ) {
        RepositoryDocument document = this.repositoryDocumentDAO.getRepositoryDocumentById(documentId);
        document.setPdfLastAttempt("01/01/1970");
        List<String> urls = this.documentUrlDAO.getUrlByDocId(documentId);
        for (String url: urls){
            document.getUrls().put(url, PDFUrlSource.OAIPMH);
        }

        DocumentDownloadTaskItem ddTaskItem = new DocumentDownloadTaskItem();
        ddTaskItem.setRepositoryDocumentBase(document);

        List<TaskItem> singletonList = Collections.singletonList(ddTaskItem);

        DocumentDownloadParameters documentDownloadParameters =
                new DocumentDownloadParameters(document.getIdRepository());
        documentDownloadParameters.setSingleItem(true);
        TaskDescription taskDescription = new TaskDescription();
        taskDescription.setTaskParameters(new Gson().toJson(documentDownloadParameters));

        this.queueWorker.getWorkerStatus().setTaskStatus(new TaskStatus());
        this.queueWorker.setCurrentWorkingTask(taskDescription);
        this.queueWorker.prepare();
        this.queueWorker.collectData();
        DocumentDownloadQueueWorkerWrapper documentDownloadQueueWorkerWrapper = (DocumentDownloadQueueWorkerWrapper) this.queueWorker;
        DefaultDocumentDownloadWorker defaultDocumentDownloadWorker = (DefaultDocumentDownloadWorker) documentDownloadQueueWorkerWrapper.getInstance();
        RepositoryHarvestProperties repositoryHarvestProperties = defaultDocumentDownloadWorker.getRepositoryHarvestProperties();
        repositoryHarvestProperties.setSkipAlreadyDownloaded(false);
        defaultDocumentDownloadWorker.setRepositoryHarvestProperties(repositoryHarvestProperties);
        List<TaskItemStatus> results = defaultDocumentDownloadWorker.process(singletonList);

        return new Gson().toJson(results);
    }
}
