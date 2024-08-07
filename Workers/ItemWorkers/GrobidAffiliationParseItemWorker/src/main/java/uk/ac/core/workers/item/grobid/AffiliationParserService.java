package uk.ac.core.workers.item.grobid;

import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import uk.ac.core.common.model.GrobidAffiliationAuthor;
import uk.ac.core.common.model.GrobidAffiliationHeaderAuthor;
import uk.ac.core.common.model.GrobidAffiliationInstitution;
import uk.ac.core.common.model.GrobidAffiliationInstitutionRelAuthor;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.TEI;
import uk.ac.core.database.service.affiliation.GrobidAffiliationDAO;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.grobid.processor.exceptions.GrobidProcessingException;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.Author;
import uk.ac.core.grobid.processor.parsers.TEIParser;
import uk.ac.core.grobid.processor.store.impl.StoreSeparateAddDataImpl;
import uk.ac.core.grobid.processor.tools.GrobidProcessorUnmarshaller;
/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
@Service
public class AffiliationParserService {

    @Autowired
    RepositoryDocumentDAO repositoryDocumentDAO;

    @Autowired
    FilesystemDAO filesystemDAO;
    
    @Autowired
    GrobidAffiliationDAO affiliationDAO;

    @Autowired
    TEIParser teiParser;
    
    @Autowired
    StoreSeparateAddDataImpl store;
    
    @Autowired
    GrobidProcessorUnmarshaller unmarshaller;

    Logger logger = LoggerFactory.getLogger(AffiliationParserService.class);

    public boolean process(Integer articleId) throws GrobidProcessingException, IllegalAccessException, uk.ac.core.grobid.processor.exceptions.GrobidProcessingException{
        StopWatch articleProcessTotal = new StopWatch();
        articleProcessTotal.start();
        StopWatch articleProcess = new StopWatch();
        
        Integer repositoryId = repositoryDocumentDAO.getRepositoryDocumentById(articleId).getIdRepository();
        if (repositoryId == null) {
            throw new GrobidProcessingException("Article id : " + articleId + " has no repository associated with it.");
        }

        String fileLocation = filesystemDAO.getExtractedGrobidTeiLocation(articleId, repositoryId);
        File file = new File(fileLocation);
        if (!file.exists()) {
            throw new GrobidProcessingException("File does not exist for article : " + articleId + " (repository:" + repositoryId + ")");
        }

        try {
            logger.info("Unmarshalling " + fileLocation + " ...");
            articleProcess.start("unmarshalling");
            TEI teiObject =  unmarshaller.unmarshalFile(file);
            articleProcess.stop();
            logger.debug("Successful unmarshalling in " + articleProcess.getLastTaskTimeMillis() + "ms");

            articleProcess.start("getListAffiliationAuthors");
            List<Author> authorList = teiParser.getListHeaderAuthors(teiObject);
            articleProcess.stop();
            logger.debug("Succesfully getted Authors in " + articleProcess.getLastTaskTimeMillis() + "ms");
            
            if(authorList == null || authorList.isEmpty()){
                logger.warn("Skipping article " + articleId + " in repository: " + repositoryId + ", doesnt have any affiliation data!");
                return false;
            }
            
            articleProcess.start("processingGrobidHeaderAuthors");
            List<GrobidAffiliationHeaderAuthor> headerAuthors = teiParser.processingGrobidHeaderAuthors(articleId, authorList);
            articleProcess.stop();
            logger.debug("Succesfully processed list of header Authors in " + articleProcess.getLastTaskTimeMillis() + "ms");
            
            articleProcess.start("insertGrobidAffiliation");
            for(GrobidAffiliationHeaderAuthor headerAuthor : headerAuthors){
                store.storeAffiliationSeparateAddData(headerAuthor, articleId);
            }
            logger.debug("Succesfully affiliation stored in DB in " + articleProcess.getLastTaskTimeMillis() + "ms");


        } catch (GrobidProcessingException ex) {
            logger.error("" + ex);
        }
        articleProcessTotal.stop();
        logger.debug("Article grobid service finished in " + articleProcessTotal.getLastTaskTimeMillis() + "ms");
        return true;
    }
}
