package uk.ac.core.workers.item.textextraction;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;
import uk.ac.core.database.repository.FileExtensionRepository;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.singleitemworker.SingleItemWorker;
import uk.ac.core.textextraction.TextExtractorService;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author lucasanastasiou
 */
public class TextExtractionWorker extends SingleItemWorker {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TextExtractionWorker.class);

    @Autowired
    private FilesystemDAO filesystemDAO;

    @Autowired
    private RepositoryDocumentDAO repositoryDocumentDAO;

    @Autowired
    private FileExtensionRepository fileExtensionRepository;

    @Override
    public void taskReceived(Object task, @Header(AmqpHeaders.CHANNEL) Channel channel,
                             @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) {
        workerStatus.setChannel(channel);
        workerStatus.setDeliveryTag(deliveryTag);
        super.taskReceived(task, channel, deliveryTag);
    }

    @Override
    public TaskItemStatus process(TaskDescription taskDescription) {

        String params = taskDescription.getTaskParameters();
        SingleItemTaskParameters singleItemTaskParameters = new Gson().fromJson(params, SingleItemTaskParameters.class);
        final Integer articleId = singleItemTaskParameters.getArticle_id();

        final RepositoryDocument repositoryDocument = repositoryDocumentDAO.getRepositoryDocumentById(articleId);
        final Integer repositoryId = repositoryDocument.getIdRepository();

        boolean success = true;
        // If the document already has the text extracted, do nothing
        if (repositoryDocument.getTextStatus() == 0) {

            final String textLocation = filesystemDAO.getTextPath(articleId, repositoryId);

            try {
                final File pdfLocation = filesystemDAO.getDocumentPath(articleId, repositoryId, fileExtensionRepository.findById(articleId).orElseThrow(() -> new Exception("No associated file extension found.")).getName().toString());
                if (pdfLocation.length() < 100000000) {
                    // Create parent directory of textLocation
                    File text = new File(textLocation);
                    text.getParentFile().mkdirs();

                    logger.debug("Extracting text for document " + pdfLocation);
                    logger.debug("Extraction started for #" + articleId);

                    final TextExtractorService textExtractor = new TextExtractorService(pdfLocation.toPath());

                    success = extractTextForDocument(textExtractor, textLocation);

                    logger.debug("Extraction finished for #" + articleId);

                    repositoryDocumentDAO.setDocumentTextStatus(articleId, success ? 1 : 0);
                    logger.debug("set document text status for #" + articleId);

                    if (success) {
                        filesystemDAO.deleteFile(filesystemDAO.getTextPathDeleted(articleId, repositoryId));
                        logger.debug("end delete old extracted text for #" + articleId);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(TextExtractionWorker.class.getName()).log(Level.SEVERE, null, ex);
                success = false;
            }
        }

        TaskItemStatus taskItemStatus = new TaskItemStatus();
        taskItemStatus.setSuccess(success);
        taskItemStatus.setTaskId(taskDescription.getUniqueId());
        return taskItemStatus;
    }

    /**
     * Runs the extraction
     *
     * @param textExtractorService
     * @param textLocation
     *
     * @return
     */
    private boolean extractTextForDocument(TextExtractorService textExtractorService, String textLocation) {

        boolean success = true;
        long t0 = System.currentTimeMillis();

        try {
            textExtractorService.extractTextFromDocumentTo(textLocation);
        } catch (IOException ex) {
            success = false;
            logger.info(ex.getMessage());
        }

        long t1 = System.currentTimeMillis();
        logger.info("Extraction of " + textExtractorService.getPath() + " took " + (t1 - t0));

        return success;
    }


}
