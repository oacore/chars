package uk.ac.core.extractmetadata.worker.oaipmh.GetRecords;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.TaskStatus;
import uk.ac.core.database.service.document.ArticleMetadataDAO;
import uk.ac.core.database.service.document.DocumentTdmStatusDAO;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.database.service.document.RepositoryMetadataDAO;
import uk.ac.core.database.service.documetduplicates.DocumentDuplicateDao;
import uk.ac.core.database.service.repositories.RepositoriesHarvestPropertiesDAO;
import uk.ac.core.database.service.repositoryDepositData.RepositoryDepositData;
import uk.ac.core.database.service.repositorySourceStatistics.RepositorySourceStatisticsDAO;
import uk.ac.core.extractmetadata.repository.RepositoryExcludedSetsRepository;
import uk.ac.core.extractmetadata.worker.edgecases.CrossrefDuplicates;
import uk.ac.core.extractmetadata.worker.edgecases.DeleteStatusDocument;
import uk.ac.core.extractmetadata.worker.issue.MetadataExtractIssueService;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.worker.WorkerStatus;

@Service
public class ArticleMetadataPersistFactory {

    private RepositoriesHarvestPropertiesDAO repositoriesHarvestPropertiesDAO;
    private DocumentTdmStatusDAO documentTdmStatusDAO;
    private RepositoryDocumentDAO repositoryDocumentDAO;
    private ArticleMetadataDAO articleMetadataDAO;
    private FilesystemDAO filesystemDAO;
    private RepositoryMetadataDAO repositoryMetadataDAO;
    private WorkerStatus workerStatus;
    private RepositoryDepositData repositoryDepositDataDAO;
    private RepositorySourceStatisticsDAO repositorySourceStatisticsDAO;
    private MetadataExtractIssueService metadataExtractIssueService;
    private RepositoryExcludedSetsRepository repositoryExcludedSetsRepository;
    private DocumentDuplicateDao documentDuplicateDao;
    private CrossrefDuplicates crossrefDuplicates;
    private DeleteStatusDocument deleteDocument;

    @Autowired
    public ArticleMetadataPersistFactory(RepositoriesHarvestPropertiesDAO repositoriesHarvestPropertiesDAO, DocumentTdmStatusDAO documentTdmStatusDAO, RepositoryDocumentDAO repositoryDocumentDAO, ArticleMetadataDAO articleMetadataDAO, FilesystemDAO filesystemDAO, RepositoryMetadataDAO repositoryMetadataDAO, WorkerStatus workerStatus, RepositoryDepositData repositoryDepositDataDAO, RepositorySourceStatisticsDAO repositorySourceStatisticsDAO, MetadataExtractIssueService metadataExtractIssueService, RepositoryExcludedSetsRepository repositoryExcludedSetsRepository, DocumentDuplicateDao documentDuplicateDao, CrossrefDuplicates crossrefDuplicates, DeleteStatusDocument deleteDocument) {
        this.repositoriesHarvestPropertiesDAO = repositoriesHarvestPropertiesDAO;
        this.documentTdmStatusDAO = documentTdmStatusDAO;
        this.repositoryDocumentDAO = repositoryDocumentDAO;
        this.articleMetadataDAO = articleMetadataDAO;
        this.filesystemDAO = filesystemDAO;
        this.repositoryMetadataDAO = repositoryMetadataDAO;
        this.workerStatus = workerStatus;
        this.repositoryDepositDataDAO = repositoryDepositDataDAO;
        this.repositorySourceStatisticsDAO = repositorySourceStatisticsDAO;
        this.metadataExtractIssueService = metadataExtractIssueService;
        this.repositoryExcludedSetsRepository = repositoryExcludedSetsRepository;
        this.documentDuplicateDao = documentDuplicateDao;
        this.crossrefDuplicates = crossrefDuplicates;
        this.deleteDocument = deleteDocument;
    }

    public ArticleMetadataPersist create(TaskStatus taskStatus, int repositoryId) {
        return new ArticleMetadataPersist(
                repositoryMetadataDAO,
                crossrefDuplicates,
                deleteDocument,
                articleMetadataDAO,
                repositoryDocumentDAO,
                documentTdmStatusDAO,
                repositoryDepositDataDAO,
                repositorySourceStatisticsDAO,
                taskStatus,
                metadataExtractIssueService,
                repositoryId,
                repositoriesHarvestPropertiesDAO.load(repositoryId).isTdmOnly(),
                repositoryExcludedSetsRepository.getSetSpecsForExclude((long) repositoryId)
        );
    }
}