package uk.ac.core.workers.item.pdf.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;
import uk.ac.core.dataprovider.logic.exception.DataProviderNotFoundException;
import uk.ac.core.worker.QueueWorker;
import uk.ac.core.workers.item.pdf.PdfDecoratingService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
public class StarterPdfController {
    @Autowired
    private PdfDecoratingService service;


    @Autowired
    private QueueWorker queueWorker;

    /**
     * @param documentId id of the document to decorate the file
     * @throws IOException                   if something goes wrong with reading or writing the file
     * @throws DataProviderNotFoundException if data provider could not be found
     * @return
     */
    @RequestMapping("/pdf/{documentId}")
    public String decoratePdf(@PathVariable(value = "documentId") final Integer documentId) throws UnsupportedEncodingException {
        TaskDescription taskDescription = new TaskDescription();
        SingleItemTaskParameters repositoryTaskParameters = new SingleItemTaskParameters(documentId);
        taskDescription.setTaskParameters(new Gson().toJson(repositoryTaskParameters));
        taskDescription.setType(TaskType.PDF_DECORATE_ITEM);
        queueWorker.taskReceived(new Gson().toJson(taskDescription).getBytes("utf-8"), null, null);
        return new Gson().toJson(taskDescription);
    }

    /**
     * Decorates PDFs from the repository
     *
     * @param repositoryId – identifier of the repository to decorate
     * @return JSON string with the decoration results
     * @throws IOException                   if something goes wrong with reading or writing the file
     * @throws DataProviderNotFoundException if data provider could not be found
     */
    @RequestMapping("/pdf/repository/{id}")
    public String decoratePdfByRepository(@PathVariable(value = "id") final Integer repositoryId) throws IOException, DataProviderNotFoundException {
        return service.decorateRepository(repositoryId);
    }

}
