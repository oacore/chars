/**
 * 
 * Overrides OCLC Library class with our modifications. Our modifications 
 * allow us to:
 * 
 * Override the default user agent
 * Fix an issue where non-valid XML characters cause exception
 *      See: this.stripNonValidXMLCharacters and XMLCharacterEntityEscaper.class
 * TODO: Enable error reporting
 * 
 * Copyright 2006 OCLC, Online Computer Library Center Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ORG.oclc.oai.harvester2.verb;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.ac.core.metadatadownloadworker.worker.metadata.oaipmh.XMLCharacterEntityEscaper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

/**
 * HarvesterVerb is the parent class for each of the OAI verbs.
 *
 * @author Jefffrey A. Young, OCLC Online Computer Library Center
 */
public abstract class HarvesterVerb {

    private static Logger logger = Logger.getLogger(HarvesterVerb.class);

    private String originalXml;

    static {
        BasicConfigurator.configure();
    }

    /* Primary OAI namespaces */
    public static final String SCHEMA_LOCATION_V2_0 = "http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd";
    public static final String SCHEMA_LOCATION_V1_1_GET_RECORD = "http://www.openarchives.org/OAI/1.1/OAI_GetRecord http://www.openarchives.org/OAI/1.1/OAI_GetRecord.xsd";
    public static final String SCHEMA_LOCATION_V1_1_IDENTIFY = "http://www.openarchives.org/OAI/1.1/OAI_Identify http://www.openarchives.org/OAI/1.1/OAI_Identify.xsd";
    public static final String SCHEMA_LOCATION_V1_1_LIST_IDENTIFIERS = "http://www.openarchives.org/OAI/1.1/OAI_ListIdentifiers http://www.openarchives.org/OAI/1.1/OAI_ListIdentifiers.xsd";
    public static final String SCHEMA_LOCATION_V1_1_LIST_METADATA_FORMATS = "http://www.openarchives.org/OAI/1.1/OAI_ListMetadataFormats http://www.openarchives.org/OAI/1.1/OAI_ListMetadataFormats.xsd";
    public static final String SCHEMA_LOCATION_V1_1_LIST_RECORDS = "http://www.openarchives.org/OAI/1.1/OAI_ListRecords http://www.openarchives.org/OAI/1.1/OAI_ListRecords.xsd";
    public static final String SCHEMA_LOCATION_V1_1_LIST_SETS = "http://www.openarchives.org/OAI/1.1/OAI_ListSets http://www.openarchives.org/OAI/1.1/OAI_ListSets.xsd";
    private Document doc = null;
    private String schemaLocation = null;
    private String requestURL = null;
    private static HashMap builderMap = new HashMap();
    private static Element namespaceElement = null;
    private static DocumentBuilderFactory factory = null;
    private static TransformerFactory xformFactory = TransformerFactory.newInstance();

    static {
        try {
            /* Load DOM Document */
            factory = DocumentBuilderFactory
                    .newInstance();
            factory.setNamespaceAware(true);
            Thread t = Thread.currentThread();
            DocumentBuilder builder = factory.newDocumentBuilder();
            builderMap.put(t, builder);

            DOMImplementation impl = builder.getDOMImplementation();
            Document namespaceHolder = impl.createDocument(
                    "http://www.oclc.org/research/software/oai/harvester",
                    "harvester:namespaceHolder", null);
            namespaceElement = namespaceHolder.getDocumentElement();
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:harvester",
                    "http://www.oclc.org/research/software/oai/harvester");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:oai20", "http://www.openarchives.org/OAI/2.0/");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:oai11_GetRecord",
                    "http://www.openarchives.org/OAI/1.1/OAI_GetRecord");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:oai11_Identify",
                    "http://www.openarchives.org/OAI/1.1/OAI_Identify");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:oai11_ListIdentifiers",
                    "http://www.openarchives.org/OAI/1.1/OAI_ListIdentifiers");
            namespaceElement
                    .setAttributeNS("http://www.w3.org/2000/xmlns/",
                            "xmlns:oai11_ListMetadataFormats",
                            "http://www.openarchives.org/OAI/1.1/OAI_ListMetadataFormats");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:oai11_ListRecords",
                    "http://www.openarchives.org/OAI/1.1/OAI_ListRecords");
            namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
                    "xmlns:oai11_ListSets",
                    "http://www.openarchives.org/OAI/1.1/OAI_ListSets");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the OAI response as a DOM object
     *
     * @return the DOM for the OAI response
     */
    public Document getDocument() {
        return doc;
    }

    /**
     * Get the xsi:schemaLocation for the OAI response
     *
     * @return the xsi:schemaLocation value
     */
    public String getSchemaLocation() {
        return schemaLocation;
    }

    /**
     * Get the OAI errors
     *
     * @return a NodeList of /oai:OAI-PMH/oai:error elements
     * @throws TransformerException error
     */
    public NodeList getErrors() throws TransformerException {
        if (SCHEMA_LOCATION_V2_0.equals(getSchemaLocation())) {
            return getNodeList("/oai20:OAI-PMH/oai20:error");
        } else {
            return null;
        }
    }

    /**
     * Get the OAI request URL for this response
     *
     * @return the OAI request URL as a String
     */
    public String getRequestURL() {
        return requestURL;
    }

    /**
     * Mock object creator (for unit testing purposes)
     */
    public HarvesterVerb() {
    }

    /**
     * Performs the OAI request
     *
     * @param requestURL
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     */
    public HarvesterVerb(String requestURL) throws IOException,
            ParserConfigurationException, SAXException, TransformerException {
        harvest(requestURL);
    }

    public HarvesterVerb(String requestURL, Map <String, String> additionalHeaders) throws IOException,
            ParserConfigurationException, SAXException, TransformerException {
        harvest(requestURL, additionalHeaders);
    }

    /**
     * Preforms the OAI request
     *
     * @param requestURL
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     */
    public void harvest(String requestURL) throws IOException,
            ParserConfigurationException, SAXException, TransformerException {
        harvest(requestURL,  new HashMap<>());
    }

    public void harvest(String requestURL, Map <String, String> additionalHeaders) throws IOException,
            ParserConfigurationException, SAXException, TransformerException {
        //HarvestIssueCollector harvestIssueCollector = new HarvestIssueCollector(requestURL, ActionType.METADATA_DOWNLOAD, 0);

        this.requestURL = requestURL;
        logger.debug("requestURL=" + requestURL);
        InputStream in = null;
        URL url = new URL(requestURL);
        HttpURLConnection con = null;        
        int responseCode = 0;
        // The number of attempts while request was forbidden
        int errorAttempt = 0;
        do {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "OAIHarvester/2.0 - core.ac.uk");
            con.setRequestProperty("Accept-Encoding",
                    "compress, gzip, identify");

            for (Map.Entry<String, String> header : additionalHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            con.setReadTimeout(300000);
            con.setConnectTimeout(300000);

            try {
                responseCode = con.getResponseCode();
                logger.debug("responseCode=" + responseCode);
            } catch (FileNotFoundException e) {
                // assume it's a 503 response
                logger.info(requestURL, e);
                responseCode = HttpURLConnection.HTTP_UNAVAILABLE;
            }

            if (responseCode == HttpURLConnection.HTTP_UNAVAILABLE) {
                long retrySeconds = con.getHeaderFieldInt("Retry-After", -1);
                if (retrySeconds == -1) {
                    long now = (new Date()).getTime();
                    long retryDate = con.getHeaderFieldDate("Retry-After", now);
                    retrySeconds = retryDate - now;
                }
                if (retrySeconds == 0) { // Apparently, it's a bad URL
                    throw new FileNotFoundException("Bad URL?");
                }
                System.err.println("Server response: Retry-After="
                        + retrySeconds);
                if (retrySeconds > 0) {
                    try {
                        Thread.sleep(retrySeconds * 1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            
            // Handle cases where HTTP response is 403: Forbidden
            // Often, this indicates we are hitting the server too much rather than we are forbidden to see it
            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN || responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                errorAttempt++;
                System.err.println("Server response: Returned " + HttpURLConnection.HTTP_FORBIDDEN + ". Manually retry after 10 seconds. Attempt No: " + errorAttempt);
                if (errorAttempt > 3) {
                    throw new AccessDeniedException(url.getFile(), null, "URL not accessible even after " + errorAttempt + " times with 10 seconds delay");
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } 
        } while (
                responseCode == HttpURLConnection.HTTP_UNAVAILABLE ||
                responseCode == HttpURLConnection.HTTP_FORBIDDEN ||
                responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR);
        String contentEncoding = con.getHeaderField("Content-Encoding");
        logger.debug("contentEncoding=" + contentEncoding);
        if ("compress".equals(contentEncoding)) {
            ZipInputStream zis = new ZipInputStream(con.getInputStream());
            zis.getNextEntry();
            in = zis;
        } else if ("gzip".equals(contentEncoding)) {
            in = new GZIPInputStream(con.getInputStream());
        } else if ("deflate".equals(contentEncoding)) {
            in = new InflaterInputStream(con.getInputStream());
        } else {
            in = con.getInputStream();
        }

        //convert the stream to string...
        String inputAsString = IOUtils.toString(in, "UTF-8");

        // ..so you can pass it to the correction function
        originalXml = XMLCharacterEntityEscaper.escape(stripNonValidXMLCharacters(inputAsString));

        //..and convert it back as a stream
        InputStream correctedStream = IOUtils.toInputStream(originalXml, "UTF-8");

        Thread t = Thread.currentThread();
        DocumentBuilder builder = (DocumentBuilder) builderMap.get(t);
        if (builder == null) {
            builder = factory.newDocumentBuilder();
            builderMap.put(t, builder);
        }
        doc = builder.parse(correctedStream);

        StringTokenizer tokenizer = new StringTokenizer(this.getSingleString("/*/@xsi:schemaLocation"), " ");

        StringBuffer sb;
        for(sb = new StringBuffer(); tokenizer.hasMoreTokens(); sb.append(tokenizer.nextToken())) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
        }

        this.schemaLocation = sb.toString();
    }

    /**
     * This method ensures that the output String has only
     * valid XML unicode characters as specified by the
     * XML 1.0 standard. For reference, please see
     * <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
     * standard</a>. This method will return an empty
     * String if the input is null or empty.
     * <p>
     * This method has been modified to ignore other unicode characters
     * which our SAX parser is incompatible with
     *
     * @param in The String whose non-valid characters we want to remove.
     * @return The in String, stripped of non-valid characters.
     * @Link http://blog.mark-mclaren.info/2007/02/invalid-xml-characters-when-valid-utf8_5873.html
     */
    public static String stripNonValidXMLCharacters(String in) {
        StringBuffer out = new StringBuffer(); // Used to hold the output.
        char current; // Used to reference the current character.

        if (in == null || ("".equals(in))) return ""; // vacancy test.
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
            if (
                            ((current == 0x9) ||
                                    (current == 0xA) ||
                                    (current == 0xD) ||
                                    (((current >= 0x20) && (current <= 0x7E))) ||
                                    (((current >= 0xA0) && (current <= 0xD7FF))) ||
                                    ((current >= 0xF900) && (current <= 0xFFFD)) ||
                                    ((current >= 0x10000) && (current <= 0x10FFFF))))
                out.append(current);
        }
        return out.toString();
    }

    /**
     * Get the String value for the given XPath location in the response DOM
     *
     * @param xpath
     * @return a String containing the value of the XPath location.
     * @throws TransformerException
     */
    public String getSingleString(String xpath) throws TransformerException {
        return getSingleString(getDocument(), xpath);
//        return XPathAPI.eval(getDocument(), xpath, namespaceElement).str();
//      String str = null;
//      Node node = XPathAPI.selectSingleNode(getDocument(), xpath,
//      namespaceElement);
//      if (node != null) {
//      XObject xObject = XPathAPI.eval(node, "string()");
//      str = xObject.str();
//      }
//      return str;
    }

    public String getSingleString(Node node, String xpath)
            throws TransformerException {
        return XPathAPI.eval(node, xpath, namespaceElement).str();
    }

    /**
     * Get a NodeList containing the nodes in the response DOM for the specified
     * xpath
     *
     * @param xpath
     * @return the NodeList for the xpath into the response DOM
     * @throws TransformerException
     */
    public NodeList getNodeList(String xpath) throws TransformerException {
        return XPathAPI.selectNodeList(getDocument(), xpath, namespaceElement);
    }

    public String toString() {
        // Remove starting XML declaration before returning
        return this.originalXml.substring(this.originalXml.indexOf("?>")+2);
        /**
         * Original implemtation but does not support multi-byte UTF-8 Chars
        Source input = new DOMSource(getDocument());
        StringWriter sw = new StringWriter();
        Result output = new StreamResult(sw);
        try {
            Transformer idTransformer = xformFactory.newTransformer();
            idTransformer.setOutputProperty(
                    OutputKeys.OMIT_XML_DECLARATION, "yes");
            idTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            idTransformer.transform(input, output);
            return sw.toString();
        } catch (TransformerException e) {
            return e.getMessage();
        }
         */
    }
}
