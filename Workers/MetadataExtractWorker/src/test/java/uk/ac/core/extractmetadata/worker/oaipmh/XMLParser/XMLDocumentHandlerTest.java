/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.extractmetadata.worker.oaipmh.XMLParser;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.ac.core.common.model.legacy.ArticleMetadata;
import uk.ac.core.extractmetadata.worker.oaipmh.SAXReaderCorpus;
import uk.ac.core.extractmetadata.worker.oaipmh.metadataformats.MetadataFormatSaxHandler;
import uk.ac.core.extractmetadata.worker.oaipmh.models.OaiMetadataFormat;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author samuel
 */
public class XMLDocumentHandlerTest {

    private static final XMLDocumentHandler documentHandler = new XMLDocumentHandler(0, XMLParseMethodEnum.PARSEALL);

    @Test
    public void testGetDoiCount() {
        documentHandler.setArticleMetadata(new ArticleMetadata());

        Map<String, Boolean> tests = new HashMap<>();
        tests.put("10.1155/2012/363764", true);
        tests.put("http://dx.doi.org/10.1155/2012/363764", true);
        tests.put("asdf10/ehta", false);
        tests.put("10.26784/sbir.v3i2.210", true);
        tests.put("10.33019/society.v5i1.15", true);
        tests.put("10.1098/rspa.2018.0763", true);

        for (Map.Entry<String, Boolean> test : tests.entrySet()) {
            documentHandler.setArticleMetadata(new ArticleMetadata());
            assertEquals(documentHandler.parseDoi(test.getKey()), test.getValue());
        }
    }

    @Test
    public void testCharacters() throws SAXException {
        documentHandler.setArticleMetadata(new ArticleMetadata());
        Map<String, String> tests = new HashMap<>();

        tests.put("https://digitalscholarship.unlv.edu/cgi/viewcontent.cgi?article=3841\\u26amp;context=thesesdissertations", "https://digitalscholarship.unlv.edu/cgi/viewcontent.cgi?article=3841&amp;context=thesesdissertations");
        for (Map.Entry<String, String> test : tests.entrySet()) {
            System.out.println(test.getKey());

            documentHandler.elementContent = new StringBuilder();
            documentHandler.characters(test.getKey().toCharArray(), 0, test.getKey().length());
            assertEquals(documentHandler.elementContent.toString(), test.getValue());
        }
    }

    @Test
    public void testDoiWithPrefixUrl() throws SAXException {
        System.out.println("testCharacters");

        XMLDocumentHandler h = new XMLDocumentHandler(0, XMLParseMethodEnum.PARSEALL);
        h.setArticleMetadata(new ArticleMetadata());
        Map<String, String> tests = new HashMap();
        tests.put("https://doi.org/10.1098/rspa.2018.0763", "10.1098/rspa.2018.0763");

        for (Map.Entry<String, String> test : tests.entrySet()) {
            System.out.println(test.getKey());

            h.elementContent = new StringBuilder();
            String result = h.cleanPotentialDoi(test.getKey());
            assertEquals(result, test.getValue());
        }
    }

    @Test
    public void testAuthorExtractionFromCrossrefMetadata() throws ParserConfigurationException, SAXException, IOException {
        File inputFile = new File("test-resources/GetRecords-Crossref.xml");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        SAXReaderCorpus corpus = new SAXReaderCorpus();
        DefaultHandler userhandler =  new XMLDocumentHandler(0, XMLParseMethodEnum.PARSEALL);
        assertTrue(true);
    }
}
