/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.core.metadatadownloadworker.worker.metadata.oaipmh;

import java.io.IOException;
import java.net.URLEncoder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 * The default ListSets class in Harvester2 does not include the ability to List Sets using resumptionToken
 * Extend the class and add the functionality.
 * @author scp334
 */
public class ListSetsWithResumptionToken extends ORG.oclc.oai.harvester2.verb.ListSets{
       
   /**
     * @param baseURL
     * @param resumptionToken
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     * @author Tomas Korec
     */
    public ListSetsWithResumptionToken(String baseURL, String resumptionToken)
            throws IOException, ParserConfigurationException, SAXException,
            TransformerException {
        // we do not want to call the super constructor as this appends ?verb=ListSets to the end of it
        // Call the underlying method Harvest in the HarvestVerb Class
        harvest(getRequestURL(baseURL, resumptionToken));
    }
 
    /**
     * Generate a ListSets request for the given baseURL and resumptionToken
     * @param baseURL
     * @param resumptionToken
     * @return
     * @throws UnsupportedEncodingException
     * @author Tomas Korec
     */
    private static String getRequestURL(String baseURL, String resumptionToken) {
        StringBuilder requestURL =  new StringBuilder(baseURL);
        requestURL.append("?verb=ListSets");
        requestURL.append("&resumptionToken=").append(URLEncoder.encode(resumptionToken));
        return requestURL.toString();
    }

    
}
