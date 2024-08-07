package uk.ac.core.extractmetadata.worker.edgecases;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.common.model.legacy.ArticleMetadata;
import uk.ac.core.database.service.document.RepositoryMetadataDAO;
import uk.ac.core.database.service.documetduplicates.DocumentDuplicateDao;
import uk.ac.core.extractmetadata.worker.oaipmh.GetRecords.ArticleMetadataPersist;
import uk.ac.core.filesystem.services.FilesystemDAO;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CrossrefDuplicates {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ArticleMetadataPersist.class);

    private static final int CROSSREF_ID = 4786;

    private final DocumentDuplicateDao documentDuplicateDao;
    private final RepositoryMetadataDAO repositoryMetadataDAO;
    private final FilesystemDAO filesystemDAO;
    private final DeleteStatusDocument deleteStatusDocument;

    @Autowired
    public CrossrefDuplicates(
            DocumentDuplicateDao documentDuplicateDao,
            RepositoryMetadataDAO repositoryMetadataDAO,
            FilesystemDAO filesystemDAO,
            DeleteStatusDocument deleteStatusDocument) {
        this.documentDuplicateDao = documentDuplicateDao;
        this.repositoryMetadataDAO = repositoryMetadataDAO;
        this.filesystemDAO = filesystemDAO;
        this.deleteStatusDocument = deleteStatusDocument;
    }

    public Integer Run(Integer docId, ArticleMetadata articleMetadata, int repositoryId) {
        if (docId != null && repositoryId == CROSSREF_ID) {
            String doi = articleMetadata.getDoi();
            HashSet<Integer> crossrefDuplicates = this.documentDuplicateDao.getIdDocumentsByDOI(doi, repositoryId);
            if (!crossrefDuplicates.isEmpty()) {

                docId = crossrefDuplicates.stream()
                        .mapToInt(Integer::valueOf)
                        .min()
                        .orElseThrow(IllegalStateException::new);

                if (crossrefDuplicates.size() > 1) {
                    logger.info("Crossref document {} has {} duplicates. Update the oldest document", docId, crossrefDuplicates.size());
//                     deleting records in the middle of inserting might be dangerous
                    this.deleteDuplicates(crossrefDuplicates, repositoryId);
                }
            }
        }
        return docId;
    }

    public Integer deleteDuplicates(Set<Integer> duplicates, int repositoryId) {
        List<Integer> docsForDeleting = duplicates.stream()
                .sorted().skip(1).collect(Collectors.toList());
        Integer originalId = duplicates.stream()
                .sorted().limit(1).collect(Collectors.toList()).get(0);

        for (Integer doc : docsForDeleting) {
            logger.info("Setting deleted status to the doc {} because it is a duplicate of doc {}", doc, originalId);
            deleteStatusDocument.setStatus(DeletedStatus.DELETED, doc, CROSSREF_ID, true);
            /*
                instead of deleting records from the DB, let's just mark it as deleted and re-index
                this is safer IMHO
                Anton
             */
//            logger.info("Deleting of the crossref duplicate by id {} ", doc);
//            repositoryMetadataDAO.deleteDocument(doc);
//            try {
//                filesystemDAO.deleteDocument(doc, repositoryId);
//            } catch (IOException e) {
//                logger.warn("I/O exception raised while deleting document {} from index", doc);
//                logger.warn("", e);
//            }
//
//            try {
//                deleteFromIndex("articles", doc);
//            } catch (IOException e) {
//                logger.warn(e.getMessage(), e);
//            }
        }

        return originalId;
    }

    private boolean deleteFromIndex(String index, int articleId) throws IOException {
        String url = "https://index.core.ac.uk/" + index + "/article/" + articleId;
        logger.debug("Executing {}", url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("DELETE");
        return connection.getResponseCode() == 200;
    }
}
