package uk.ac.core.extractmetadata.worker.oaipmh.GetRecords;

import org.junit.jupiter.api.Test;
import org.xml.sax.helpers.DefaultHandler;
import uk.ac.core.common.model.legacy.ArticleMetadata;
import uk.ac.core.extractmetadata.worker.oaipmh.XMLParser.Persist;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CrossrefSaxHandlerTest {
    @Test
    public void testParsingXml() throws Exception {
        File inputFile = new File("test-resources/GetRecords-Crossref.xml");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        Persist<ArticleMetadata> p = new Persist<ArticleMetadata>() {
            @Override
            public void persist(ArticleMetadata am) {
                // test first call from SAX returns these values
                assertEquals("Association for Computing Machinery (ACM)", am.getPublisher());
                assertEquals("Drahomira Herrmannova and Petr Knoth", am.getAuthorsString());
                assertEquals("Semantometrics", am.getTitle());
                assertEquals("2016-06-19", am.getDate());
                assertEquals("10.1145/2910896.2925448", am.getDoi());
                assertEquals("http://www.acm.org/publications/policies/copyright_policy#Background", am.getLicense());
                assertEquals("info:doi/10.1145%2F2910896.2925448", am.getOAIIdentifier());
                assertEquals(Date.from(LocalDate.of(2021, 5, 12).atStartOfDay().toInstant(ZoneOffset.UTC)), am.getDateStamp());
            }
        };

        DefaultHandler userhandler = new CrossrefSaxHandler(p);
        saxParser.parse(inputFile, userhandler);
    }

    @Test
    public void testListRecordsManyEntriesXml() throws Exception {
        File inputFile = new File("test-resources/ListRecords-Crossref.xml");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        List<ArticleMetadata> articleMetadataList = new ArrayList<>();

        Persist<ArticleMetadata> p = new Persist<ArticleMetadata>() {
            @Override
            public void persist(ArticleMetadata am) {
                articleMetadataList.add(am);
            }
        };

        DefaultHandler userhandler = new CrossrefSaxHandler(p);
        saxParser.parse(inputFile, userhandler);

        // Research Paper
        ArticleMetadata am1 = articleMetadataList.get(0);
        assertEquals("Springer Science and Business Media LLC", am1.getPublisher());
        assertEquals("Matteo Cancellieri, Nancy Pontika, Samuel Pearce, Lucas Anastasiou and Petr Knoth", am1.getAuthorsString());
        assertEquals("Building Scalable Digital Library Ingestion Pipelines Using Microservices", am1.getTitle());
        assertEquals("2017-11-14", am1.getDate());
        assertEquals("10.1007/978-3-319-70863-8_27", am1.getDoi());
        assertEquals("http://www.springer.com/tdm", am1.getLicense());
        assertEquals("info:doi/10.1007%2F978-3-319-70863-8_27", am1.getOAIIdentifier());
        assertEquals(Date.from(LocalDate.of(2019,5, 20).atStartOfDay().toInstant(ZoneOffset.UTC)), am1.getDateStamp());

        // Research Paper
        ArticleMetadata am2 = articleMetadataList.get(1);
        assertEquals("Springer Science and Business Media LLC", am2.getPublisher());
        assertEquals("Sergey Parinov", am2.getAuthorsString());
        assertEquals("Semantic Attributes for Citation Relationships: Creation and Visualization", am2.getTitle());
        assertEquals("2017-11-14", am2.getDate());
        assertEquals("10.1007/978-3-319-70863-8_28", am2.getDoi());
        assertEquals(null, am2.getLicense());
        assertEquals("info:doi/10.1007%2F978-3-319-70863-8_28", am2.getOAIIdentifier());
        assertEquals(Date.from(LocalDate.of(2017,11, 12).atStartOfDay().toInstant(ZoneOffset.UTC)), am2.getDateStamp());

        // Book
        ArticleMetadata am3 = articleMetadataList.get(2);
        assertEquals("Brepols Publishers NV", am3.getPublisher());
        assertEquals("Marek Thue Kretschmer", am3.getAuthorsString());
        assertEquals("Latin Love Elegy and the Dawn of the Ovidian Age", am3.getTitle());
        assertEquals("2020-02-19", am3.getDate());
        assertEquals("10.1484/M.PJML-EB.5.119053", am3.getDoi());
        assertEquals(null, am3.getLicense());
        assertEquals("info:doi/10.1484%2Fpjml-eb", am3.getOAIIdentifier());
        assertEquals(Date.from(LocalDate.of(2020,02, 19).atStartOfDay().toInstant(ZoneOffset.UTC)), am3.getDateStamp());

        // Journal
        ArticleMetadata am4 = articleMetadataList.get(3);
        assertEquals("Juniper Publishers", am4.getPublisher());
        assertEquals("Fabiano Séllos Costa", am4.getAuthorsString());
        assertEquals("Focal Hepatic Steatosis in A Juvenile Green Sea Turtle - Case Report", am4.getTitle());
        assertEquals("2020-07-11", am4.getDate());
        assertEquals("10.19080/JOJWB.2020.02.555592", am4.getDoi());
        assertEquals(null, am4.getLicense());
        assertEquals("info:doi/10.19080%2FJOJWB.2020.02.555592", am4.getOAIIdentifier());
        assertEquals(Date.from(LocalDate.of(2020,10, 10).atStartOfDay().toInstant(ZoneOffset.UTC)), am4.getDateStamp());
    }
}