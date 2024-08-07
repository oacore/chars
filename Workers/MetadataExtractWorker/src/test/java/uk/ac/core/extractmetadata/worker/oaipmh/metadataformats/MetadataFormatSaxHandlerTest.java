/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.extractmetadata.worker.oaipmh.metadataformats;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.junit.jupiter.api.Test;
import org.xml.sax.helpers.DefaultHandler;
import uk.ac.core.extractmetadata.worker.oaipmh.XMLParser.Persist;
import uk.ac.core.extractmetadata.worker.oaipmh.models.OaiMetadataFormat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author samuel
 */
public class MetadataFormatSaxHandlerTest {

    public MetadataFormatSaxHandlerTest() {
    }

    @Test
    public void testParsingXml() throws Exception {
        File inputFile = new File("test-resources/metadataFormat.xml");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        final AtomicInteger i = new AtomicInteger(0);
        Persist<OaiMetadataFormat> p = new Persist<OaiMetadataFormat>() {
            @Override
            public void persist(OaiMetadataFormat format) {
                // test first call from SAX returns these values
                if (i.get() == 0) {
                    assertEquals("didl", format.getMetadataPrefix());
                    assertEquals("http://standards.iso.org/ittf/PubliclyAvailableStandards/MPEG-21_schema_files/did/didl.xsd", format.getSchema());
                    assertEquals("urn:mpeg:mpeg21:2002:02-DIDL-NS", format.getMetadataNamespace());
                }
                i.addAndGet(1);
            }
        };

        DefaultHandler userhandler = new MetadataFormatSaxHandler(p);
        saxParser.parse(inputFile, userhandler);
        assertEquals(7, i.get());
    }

}
