package uk.ac.core.filesystem.services;

import org.apache.commons.lang3.tuple.Pair;
import uk.ac.core.common.model.article.DeletedStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 *
 * @author lucasanastasiou
 */
public interface FilesystemDAO {

    String getImageBasePath() throws FileNotFoundException;

    String imageDestinationPathBuilder(Integer documentId, String size) throws FileNotFoundException;

    String getPdfPath(Integer articleId, Integer repositoryId);

    String getExtensionlessDocumentPath(int articleId, int repositoryId);

    File getDocumentPath(int articleId, int repositoryId, String extension);

    String getTextPath(Integer articleId, Integer repositoryId);

    String getDeduplicationReportCachePath(Integer repositoryId);

    boolean deleteFile(String path);

    boolean moveFile(String srcPath, String dstPath);

    boolean copyFile(String srcPath, String dstPath);

    String getTextPathDeleted(Integer articleId, Integer repositoryId);

    void storeExtractedTei(Integer articleId, Integer repositoryId, String extractedTeiContent) throws IOException;

    List<Integer> getTeiFilesIdByRepositoryID(Integer repositoryId);

    List<Integer> getAllTeiFilesIDs(Integer fromRepositoryId);

    String getExtractedGrobidTeiLocation(Integer articleId, Integer repositoryId);

    void updateDocumentStatus(Integer articleId, Integer repositoryId, DeletedStatus deletedStatus);

    Long getMetadataSize(Integer repositoryId);

    File getLatestMetadataPath(Integer repositoryId);
    
    String getMetadataPath(Integer repositoryId);
    
    String getMetadataPageDownloadPath(int documentId, int repositoryId, String ext);

    String getMetadataPageDownloadPath(int documentId, int repositoryId);
    
    String getMetadataPath(Integer repositoryId, Date fromDate, Date untilDate);

    String getMetadataPathPart(Integer repositoryId);

    String getMetadataPathPart(Integer repositoryId, Date fromDate, Date untilDate);

    String getIncrementalFolder(Integer repositoryId);

    String calculatePdfHashValue(Integer articleId, Integer repositoryId);

    String getGrobidExtractedImagesPath(Integer articleId, Integer repositoryId);
    
    boolean makeDirectory(String path);

        void compress(File file) throws IOException;

    File createPathToNewMetadataXmlFile(Integer repositoryId);

    String createNewMetadataPathPart(Integer repositoryId);

    String getMetadataStoragePath(String metadataDirectoryName);
    
    String getMetadataStoragePath();

    void createSymbolicLink(Integer repositoryId, String path) throws IOException;

    Pair<LocalDate, LocalDate> getFirstIncrementalDate(Integer repositoryId);

    void deleteDocument(Integer doc, Integer repositoryId) throws IOException;

    File createCrossrefMetadataFile();

    File createEmptyMalformedReportFile();

    File getLatestCrossrefRecordsMalformedReport();
}
