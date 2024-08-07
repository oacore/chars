package uk.ac.core.database.service.repositorySourceStatistics;

import java.util.Optional;
import uk.ac.core.database.model.RepositorySourceStatistics;

public interface RepositorySourceStatisticsDAO {
    
    void setRepositorySourceStatistics(int id_repository, Integer metadataAllCount, Integer metadataNonDeletedCount, Integer metadataWithAttachmentsCount, Integer fulltextCount);

    boolean save(RepositorySourceStatistics repositorySourceStatistics);
   
    Optional<RepositorySourceStatistics> get(int idRepository);
    
    void setFulltextCount(int id_repository, int fulltextCount);
    
    void setMetadataWithAttachmentsCount(int id_repository, int fulltextCount);

}
