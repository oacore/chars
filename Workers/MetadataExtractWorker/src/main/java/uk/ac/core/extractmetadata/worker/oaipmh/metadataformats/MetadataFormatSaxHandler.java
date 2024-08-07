package uk.ac.core.extractmetadata.worker.oaipmh.metadataformats;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.ac.core.extractmetadata.worker.oaipmh.XMLParser.Persist;
import uk.ac.core.extractmetadata.worker.oaipmh.models.OaiMetadataFormat;

/**
 * Parses an XML document finding MetadataFormats.
 * 
 * Once a MetadataFormat is parsed, an OaiMetadataFormat object is created and
 * is given as a parameter to Persist.Persist( )
 * @author samuel
 */
public class MetadataFormatSaxHandler extends DefaultHandler {

    private OaiMetadataFormat oaiMetadataFormats;

    private Boolean insideMetadataFormat = false;

    private boolean metadataPrefix;
    private boolean schema;
    private boolean metadataNamespace;

    private final Persist<OaiMetadataFormat> persistence;

    /**
     * When a MetadataFormat is found, it is given to Persist
     * @param persistence MetadataFormats are saved to this object
     */
    public MetadataFormatSaxHandler(Persist<OaiMetadataFormat> persistence) {
        this.persistence = persistence;
    }

    @Override
    public void startElement(String uri,
            String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equalsIgnoreCase("ListMetadataFormats")) {
            // Speed up processing. No other methods to execute
            return;
        }
        if (qName.equalsIgnoreCase("metadataFormat")) {
            this.oaiMetadataFormats = new OaiMetadataFormat();
            this.insideMetadataFormat = true;

            // Speed up processing. No other methods to execute
            return;
        }

        if (this.insideMetadataFormat) {
            if (qName.equalsIgnoreCase("metadataPrefix")) {
                this.metadataPrefix = true;
            }
            if (qName.equalsIgnoreCase("schema")) {
                this.schema = true;
            }
            if (qName.equalsIgnoreCase("metadataNamespace")) {
                this.metadataNamespace = true;
            }
        }

    }

    @Override
    public void endElement(String uri,
            String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("metadataPrefix")) {
            this.metadataPrefix = false;
        }
        if (qName.equalsIgnoreCase("schema")) {
            this.schema = false;
        }
        if (qName.equalsIgnoreCase("metadataNamespace")) {
            this.metadataNamespace = false;
        }

        // This indicates the end of an affiliation
        if (qName.equalsIgnoreCase("metadataFormat")) {
            this.insideMetadataFormat = false;
     
            // Persist the enitity to the object passed            
            if (this.persistence != null) {
                this.persistence.persist(this.oaiMetadataFormats);
            }
            this.oaiMetadataFormats = null;
        }
    }

    @Override
    public void characters(char ch[],
            int start, int length) throws SAXException {

        String content = new String(ch, start, length);

        if (this.insideMetadataFormat) {
            if (this.metadataPrefix) {
                this.oaiMetadataFormats.setMetadataPrefix(content);
            } else if (this.schema) {
                this.oaiMetadataFormats.setSchema(content);
            } else if (this.metadataNamespace) {
                this.oaiMetadataFormats.setMetadataNamespace(content);
            }
        }
    }

}
