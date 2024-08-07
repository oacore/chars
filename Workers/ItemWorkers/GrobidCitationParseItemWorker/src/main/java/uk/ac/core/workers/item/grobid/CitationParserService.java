package uk.ac.core.workers.item.grobid;

import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.ListBibl;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.TEI;
import uk.ac.core.common.model.GrobidCitation;
import uk.ac.core.database.languages.LanguageDAO;
import uk.ac.core.database.service.citation.GrobidCitationDAO;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.grobid.processor.exceptions.GrobidProcessingException;
import uk.ac.core.grobid.processor.parsers.TEIParser;
import uk.ac.core.grobid.processor.tools.GrobidProcessorUnmarshaller;
/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
@Service
public class CitationParserService {

    @Autowired
    RepositoryDocumentDAO repositoryDocumentDAO;

    @Autowired
    FilesystemDAO filesystemDAO;
    
    @Autowired
    GrobidCitationDAO grobidCitationDAO;

    @Autowired
    TEIParser teiParser;

    @Autowired
    LanguageDAO languageDAO;
    
    @Autowired
    GrobidProcessorUnmarshaller unmarshaller;

    Logger logger = LoggerFactory.getLogger(CitationParserService.class);

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

            articleProcess.start("getListBibl");
            ListBibl listBibl = teiParser.getListBibl(teiObject);
            articleProcess.stop();
            logger.debug("Succesfully getted ListBibl in " + articleProcess.getLastTaskTimeMillis() + "ms");

            articleProcess.start("getListOfGrobidCitationsFromListBibl");
            List<GrobidCitation> grobidCitations = teiParser.getListOfGrobidCitationsFromListBibl(articleId, listBibl);
            articleProcess.stop();
            logger.debug("Succesfully parsed list of citations in " + articleProcess.getLastTaskTimeMillis() + "ms");
            
            articleProcess.start("insertGrobidCitation");
            for (GrobidCitation grobidCitation : grobidCitations) {
                Integer citationKey = grobidCitationDAO.insertGrobidCitation(grobidCitation);
                
                /*if(citationKey != null){
                    // store authors and bibleScope to the separate tables
                    teiParser.stroreCitationSeparateAddData(grobidCitation, citationKey);
                }*/
            }
            articleProcess.stop();
            logger.debug("Succesfully citations stored in DB in " + articleProcess.getLastTaskTimeMillis() + "ms");

            String lang = teiObject.getLang();
            if (lang != null) {
                languageDAO.insertLanguageForDocumentBy2LetterCountryCode(articleId, lang);
            }

        } catch (GrobidProcessingException ex) {
            logger.error("" + ex);
        }
        articleProcessTotal.stop();
        logger.debug("Article grobid service finished in " + articleProcessTotal.getLastTaskTimeMillis() + "ms");
        return true;
    }
}
