package uk.ac.core.workers.item.grobid;

import org.grobid.core.engines.Engine;
import org.grobid.core.engines.config.GrobidAnalysisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.workers.item.grobid.exceptions.GrobidExtractionException;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class GrobidExtractionService {

    @Autowired
    FilesystemDAO filesystemDAO;

    @Autowired
    Engine grobidEngine;

    @Autowired
    RepositoryDocumentDAO repositoryDocumentDAO;

    private static final Logger LOGGER = LoggerFactory.getLogger(GrobidExtractionService.class);

    boolean extractAndStore(Integer articleId) throws GrobidExtractionException {

        Integer repositoryId = repositoryDocumentDAO.getRepositoryDocumentById(articleId).getIdRepository();

        if (repositoryId==null){
            throw new GrobidExtractionException("No repository found for this article id : "+articleId);
        }
        String inputFilePath = filesystemDAO.getPdfPath(articleId, repositoryId);
        String extractedImagesDirPath = filesystemDAO.getGrobidExtractedImagesPath(articleId, repositoryId);
        
        GrobidAnalysisConfig.GrobidAnalysisConfigBuilder builder = GrobidAnalysisConfig.builder();
        builder.consolidateCitations(0);
        builder.consolidateHeader(0);
        builder.startPage(-1);
        builder.endPage(-1);
        builder.generateTeiIds(false);
        builder.generateTeiCoordinates(null);
        builder.pdfAssetPath(null);
//        builder.pdfAssetPath(new File(extractedImagesDirPath));
        builder.withPreprocessImages(false);
        
        GrobidAnalysisConfig grobidAnalysisConfig = builder.build();
        
        try {
            File inputFile = new File(inputFilePath);
            String extractedTei = grobidEngine.fullTextToTEI(inputFile, grobidAnalysisConfig);
//grobidEngine.fullTextToTEI(inputFile,false,false,null,-1,-1,false);

            filesystemDAO.storeExtractedTei(articleId, repositoryId, extractedTei);

        } catch (IOException ioe){
            ioe.printStackTrace();
            LOGGER.info(ioe.getMessage());
            throw new GrobidExtractionException("IOException");
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage());
            ex.printStackTrace();
            throw new GrobidExtractionException("Generic exception while extracting");
        }

        return true;
    }

}
