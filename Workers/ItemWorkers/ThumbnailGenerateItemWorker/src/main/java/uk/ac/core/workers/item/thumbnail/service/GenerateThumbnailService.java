package uk.ac.core.workers.item.thumbnail.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.database.service.document.DocumentDAO;

/**
 *
 * @author samuel
 */
@Service
public class GenerateThumbnailService {

    @Autowired
    RepositoryDocumentDAO repositoryDocumentDAO;
    
    @Autowired
    FilesystemDAO filesystemDAO;
    
    private static final Logger logger = LoggerFactory.getLogger(GenerateThumbnailService.class);

    public boolean createImagePreview(Integer articleId) throws FileNotFoundException {
        PdfImagePreview imageGenerator = new PdfImagePreview();
        
        Integer repositoryId = repositoryDocumentDAO.getRepositoryDocumentById(articleId).getIdRepository();
       
        String fileSource = filesystemDAO.getPdfPath(articleId,repositoryId);

        // Get the original path to the PDF
        File fileOrigPath = new File(fileSource);
        if (!fileOrigPath.exists()) {
            throw new FileNotFoundException("The File " + fileSource + " does not exist");
        }

        logger.debug("Processing file: {}", fileSource);
        
        String[] sizeCategories = {"200", "400"};

        boolean previewsCreated = true;
        // Get the temp dire

        String TempPath = "/tmp/" + articleId.toString() + ".pdf";
        File fileTempPath = new File(TempPath);

        final long startTime = System.currentTimeMillis();
        
        try {

            try {
                FileUtils.copyFile(fileOrigPath, fileTempPath);
            } catch (IOException ex) {
                logger.debug(ex.getMessage(), ex);
            }

            // If we were able to create the local tmp path, use it
            // It is super fast when generating a pdf preview if the pdf is local.
            String filePath = "";
            if (fileTempPath.exists()) {
                filePath = TempPath;
            } else {
                filePath = fileSource;
            }

            // Now generate the preview
            for (String size : sizeCategories) {

                String imgPath = filesystemDAO.imageDestinationPathBuilder(articleId, size);

                if (!new File(imgPath).exists()) {
                    logger.debug("Generating {} image preview for doc {} at {}", new Object[]{size, articleId, imgPath});

                    imageGenerator.generateImage(filePath, imgPath, String.valueOf(size));

                    // all sizes must be created in order to consider document preview generated
                    File imageFile = new File(imgPath);
                    previewsCreated = previewsCreated && imageFile.exists() && imageFile.length() > 1;
                }
            }
        } finally {

            if (fileTempPath.exists()) {
                fileTempPath.delete();
            }
        }
        
        final long duration = System.currentTimeMillis() - startTime;
        logger.debug(articleId + " took " + String.valueOf(duration) + " ms ");

        
        return new File(filesystemDAO.imageDestinationPathBuilder(articleId, "75")).exists() &&
                new File(filesystemDAO.imageDestinationPathBuilder(articleId, "200")).exists() &&
                new File(filesystemDAO.imageDestinationPathBuilder(articleId, "400")).exists();
    }

    
}
