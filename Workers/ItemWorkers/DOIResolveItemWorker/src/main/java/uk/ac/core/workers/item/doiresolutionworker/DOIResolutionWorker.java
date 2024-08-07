package uk.ac.core.workers.item.doiresolutionworker;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import uk.ac.core.common.model.article.CleanDOI;
import uk.ac.core.common.model.legacy.ArticleMetadataBase;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;
import uk.ac.core.database.crossref.CrossrefDoisForDocumentId;
import uk.ac.core.database.crossref.impl.CrossrefCitationForDocumentId;
import uk.ac.core.database.service.document.ArticleMetadataDAO;
import uk.ac.core.database.service.document.ArticleMetadataDoiDAO;
import uk.ac.core.singleitemworker.SingleItemWorker;
import uk.ac.core.workers.item.doiresolutionworker.crossref.CrossrefCitation;
import uk.ac.core.workers.item.doiresolutionworker.crossref.Resolver;

/**
 *
 * @author Samuel Pearce <samuel.pearce@open.ac.uk>
 */
public class DOIResolutionWorker extends SingleItemWorker {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DOIResolutionWorker.class);

    @Autowired
    private Resolver resolver;

    @Autowired
    private ArticleMetadataDAO articleMetadataDAO;

    @Autowired
    private SaveDOI saveDOI;

    private final List<CrossrefCitation> list = Collections.synchronizedList(new ArrayList<CrossrefCitation>());

    @Autowired
    CrossrefDoisForDocumentId crossrefDoisForDocumentId;

    @Autowired
    ArticleMetadataDoiDAO articleMetadataDoiDAO;

    @Override
    public void taskReceived(Object task, @Header(AmqpHeaders.CHANNEL) Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) {
        workerStatus.setChannel(channel);
        workerStatus.setDeliveryTag(deliveryTag);
        super.taskReceived(task, channel, deliveryTag);
    }

    @Override
    public TaskItemStatus process(TaskDescription taskDescription) {

         TaskItemStatus taskItemStatus = new TaskItemStatus();

        taskItemStatus.setSuccess(false);

        taskItemStatus.setTaskId(taskDescription.getUniqueId());
        return taskItemStatus;
        
//        String params = taskDescription.getTaskParameters();
//        SingleItemTaskParameters singleItemTaskParameters = new Gson().fromJson(params, SingleItemTaskParameters.class);
//        final Integer articleId = singleItemTaskParameters.getArticle_id();
//
//        Boolean success = true;
//        ArticleMetadataBase am = this.articleMetadataDAO.getArticleMetadata(articleId);
//
//        if (am != null) {
//            String[] wordsinTitle = am.getTitle().split("\\s+");
//
//            // TEMP, if already queried, use the existing query stored in the DB
//            if (am.getId() <= 8966 && this.crossrefDoisForDocumentId.isDocumentIdResolved(articleId)) {
//                if ((am.getDoi() == null || am.getDoi().isEmpty()) || am.getDoi().startsWith("http://dx.doi.org/")) {
//                    CrossrefCitationForDocumentId doi = this.crossrefDoisForDocumentId.getCitationResolution(articleId);
//                    if (doi.getScore() > 80) {
//                        logger.debug("Saving DOI. Score above 80, save document ID: " + doi.getId() + "    DOI: " + doi.getDoi() + "    Score: " + doi.getScore());
//                        this.articleMetadataDoiDAO.updateDOI(doi.getId(), new CleanDOI(doi.getDoi()).toString(), ArticleMetadataDoiDAO.Source.CROSSREF);
//                    }
//                }
//            } else { // END TEMP
//                if ((am.getDoi() == null || am.getDoi().isEmpty())
//                        && wordsinTitle.length > 2) {
//                    this.list.add(new CrossrefCitation(am));
//                }
//            }
//
//        }
//
//        if (list.size()
//                >= Resolver.BATCH_SIZE) {
//            final long startTime = System.currentTimeMillis();
//            success = this.processList();
//            final long endTime = System.currentTimeMillis();
//            logger.debug("Time to run resolution for " + Resolver.BATCH_SIZE + " items: " + (endTime - startTime));
//        }
//
//        TaskItemStatus taskItemStatus = new TaskItemStatus();
//
//        taskItemStatus.setSuccess(success);
//
//        taskItemStatus.setTaskId(taskDescription.getUniqueId());
//        return taskItemStatus;

    }

    private synchronized Boolean processList() {
        Boolean success = false;
        synchronized (this.list) {
            resolver.resolveArticleDoisTo(list, saveDOI);
            this.list.clear();
            success = true;
        }
        return success;
    }

    public void scheduledProcessing() {
        if (this.list.size() > 0) {
            this.processList();
        }
    }

}
