package uk.ac.core.documentdownload.entities.dao;

/**
 *
 * @author mc26486
 */
public interface DocumentDownloadDAO {

     void setDownloadSuccessful(Integer documentId, String originalUrl, String downloadUrl);

     void setDownloadUnsuccessful(Integer documentId);

     void flushDocumentStatus();

}
