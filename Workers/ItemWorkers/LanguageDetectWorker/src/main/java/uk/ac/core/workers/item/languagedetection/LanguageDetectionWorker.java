package uk.ac.core.workers.item.languagedetection;

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
import uk.ac.core.database.entity.Language;
import uk.ac.core.database.languages.LanguageDAO;
import uk.ac.core.database.repository.LanguageRepository;
import uk.ac.core.database.service.document.ArticleMetadataDAO;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.issueDetection.model.IssueBO;
import uk.ac.core.issueDetection.service.IssueService;
import uk.ac.core.issueDetection.util.IssueType;
import uk.ac.core.languagedetection.LanguageDetectionService;
import uk.ac.core.languagenormalise.Languages;
import uk.ac.core.languagenormalise.NormaliseLanguage;
import uk.ac.core.singleitemworker.SingleItemWorker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static uk.ac.core.common.util.datastructure.FluentHashMap.map;

/**
 * @author lucasanastasiou
 */
public class LanguageDetectionWorker extends SingleItemWorker {

    final int MAX_SIZE = 1048576;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LanguageDetectionWorker.class);

    @Autowired
    private FilesystemDAO filesystemDAO;

    @Autowired
    private RepositoryDocumentDAO repositoryDocumentDAO;

    @Autowired
    private ArticleMetadataDAO articleMetadataDAO;

    @Autowired
    private LanguageDAO languageDAO;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private LanguageDetectionService languageDetectionService;

    @Autowired
    private IssueService issueService;

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

        String threeLetterCode = null;

        if (repositoryDocument.getTextStatus() == 1) {

            final String textLocation = filesystemDAO.getTextPath(articleId, repositoryId);

            try {
                File targetFile = new File(textLocation);

                String textExtractLanguageInEnglish = detectLanguage(articleId, targetFile);
                // using optional to preserve existing code (no need to refactor the rest of the code as it already works!)
                Optional<String> OptTextExtractThreeLetterCode = Optional.of(new NormaliseLanguage(textExtractLanguageInEnglish).asIso639_3());

                String metadataLanguageAsIso639_3 = getMetadataLanguage(articleId, repositoryId);

                if (OptTextExtractThreeLetterCode.isPresent()) {
                    String textExtractedLanguage = OptTextExtractThreeLetterCode.get();
                    logger.info("Fulltext detected: {}", textExtractedLanguage);
                    if (textExtractedLanguage == metadataLanguageAsIso639_3 ||
                            metadataLanguageAsIso639_3 == null ||
                            metadataLanguageAsIso639_3 == "eng") {
                        threeLetterCode = textExtractedLanguage;
                    } else {
                        threeLetterCode = metadataLanguageAsIso639_3;
                    }
                } else {
                    threeLetterCode = metadataLanguageAsIso639_3;
                }

                if (null == textExtractLanguageInEnglish) {
                    logger.info("language is not set in metadata");
                }

                if (metadataLanguageAsIso639_3 != null
                        && !OptTextExtractThreeLetterCode.orElse("").equals(metadataLanguageAsIso639_3)) {
                    issueService.saveIssue(
                            new IssueBO(
                                    repositoryId,
                                    articleId,
                                    IssueType.POTENTIAL_LANGUAGE_MISMATCH,
                                    String.format("metadata language (%s) does not match detected language in fulltext %s",
                                            metadataLanguageAsIso639_3,
                                            OptTextExtractThreeLetterCode.orElse("")),
                                    map("metadataLanguageAsIso639_3", metadataLanguageAsIso639_3)
                                            .with("fulltextDetectedLanguage", OptTextExtractThreeLetterCode.get()),
                                    null
                            )
                    );
                }
            } catch (IOException ex) {
                Logger.getLogger(LanguageDetectionWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            logger.info("No fulltext found, storing language based on metadata only for {} ", articleId);
            threeLetterCode = getMetadataLanguage(articleId, repositoryId);
        }

        languageDAO.insertLanguageForDocumentByIso639_2Code(articleId, threeLetterCode);
        logger.debug("Document #{} language set to {}", articleId, threeLetterCode);

        TaskItemStatus taskItemStatus = new TaskItemStatus();
        taskItemStatus.setSuccess(success);
        taskItemStatus.setTaskId(taskDescription.getUniqueId());
        return taskItemStatus;
    }

    /**
     * @param articleId
     * @return 3 letter ISO 639-3
     */
    private String getMetadataLanguage(Integer articleId, int repositoryId) {
        String outputLanguage = null;
        Optional<String> optMetadataLanguage = articleMetadataDAO.getArticleLanguage(articleId);
        if (optMetadataLanguage.isPresent()) {
            logger.info("Metadata Language original: {}", optMetadataLanguage.get());
            outputLanguage = new NormaliseLanguage(optMetadataLanguage.get()).asIso639_3();
            logger.info("Metadata Language normalised: {}", outputLanguage);

            // Report issues with Language
            String originalLanguage = optMetadataLanguage.get();
            String normalisedLanguageAsString = new NormaliseLanguage(originalLanguage).asIso639_3();
            if (normalisedLanguageAsString != originalLanguage) {
                if (normalisedLanguageAsString.equals(Languages.UNDEFINED_LANGUAGE.toString())) {
                    issueService.saveIssue(
                            new IssueBO(
                                    Long.valueOf(repositoryId),
                                    articleId,
                                    IssueType.UNPARSABLE_LANGUAGE,
                                    String.format("We did not recognise the language '%s' (possible spelling error in metadata language field)",
                                            originalLanguage),
                                    map("originalLanguage", originalLanguage), null
                            )
                    );
                } else {
                    Language normalisedLanguage = languageRepository.findOneByIso639part3(normalisedLanguageAsString);
                    issueService.saveIssue(
                            new IssueBO(
                                    Long.valueOf(repositoryId),
                                    articleId,
                                    IssueType.NORMALISED_LANGUAGE,
                                    String.format("The provided language '%s' was converted to %s (%s)",
                                            originalLanguage,
                                            normalisedLanguage.getName(),
                                            normalisedLanguage.getIso639part3()),
                                    map("originalLanguage", originalLanguage)
                                            .with("normalisedLanguageAsString", normalisedLanguageAsString),null
                            )
                    );
                }

            }

        }
        return outputLanguage;
    }

    private String detectLanguage(int articleId, File targetFile) throws IOException {
        String text = this.readFirstBytes(targetFile, MAX_SIZE);

        logger.debug("Detecting language for document " + targetFile.getAbsolutePath() + " Size (" + targetFile.length() + " bytes)");

        String fullLanguageNameInEnglish = languageDetectionService.detectLanguage(text);
        logger.debug("Language detection finished for #{} detected as {}", articleId, fullLanguageNameInEnglish);
        return fullLanguageNameInEnglish;
    }

    /**
     * Reads approximately the first X bytes of a file.
     * <p>
     * This value may be less when using multibyte UTF-8 characters.
     * <p>
     * This method key feature to to limit the input to prevent Apache Tika from
     * hanging. We don't need a 35MB file loaded into memory to run language
     * detection but only enough. 10Mb seems to be lots of data but without
     * crashing the worker
     *
     * @param sourceFile        the File of the source file
     * @param sizeInBytesToRead the size of bytes to read from the file
     * @return
     * @throws IOException
     */
    private String readFirstBytes(final File sourceFile, final int sizeInBytesToRead) throws IOException {
        StringBuilder sb = new StringBuilder();
        AtomicInteger countCharacters = new AtomicInteger();
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {
            String line = reader.readLine();
            boolean sizeAllowsContinue = countCharacters.get() < sizeInBytesToRead;
            while (line != null && sizeAllowsContinue) {
                sb.append(line);
                countCharacters.addAndGet(line.length());
                line = reader.readLine();
                sizeAllowsContinue = countCharacters.get() <= sizeInBytesToRead;
            }
            if (!sizeAllowsContinue) {
                logger.warn("File size exceeds sizeInBytesToRead, truncating input file size ({})", sourceFile.getAbsolutePath());
            }
        }
        return sb.toString();
    }
}
