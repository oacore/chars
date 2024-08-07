package uk.ac.core.documentdownload.taskitem;

import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import uk.ac.core.common.model.legacy.RepositoryDocumentBase;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.parameters.DocumentDownloadParameters;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.documentdownload.exception.DataBaseConnectionException;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DocumentDownloadTaskItemList extends ArrayList<TaskItem> {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DocumentDownloadTaskItemList.class);

    private String repositoryId;
    private RepositoryDocumentDAO repositoryDocumentDAO;
    private DocumentDownloadParameters pdfDownloadParameters;
    private Boolean prioritiseOldDocumentsForDownload;

    private long limited;
    private long readLimit;
    private long currentPage = 0;
    private long currentReaded = 0;

    public DocumentDownloadTaskItemList(String repositoryId, RepositoryDocumentDAO repositoryDocumentDAO, DocumentDownloadParameters pdfDownloadParameters, Boolean prioritiseOldDocumentsForDownload) {
        this.repositoryId = repositoryId;
        this.repositoryDocumentDAO = repositoryDocumentDAO;
        this.pdfDownloadParameters = pdfDownloadParameters;
        this.prioritiseOldDocumentsForDownload = prioritiseOldDocumentsForDownload;

        this.limited = this.pdfDownloadParameters.getSize() != null && this.pdfDownloadParameters.getSize() < 1000 ? this.pdfDownloadParameters.getSize() : 1200;
        this.readLimit = this.pdfDownloadParameters.getSize() != null ? this.pdfDownloadParameters.getSize() : Long.MAX_VALUE;
    }

    private List<TaskItem> currentBuffer = Collections.emptyList();
    private int currentBufferIndex = -1;

    @Override
    public Iterator<TaskItem> iterator() {
        return new Iterator<TaskItem>() {
            @Override
            public boolean hasNext() {
                logger.info("HasNext started. Buffer size: {}, buffer index: {}", currentBuffer.size(), currentBufferIndex);
                if (safeGet(currentBuffer, currentBufferIndex + 1) != null) {
                    logger.info("There is an element");
                    return true;
                } else {
                    currentBuffer = convertToDocumentDownloadTaskItem(getNextDocuments());
                    currentBufferIndex = -1;
                    logger.info("Fetched new data. element exist: {}", safeGet(currentBuffer, currentBufferIndex + 1) != null);
                    return safeGet(currentBuffer, currentBufferIndex + 1) != null;
                }
            }

            @Override
            public TaskItem next() {
                return currentBuffer.get(++currentBufferIndex);
            }
        };
    }

    private List<RepositoryDocumentBase> getNextDocuments() throws DataBaseConnectionException {
        if (currentReaded >= readLimit) {
            return new ArrayList<>();
        }

        long offset = currentPage++ * limited;

        List<RepositoryDocumentBase> readedList = databaseReadRetry(
                () -> repositoryDocumentDAO.getDocuments(repositoryId, prioritiseOldDocumentsForDownload,
                pdfDownloadParameters.getFromDate(), pdfDownloadParameters.getToDate(), offset, limited),
                (e) -> logger.error("Error while reading documents", e));

        currentReaded += readedList.size();
        return mapDocuments(readedList);
    }

    private static <T> T databaseReadRetry(Supplier<T> supplier, Consumer<Throwable> onError) throws DataBaseConnectionException {
        final int retryCount = 5;
        int count = 0;

        while (count++ < retryCount) {
            try {
                return supplier.get();
            } catch (DataAccessResourceFailureException e) {
                onError.accept(e);
                if(count == retryCount) {
                    throw new DataBaseConnectionException("Attempted request " + retryCount + " times. Aborting", e);
                }
            }
            try {Thread.sleep(120000);} catch (InterruptedException ignored) {}
        }
        throw new IllegalArgumentException("Can't receive result");
    }

    private List<RepositoryDocumentBase> mapDocuments(List<RepositoryDocumentBase> documents) {
        Map<Integer, RepositoryDocumentBase> documentMap = new HashMap<>();

        documents.forEach((document) -> {
            if (documentMap.containsKey(document.getIdDocument())) {
                document.getUrls().forEach((url, source) -> {
                    documentMap.get(document.getIdDocument()).addUrl(url, source);
                });
            } else {
                documentMap.put(document.getIdDocument(), document);
            }
        });
        logger.info("End executing mapping for getRepositoryDocumentsForPdfDownload");
        return new LinkedList<>(documentMap.values());
    }


    /**
     * this may be an over killing
     *
     * @param documentsForDownload
     * @return
     */
    private List<TaskItem> convertToDocumentDownloadTaskItem(List<RepositoryDocumentBase> documentsForDownload) {
        List<TaskItem> result = new ArrayList<>();
        logger.info("Started converting to DocumentDownloadTaskItem");
        for (RepositoryDocumentBase document : documentsForDownload) {
            DocumentDownloadTaskItem documentDownloadTaskItem = new DocumentDownloadTaskItem();
            documentDownloadTaskItem.setRepositoryDocumentBase(document);
            result.add(documentDownloadTaskItem);
        }
        return result;
    }

    private static <T> T safeGet(List<T> list, int index) {
        try {
            return list.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
