package uk.ac.core.extractmetadata.worker.taskitem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.configuration.filesystem.FileSystemRepositoriesConfiguration;
import uk.ac.core.common.configuration.filesystem.FileSystemRepositoryConfig;


/**
 * @author Giorgio Basile
 * @since 21/04/2017
 */

@Service
public class ExtractMetadataTaskItemStatusFactory {

    @Autowired
    private FileSystemRepositoriesConfiguration fileSystemRepositoriesConfiguration;

    public ExtractMetadataTaskItemStatus create(Integer repositoryId){
        FileSystemRepositoryConfig fsRepositoryConfig = fileSystemRepositoriesConfiguration.getRepositoryConfigById(repositoryId);
        if(fsRepositoryConfig != null){
            return new FsExtractMetadataTaskItemStatus();
        } else {
            return new ExtractMetadataTaskItemStatus();
        }

    }

}
