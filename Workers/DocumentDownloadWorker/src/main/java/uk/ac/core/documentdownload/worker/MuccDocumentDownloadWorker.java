//package uk.ac.core.documentdownload.worker;
//
//import com.google.gson.Gson;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.List;
//import uk.ac.core.common.model.legacy.ActionType;
//import uk.ac.core.common.model.legacy.LegacyRepository;
//import uk.ac.core.common.model.legacy.RepositoryDocumentBase;
//import uk.ac.core.common.model.task.TaskItem;
//import uk.ac.core.common.model.task.parameters.RepositoryTaskParameters;
//import uk.ac.core.documentdownload.entities.DocumentDownloadMetric;
//
///**
// *
// * @author lucas
// */
//public class MuccDocumentDownloadWorker extends DocumentDownloadWorker{
//    
//    public MuccDocumentDownloadWorker(){
//        
//    }
//    
//    @Override
//    public List<TaskItem> collectData() {
//
//        this.repositoryTaskParameters = null;//new Gson().fromJson(this.currentWorkingTask.getTaskParameters(), RepositoryTaskParameters.class);
//        this.slownessService = null;//new SlownessService(this.workerStatus.getTaskStatus());
//        this.documentDownloadIssueCollector.init(repositoryTaskParameters.getRepositoryId(), ActionType.PDFS, 0);
//        this.documentDownloadIssueCollector.resetIssues();
//        this.repositoryHarvestProperties = repositoriesHarvestPropertiesDAO.load(repositoryTaskParameters.getRepositoryId());
//        this.urlFilteringService = new UrlEvaluator(this.repositoryHarvestProperties);
//        this.crawlingHeuristicService.loadHeuristic(this.repositoryTaskParameters.getRepositoryId());
//        LegacyRepository repository = repositoriesDAO.getRepositoryById(repositoryTaskParameters.getRepositoryId().toString());
//        this.domainExceptions = this.repositoriesHarvestPropertiesDAO.getRepositoryDomainExceptions(this.repositoryHarvestProperties.getRepositoryId());
//        this.numberOfRequestsPerformed = 0;
//        this.numberOfDocumentsAttempted = 0;
//        this.isAFilesystemRepo = this.fileSystemRepositoriesConfiguration.getRepositoryConfigById(this.repositoryTaskParameters.getRepositoryId()) != null;
//        this.documentDownloadMetric = new DocumentDownloadMetric();
//        this.documentDownloadMetric.setRepositoryId(repositoryTaskParameters.getRepositoryId());
//        this.documentDownloadMetric.setStartTime(System.currentTimeMillis());
//        String repositoryUrl = repository.getUri();
//        try {
//            this.repositoryDomain = new URL(repositoryUrl).getHost();
//        } catch (MalformedURLException ex) {
//            this.repositoryDomain = repositoryUrl;
//        }
//        List<RepositoryDocumentBase> documentsForDownload = this.collectDocumentsForDownload();
//        List<TaskItem> taskItems = this.convertToDocumentDownloadTaskItem(documentsForDownload);
//
//        return taskItems;
//    }
//    
//}
