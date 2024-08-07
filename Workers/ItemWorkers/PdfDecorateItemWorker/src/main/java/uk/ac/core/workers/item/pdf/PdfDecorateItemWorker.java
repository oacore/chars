package uk.ac.core.workers.item.pdf;

import com.google.gson.Gson;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;
import uk.ac.core.singleitemworker.SingleItemWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class PdfDecorateItemWorker extends SingleItemWorker {
    private final List<Integer> lastItems = new ArrayList<>(10);
    private final ExecutorService executor;

    private static final int MAX_CONN_DROP_COUNT = 5;
    private static final String ERR_MESSAGE = "Too many open files";

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(PdfDecorateItemWorker.class);

    @Autowired
    private PdfDecoratingService pdfDecoratingService;

    public PdfDecorateItemWorker() {
        this.executor = new ThreadPoolExecutor(
                3,
                5,
                2, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Override
    public TaskItemStatus process(TaskDescription taskDescription) {
        String params = taskDescription.getTaskParameters();
        SingleItemTaskParameters singleItemTaskParameters = new Gson().fromJson(params, SingleItemTaskParameters.class);
        final Integer articleId = singleItemTaskParameters.getArticle_id();
        AtomicBoolean success = new AtomicBoolean(true);
        if (!this.lastItems.contains(articleId)) {
            this.executor.execute(() -> {
                logger.info("Processing " + articleId);
                long start = System.currentTimeMillis();
                try {
                    success.set(this.pdfDecoratingService.decorateOneDocumentWithPreconditions(articleId));
                } catch (Exception e) {
                    if (e instanceof CannotGetJdbcConnectionException) {
                        logger.error("Database connection keeps dropping - initiating application halt ...");
                        this.initiateHalt(1);
                    } else {
                        if (e.getMessage().contains(ERR_MESSAGE)) {
                            logger.error("Too many open files - exiting application ...");
                            this.initiateHalt(1);
                        } else {
                            logger.error("Exception while decorating document " + articleId, e);
                        }
                    }
                }
                long end = System.currentTimeMillis();
                logger.info("Processing of article {} finished in {} ms", articleId, end - start);
            });
            if (this.lastItems.size() > 9) {
                this.lastItems.remove(9);
            }
            this.lastItems.add(articleId);
        } else {
            logger.warn("Document ID " + articleId + " was decorated within the last 10 items, Skipping");
        }
        TaskItemStatus taskItemStatus = new TaskItemStatus();
        taskItemStatus.setSuccess(success.get());
        taskItemStatus.setTaskId(taskDescription.getUniqueId());
        return taskItemStatus;
    }

    private void initiateHalt(int exitCode) {
        this.executor.shutdownNow();
        Runtime.getRuntime().halt(exitCode);
    }
}
