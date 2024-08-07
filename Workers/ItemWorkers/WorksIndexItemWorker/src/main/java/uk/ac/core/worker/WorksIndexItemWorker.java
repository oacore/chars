package uk.ac.core.worker;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.common.model.legacy.ArticleMetadata;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.parameters.ItemIndexParameters;
import uk.ac.core.database.service.document.DocumentDAO;
import uk.ac.core.database.service.document.impl.MySQLArticleMetadataDAO;
import uk.ac.core.database.model.WorksToDocumentDTO;
import uk.ac.core.database.service.documetduplicates.DocumentDuplicateDao;
import uk.ac.core.database.service.documetduplicates.impl.MySqlDocumentDuplicateDao;
import uk.ac.core.dataprovider.api.service.DuplicatesService;
import uk.ac.core.elasticsearch.entities.ElasticSearchArticleMetadata;
import uk.ac.core.elasticsearch.entities.ElasticSearchWorkMetadata;
import uk.ac.core.elasticsearch.exceptions.IndexException;
import uk.ac.core.elasticsearch.repositories.ArticleMetadataRepository;
import uk.ac.core.elasticsearch.repositories.WorksMetadataRepository;
import uk.ac.core.elasticsearch.services.IndexWorkService;
import uk.ac.core.elasticsearch.services.util.ElasticSearchWorkMetadataBuilder;
import uk.ac.core.repository.WorkToDocumentRepository;
import uk.ac.core.singleitemworker.SingleItemWorker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;


/**
 *
 * @author MTarasyuk
 */
public class WorksIndexItemWorker extends SingleItemWorker {

    @Autowired
    private String worksIndexName;

    @Autowired
    private IndexWorkService indexWorkService;

    @Autowired
    private WorkToDocumentRepository workToDocumentRepository;

    @Autowired
    private ElasticSearchWorkMetadataBuilder elasticSearchWorkMetadataBuilder;

    @Autowired
    private WorksMetadataRepository worksMetadataRepository;

    @Autowired
    private ArticleMetadataRepository articleMetadataRepository;

    @Autowired
    private DocumentDuplicateDao documentDuplicateDao;

    @Autowired
    private DuplicatesService duplicatesService;

    @Autowired
    private MySQLArticleMetadataDAO mySQLArticleMetadataDAO;

    @Autowired
    private MySqlDocumentDuplicateDao mySqlDocumentDuplicateDao;

    @Autowired
    private DocumentDAO documentDAO;

    private final List<Integer> lastItems = new ArrayList<>(10);
    private final ExecutorService executor;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(WorksIndexItemWorker.class);

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public WorksIndexItemWorker() {
        this.executor = new ThreadPoolExecutor(3,
                5, 2, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

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
        ItemIndexParameters singleItemTaskParameters = new Gson().fromJson(params, ItemIndexParameters.class);
        Integer documentId = singleItemTaskParameters.getArticle_id();
        String indexName = Optional.ofNullable(singleItemTaskParameters.getIndexName())
                .orElse(worksIndexName);
        boolean forceReindex = taskDescription.isForceWorksReindex();

        boolean hasIndexedItemRecently = this.lastItems.contains(documentId);
        if (!hasIndexedItemRecently) {

            executor.execute(() -> {
                try {
                    indexOneDocument(documentId, indexName, false, forceReindex);
                } catch (Exception ex) {
                    logger.error("Error while indexing one document: {}", documentId, ex);
                }
            });

            if (lastItems.size() > 9) {
                // Remove the 10th item (don't forget its zero indexed)
                this.lastItems.remove(9);
            }
            this.lastItems.add(documentId);
        } else {
            logger.warn("Document ID " + documentId + " was index within the last 10 items, Skipping");
        }

        TaskItemStatus taskItemStatus = new TaskItemStatus();
        taskItemStatus.setSuccess(true);
        taskItemStatus.setTaskId(taskDescription.getUniqueId());
        return taskItemStatus;

    }

    public void indexOneDocument(Integer documentId, String indexName, Boolean forceMerge, Boolean forceReindex) {
        logger.info("Starting indexing of the document :{} with index {}", documentId, indexName);
        long timeStart = System.currentTimeMillis();

        ElasticSearchWorkMetadata elasticSearchWorkMetadata;
        ElasticSearchArticleMetadata elasticSearchArticleMetadata = getValidElasticSearchArticleMetadata(documentId);

        logger.info("Try to get duplicates for document {}", documentId);
        List<WorksToDocumentDTO> worksToDocumentDTOS = duplicatesService.findAnnoyAndDoiDuplicates(documentId);
        List<WorksToDocumentDTO> filteredWorksToDocumentDTOS;

        if(worksToDocumentDTOS.size() == 1 && worksToDocumentDTOS.get(0).getDocumentId().equals(documentId)){
            filteredWorksToDocumentDTOS = worksToDocumentDTOS;
        } else {
            filteredWorksToDocumentDTOS = removeExclusionsFromDuplicates(documentId, worksToDocumentDTOS);
        }

        Set<Integer> filteredDuplicateIds = filteredWorksToDocumentDTOS.stream().map(WorksToDocumentDTO::getDocumentId).collect(Collectors.toSet());

        Integer workId = getWorkId(filteredDuplicateIds);

        elasticSearchWorkMetadata = generateElasticSearchWorkMetadata(workId, filteredDuplicateIds, elasticSearchArticleMetadata,
                filteredWorksToDocumentDTOS, forceMerge, forceReindex);
        if(elasticSearchWorkMetadata == null){
            return;
        }

        workId = saveWorkToDocumentDTO(elasticSearchWorkMetadata, filteredWorksToDocumentDTOS, workId);
        elasticSearchWorkMetadata.setId(workId);


        try {
            indexWorkService.indexWork(indexName, elasticSearchWorkMetadata);
        } catch (IndexException ex) {
            logger.error("Error while indexing works metadata", ex);
        }

        logger.info("Finish indexing document {}, it took {} milliseconds",
                documentId, System.currentTimeMillis() - timeStart);
    }

    private List<WorksToDocumentDTO> removeExclusionsFromDuplicates(Integer documentId,
                                                                    List<WorksToDocumentDTO> worksToDocumentDTOS){
        List<Integer> exclusionList = mySqlDocumentDuplicateDao.getExclusionList(documentId);
        return worksToDocumentDTOS.stream().filter(w -> !exclusionList.contains(w.getDocumentId()))
                .collect(Collectors.toList());
    }

    private ElasticSearchWorkMetadata generateElasticSearchWorkMetadata(Integer workId, Set<Integer> duplicateIds,
                                                                        ElasticSearchArticleMetadata elasticSearchArticleMetadata,
                                                                        List<WorksToDocumentDTO> worksToDocumentDTOS, boolean forceMerge, boolean forceReindex){
        logger.info("Start generating elasticsearch work metadata");
        ElasticSearchWorkMetadata elasticSearchWorkMetadata = null;
        List<Integer> savedDuplicates = new ArrayList<>();

        if (workId != null) {
            //savedDuplicates = workToDocumentRepository.getSavedDuplicatesByWorkId(workId);
            elasticSearchWorkMetadata = worksMetadataRepository.findFirstById(workId);
            if(elasticSearchWorkMetadata != null){
                savedDuplicates = elasticSearchWorkMetadata
                        .getCoreIds().stream().map(Integer::parseInt).collect(Collectors.toList());
            }
        }

        //if we didn't find any new duplicate stop processing
        if (savedDuplicates.size() == duplicateIds.size() && savedDuplicates.containsAll(duplicateIds) && !forceMerge && !forceReindex) {
            logger.info("All duplicates are already processed");
            return null;
        }

        if (elasticSearchWorkMetadata == null || forceMerge || forceReindex) {
            logger.info("Generating of new work metadata for document {}, with workId {}", elasticSearchArticleMetadata.getId(), workId);
            elasticSearchWorkMetadata = elasticSearchWorkMetadataBuilder.generateElasticSearchWorkMetadata(elasticSearchArticleMetadata, workId);
            logger.info("Work metadata for document {} generated", elasticSearchArticleMetadata.getId());
        }

        Set<Integer> newDocumentsForMergings;
        //find only new duplicates and merge them to existing work metadata
        if (forceMerge || forceReindex){
           newDocumentsForMergings = duplicateIds;
        } else {
            List<Integer> finalSavedDuplicates = savedDuplicates;
            newDocumentsForMergings = worksToDocumentDTOS.stream()
                    .filter(e -> finalSavedDuplicates.stream().noneMatch(d -> d.equals(e.getDocumentId())))
                    .map(WorksToDocumentDTO::getDocumentId)
                    .collect(Collectors.toSet());
        }

        logger.info("We need to merge {} new documents to existing work metadata", newDocumentsForMergings.size());

        elasticSearchWorkMetadata = mergeNewDocumentsToExistingWorkMetadata(elasticSearchWorkMetadata, newDocumentsForMergings, workId);

         return elasticSearchWorkMetadata;
    }

    private List<WorksToDocumentDTO> removeExcludedDocuments(List<WorksToDocumentDTO> worksToDocumentDTOS){
        return worksToDocumentDTOS.stream()
                .filter(e -> !mySqlDocumentDuplicateDao.isDocumentExcluded(e.getDocumentId()))
                .collect(Collectors.toList());
    }


    private Integer saveWorkToDocumentDTO(ElasticSearchWorkMetadata elasticSearchWorkMetadata, List<WorksToDocumentDTO> worksToDocumentDTOS, Integer workId) {
        for(WorksToDocumentDTO worksToDocumentDTO : worksToDocumentDTOS) {
            ElasticSearchArticleMetadata currentArticleMetadata = articleMetadataRepository.findOneById(worksToDocumentDTO.getDocumentId().toString());
            if(currentArticleMetadata == null){
                logger.info("Document {} doesn't have article metadata, skip it from indexing", worksToDocumentDTO.getDocumentId());
                continue;
            }

            Double confidence = duplicatesService.calculateConfidence(elasticSearchWorkMetadata.getYearPublished(),
                        currentArticleMetadata.getYear(), currentArticleMetadata.getAuthors(),
                        elasticSearchWorkMetadata.getAuthors());

            logger.info("Confidence for document {} is {} ", worksToDocumentDTO.getDocumentId(), confidence);

            if (workId == null) {
                workId = workToDocumentRepository.insertNewWorkToDocument(worksToDocumentDTO.getDocumentId(),
                        worksToDocumentDTO.getExplanation().toString(), confidence);
                elasticSearchWorkMetadata.setId(workId);
                logger.info("Was created new workId {}", workId);
            } else {
                workToDocumentRepository.insertOrUpdateWorkToDocument(worksToDocumentDTO.getDocumentId(), workId,
                        worksToDocumentDTO.getExplanation().toString(), confidence);
            }
        }
        return workId;
    }

    private Integer getWorkId(Set<Integer> duplicateIds) {
        List<Integer> worksIds = workToDocumentRepository.findWorkIdByDocuments(duplicateIds);
        Integer workId = null;
        if(!worksIds.isEmpty()){
            if(worksIds.size() == 1){
                workId = worksIds.get(0);
            } else {
                workId = worksIds.get(0);
                worksIds.remove(0);
                workToDocumentRepository.updateRootWorkId(worksIds, workId);
            }
        }
        return workId;
    }

    private ElasticSearchArticleMetadata getValidElasticSearchArticleMetadata(Integer documentId) {
        ElasticSearchArticleMetadata elasticSearchArticleMetadata =
                articleMetadataRepository.findOneById(documentId.toString());


        if(elasticSearchArticleMetadata == null){
            documentDAO.updateIndexedFieldForNotIndexedDocument(documentId);
            String message = String.format("Document  %s doesn't have elasticsearch " +
                    "article metadata and wasn't index with 'article' index", documentId);
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        ArticleMetadata articleMetadata = mySQLArticleMetadataDAO.getArticleMetadata(documentId);
        if(articleMetadata == null){
            documentDAO.updateIndexedFieldForNotIndexedDocument(documentId);
            String message = String.format("Document  %s doesn't have article metadata ", documentId);
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        if (!elasticSearchArticleMetadata.getDeleted().equals(DeletedStatus.ALLOWED.name())){
            String message = String.format("Document  %s is deleted", documentId);
            logger.error(message);
            throw new IllegalArgumentException(message);
        }
        return elasticSearchArticleMetadata;
    }


    public ElasticSearchWorkMetadata mergeOneDocument(ElasticSearchWorkMetadata elasticSearchWorkMetadata,
                                                      Integer workId, ElasticSearchArticleMetadata docArticleMetadata){
        ElasticSearchWorkMetadata newDocumentElasticSearchWorkMetadata = elasticSearchWorkMetadataBuilder
                .generateElasticSearchWorkMetadata(docArticleMetadata, workId);

        return mergeWorksMetadatas(newDocumentElasticSearchWorkMetadata, elasticSearchWorkMetadata);
    }

    public ElasticSearchWorkMetadata mergeNewDocumentsToExistingWorkMetadata(ElasticSearchWorkMetadata elasticSearchWorkMetadata,
                                                                             Set<Integer> duplicates, Integer workId) {
        return duplicates.stream()
                .sorted()
                .map(id -> articleMetadataRepository.findOneById(id.toString()))
                .filter(Objects::nonNull)
                .map(am -> mergeOneDocument(elasticSearchWorkMetadata, workId, am))
                //.map(am -> elasticSearchWorkMetadataBuilder.generateElasticSearchWorkMetadata(am, workId))
                .reduce(this::mergeWorksMetadatas)
                .orElseThrow(() -> new IllegalStateException("There are no document matching"));
    }

    private ElasticSearchWorkMetadata mergeWorksMetadatas(ElasticSearchWorkMetadata first,
                                                          ElasticSearchWorkMetadata second){
        if(first.getAuthors().size() > second.getAuthors().size()) {
            second.setAuthors(first.getAuthors());
        }

        if(second.getDescription() == null){
            second.setDescription(first.getDescription());
        }

        if(first.getContributors().size() > second.getContributors().size()){
            second.getContributors().addAll(first.getContributors());
        }

        second.getDataProviders().addAll(first.getDataProviders());
        second.getSourceFullTextUrls().addAll(first.getSourceFullTextUrls());

        if(second.getDownloadUrl() != null && !second.getDownloadUrl().contains("core.ac.uk")) {
            if (first.getDownloadUrl() != null && first.getDownloadUrl().contains("core.ac.uk")) {
                second.setDownloadUrl(first.getDownloadUrl());
            }
        }

        if(first.getReferences().size() > second.getReferences().size()){
            second.setReferences(first.getReferences());
        }

        if(second.getJournals() == null){
            if(first.getJournals() !=  null)
                second.setJournals(first.getJournals());
        }


        second.setAcceptedDate(getOldest(second.getAcceptedDate(), first.getAcceptedDate()));

        second.setDepositedDate(getOldest(second.getDepositedDate(), first.getDepositedDate()));
        second.setPublishedDate(getOldest(second.getPublishedDate(), first.getPublishedDate()));
        second.setYearPublished(getOldest(first.getYearPublished(), second.getYearPublished()));


        second.setCreatedDate(getOldest(second.getCreatedDate(), first.getCreatedDate()));
        second.setUpdatedDate(getLast(second.getUpdatedDate(), first.getUpdatedDate()));

        if(second.getArxivId() == null){
            second.setArxivId(first.getArxivId());
        }

        if(second.getPubmedId() == null){
            second.setPubmedId(first.getPubmedId());
        }

        if(second.getDoi() == null) {
            second.setDoi(first.getDoi());
        }

        second.setOaiIds(getAll(second.getOaiIds(), first.getOaiIds()));
        second.setCoreIds(getAll(second.getCoreIds(), first.getCoreIds()));
        second.setIdentifiers(getAll(second.getIdentifiers(), first.getIdentifiers()));

        return second;
    }

    private String getOldest(String firstStr, String secondStr) {
        return getStringDate(firstStr, secondStr, LocalDateTime::isBefore);
    }

    private String getLast(String firstStr, String secondStr){
        return getStringDate(firstStr, secondStr, LocalDateTime::isAfter);
    }

    private String getStringDate(String firstStr, String secondStr,
                                 BiFunction<LocalDateTime, LocalDateTime, Boolean> function) {
        if(isBlank(firstStr) && isBlank(secondStr)) {
            return null;
        } else if (isBlank(firstStr)) {
            return secondStr;
        } else if(isBlank(secondStr)){
            return firstStr;
        }

        LocalDateTime first = LocalDateTime.parse(firstStr, formatter);
        LocalDateTime second = LocalDateTime.parse(secondStr, formatter);

        return function.apply(first, second) ? first.format(formatter) : second.format(formatter);
    }

    private Integer getOldest(Integer first, Integer second) {
        if (first == null && second == null) {
            return null;
        } else if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            return first < second ? first : second;
        }
    }

    private <T> Set<T> getAll(Set<T> first, Set<T> second) {
        if (first == null && second == null) {
            return new HashSet<>();
        } else if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        } else {
            first.addAll(second);
            return first;
        }
    }

    public void reindexDocumentWithDeletingFromCurrentWorkId(Integer excludedDocumentId, List<Integer> documentIds,
                                                             String indexName){
        documentDuplicateDao.insertDocumentToExcludingTable(excludedDocumentId, documentIds);
        workToDocumentRepository.deleteDocumentFromWorkToDocument(excludedDocumentId);

        indexOneDocument(documentIds.get(0), indexName, true, false);
        indexOneDocument(excludedDocumentId, indexName, true, false);
    }
}
