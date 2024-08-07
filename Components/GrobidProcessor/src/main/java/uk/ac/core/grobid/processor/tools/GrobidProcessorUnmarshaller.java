/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.grobid.processor.tools;

import java.io.File;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.core.grobid.processor.generated.org.tei_c.ns._1.TEI;
import uk.ac.core.grobid.processor.exceptions.GrobidProcessingException;

/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
@Service
public class GrobidProcessorUnmarshaller {
    JAXBContext jc = null;
    Unmarshaller unmarshaller;
    
    Logger logger = LoggerFactory.getLogger(GrobidProcessorUnmarshaller.class);
    
    public GrobidProcessorUnmarshaller(){
        try {
            jc = JAXBContext.newInstance(TEI.class);
            unmarshaller = jc.createUnmarshaller();
        } catch (JAXBException ex) {
            logger.error("Error creating unmarshaller", ex);
        }
    }
    
    public TEI unmarshalFile(File file) throws GrobidProcessingException{
        try {
            return (TEI) unmarshaller.unmarshal(file);
        } catch (JAXBException ex) {
            logger.error("Cannot unmarhsall :" + ex);
            throw new GrobidProcessingException("Error while unmarshalling");
        }
    }
}
