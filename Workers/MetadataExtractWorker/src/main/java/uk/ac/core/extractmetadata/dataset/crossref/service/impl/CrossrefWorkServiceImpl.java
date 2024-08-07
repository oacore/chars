package uk.ac.core.extractmetadata.dataset.crossref.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.common.model.legacy.ArticleMetadata;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.crossref.json.CrossRefDocument;
import uk.ac.core.database.service.documetduplicates.DocumentDuplicateDao;
import uk.ac.core.extractmetadata.dataset.crossref.service.CrossrefWorkService;
import uk.ac.core.extractmetadata.dataset.crossref.util.CrossrefWorkMapper;
import uk.ac.core.extractmetadata.worker.oaipmh.GetRecords.ArticleMetadataPersist;
import uk.ac.core.extractmetadata.worker.oaipmh.GetRecords.ArticleMetadataPersistFactory;
import uk.ac.core.supervisor.client.SupervisorClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CrossrefWorkServiceImpl implements CrossrefWorkService {
    private static final Logger log = LoggerFactory.getLogger(CrossrefWorkServiceImpl.class);
    private static final int CROSSREF_ID = 4786;
    private static final int BATCH_SIZE = 100;

    private final DocumentDuplicateDao duplicatesDAO;
    private final CrossrefWorkMapper mapper;
    private final ArticleMetadataPersist persist;
    private final SupervisorClient supervisorClient;

    private int batchCounter;
    private final List<Integer> docsToIndex;

    @Autowired
    public CrossrefWorkServiceImpl(
            DocumentDuplicateDao duplicatesDAO,
            CrossrefWorkMapper mapper,
            ArticleMetadataPersistFactory persistFactory,
            SupervisorClient supervisorClient) {
        this.duplicatesDAO = duplicatesDAO;
        this.mapper = mapper;
        this.persist = persistFactory.create(new TaskItemStatus(), CROSSREF_ID);
        this.supervisorClient = supervisorClient;
        this.batchCounter = 0;
        this.docsToIndex = new ArrayList<>();
    }

    @Override
    public Optional<Integer> matchCrossrefWork(CrossRefDocument work) {
        String crossrefDOI = work.getDOI();
        log.info("Matching Crossref DOI {} to CORE ...", crossrefDOI);

        Set<Integer> coreIds = this.duplicatesDAO.getIdDocumentsByDOI(crossrefDOI, CROSSREF_ID);
        if (coreIds.isEmpty()) {
            log.info("Found no Crossref documents with DOI {}", crossrefDOI);
            return Optional.empty();
        } else if (coreIds.size() == 1) {
            log.info("Found 1 Crossref document with DOI {}", crossrefDOI);
            Integer id = coreIds.iterator().next();
            return Optional.of(id);
        } else {
            log.info("Found more than 1 Crossref documents with DOI {}", crossrefDOI);
            log.info("Returning the oldest document ID ...");
            return coreIds.stream()
                    .sorted()
                    .limit(1)
                    .findFirst();
        }
    }

    @Override
    public void updateExistingCoreRecord(Integer coreId, CrossRefDocument work) {
        ArticleMetadata am = this.mapper.newArticleMetadata(work);
        am.setId(coreId);
        // update DB
        this.persist.persist(am);

        this.docsToIndex.add(am.getId());
        this.batchCounter++;
        if (this.batchCounter >= BATCH_SIZE) {
            this.flushAndIndex();
        }
    }

    @Override
    public void addNewCoreRecord(CrossRefDocument work) {
        ArticleMetadata am = this.mapper.newArticleMetadata(work);
        // update DB
        this.persist.persist(am);

        this.docsToIndex.add(am.getId());
        this.batchCounter++;
        if (this.batchCounter >= BATCH_SIZE) {
            this.flushAndIndex();
        }
    }

    private void flushAndIndex() {
        // flush articles
        log.info("Flushing articles to the database ...");
        this.persist.finalise(false);
        // schedule index
        for (Integer id : this.docsToIndex) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("Sending index request for document {} ...", id);
            this.sendIndexRequest(id);
        }
        // reset counter and empty the list
        log.info("Resetting counter and clearing the batch");
        this.batchCounter = 0;
        this.docsToIndex.clear();
    }

    private void sendIndexRequest(Integer coreId) {
        try {
            this.supervisorClient.sendIndexItemRequest(coreId);
        } catch (CHARSException ignored) {
            // theoretically, it should never be thrown
            // see uk.ac.core.supervisor.client.HttpSupervisorClient
        }
    }
}
