package uk.ac.core.worker.sitemap.generation;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import uk.ac.core.worker.sitemap.generation.entity.sitemap.SitemapFile;
import uk.ac.core.worker.sitemap.generation.entity.sitemap.SitemapUrlProperty;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;


@RunWith(JUnit4.class)
public class SitemapGeneratorImplTest {
    private static final String FAKE_URL_1 = "http://test1.com";
    private static final String FAKE_URL_2 = "http://test2.com";
    private static final String FAKE_URL_3 = "http://test3.com";
    private static final String FAKE_URL_4 = "http://test4.com";

    private static final String FAKE_SITEMAP_1 = "0.xml";
    private static final String FAKE_SITEMAPS_INDEX = "sitemap.xml";

    private static String SITEMAP_FILENAME_PATH_1;
    private static String SITEMAP_INDEX_FILENAME_PATH;
    private static String SITEMAP_PATH;

    private static final String SITEMAP_URL_PATH = "https://core.ac.uk/sitemaps/";
    private static final String SITEMAP_LASTMOD_DATE = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);

    private static final String SITEMAP_FILE_BODY_1 =
            "<?xml version='1.0' encoding='UTF-8'?>\n" +
                    "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
                    "  <url>\n" +
                    "    <loc>" + FAKE_URL_1 + "</loc>\n" +
                    "  </url>\n" +
                    "  <url>\n" +
                    "    <loc>" + FAKE_URL_2 + "</loc>\n" +
                    "  </url>\n" +
                    "  <url>\n" +
                    "    <loc>" + FAKE_URL_3 + "</loc>\n" +
                    "  </url>\n" +
                    "  <url>\n" +
                    "    <loc>" + FAKE_URL_4 + "</loc>\n" +
                    "  </url>\n" +
                    "</urlset>";

    private static final String INDEX_FILE_BODY =
            "<?xml version='1.0' encoding='UTF-8'?>\n" +
                    "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
                    "  <sitemap>\n" +
                    "    <loc>" + SITEMAP_URL_PATH + "0.xml</loc>\n" +
                    "    <lastmod>" + SITEMAP_LASTMOD_DATE + "</lastmod>\n" +
                    "  </sitemap>\n" +
                    "</sitemapindex>";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private SitemapGenerator sitemapGenerator;

    @Before
    public void before() throws IOException {
        SITEMAP_PATH = temporaryFolder.newFolder("sitemaps").getAbsolutePath() + File.separator;

        SITEMAP_FILENAME_PATH_1 = SITEMAP_PATH + FAKE_SITEMAP_1;

        SITEMAP_INDEX_FILENAME_PATH = SITEMAP_PATH + FAKE_SITEMAPS_INDEX;

        sitemapGenerator = new SitemapGeneratorImpl(configureXmlMapper());
    }

    private XmlMapper configureXmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return xmlMapper;
    }

    @Test(expected = IOException.class)
    public void indexFileCantBeGeneratedBeforeSitemapsTest() throws IOException {
        sitemapGenerator.generateIndexFile("any string");
    }

    @Test
    public void sitemapsGeneratedTest() throws IOException {
        sitemapGenerator.generateOne(createSitemap(), SITEMAP_PATH);

        Assert.assertTrue(Files.exists(Paths.get(SITEMAP_FILENAME_PATH_1)));
        Assert.assertEquals(SITEMAP_FILE_BODY_1, readFile(SITEMAP_FILENAME_PATH_1));
    }

    private SitemapFile createSitemap() {
        return new SitemapFile(createUrlProperties());
    }

    private List<SitemapUrlProperty> createUrlProperties() {
        List<SitemapUrlProperty> sitemapUrlProperties = new ArrayList<>();
        sitemapUrlProperties.add(new SitemapUrlProperty(FAKE_URL_1));
        sitemapUrlProperties.add(new SitemapUrlProperty(FAKE_URL_2));
        sitemapUrlProperties.add(new SitemapUrlProperty(FAKE_URL_3));
        sitemapUrlProperties.add(new SitemapUrlProperty(FAKE_URL_4));
        return sitemapUrlProperties;
    }

    private String readFile(String pathToFile) throws IOException {
        return String.join("\n", Files.readAllLines(Paths.get(pathToFile)));
    }

    @Test
    public void sitemapsIndexFileGeneratedTest() throws IOException {
        sitemapGenerator.generateOne(createSitemap(), SITEMAP_PATH);
        sitemapGenerator.generateIndexFile(SITEMAP_PATH);

        Assert.assertTrue(Files.exists(Paths.get(SITEMAP_INDEX_FILENAME_PATH)));
        Assert.assertEquals(INDEX_FILE_BODY, readFile(SITEMAP_INDEX_FILENAME_PATH));
    }
}
