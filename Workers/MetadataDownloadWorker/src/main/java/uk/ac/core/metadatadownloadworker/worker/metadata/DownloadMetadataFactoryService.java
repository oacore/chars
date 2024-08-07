package uk.ac.core.metadatadownloadworker.worker.metadata;

import org.springframework.stereotype.Service;
import java.util.Date;

/**
 *
 * @author Samuel Pearce <samuel.pearce@open.ac.uk>
 */
@Service
public class DownloadMetadataFactoryService {

    private final OaiPmhDownloaderService oaiPmhDownloaderService;

    public DownloadMetadataFactoryService(OaiPmhDownloaderService oaiPmhDownloaderService) {
        this.oaiPmhDownloaderService = oaiPmhDownloaderService;
    }

    public DownloadMetadata createDownloader(Integer repositoryId, Date fromDate, Date toDate) {
        //TODO - repository types should be retrieved from a Database not from enumeration 
        oaiPmhDownloaderService.init(repositoryId, fromDate, toDate);
        return oaiPmhDownloaderService;
    }
}
