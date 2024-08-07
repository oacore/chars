package uk.ac.core.documentdownload.entities.dao;

/**
 *
 * @author samuel
 */
public interface DocumentDownloadStatusDAO {
    
    public void setDownloadStatus(Integer documentId, String status);
    
}
