package uk.ac.core.worker.sitemap.generation;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import uk.ac.core.worker.sitemap.generation.entity.index.SitemapIndexFile;
import uk.ac.core.worker.sitemap.generation.entity.index.SitemapProperty;
import uk.ac.core.worker.sitemap.generation.entity.sitemap.SitemapFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SitemapGeneratorImpl implements SitemapGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
    private static final String SITEMAP_EXTENSION = ".xml";
    private static final String SITEMAP_INDEX_URL_TEMPLATE = "https://core.ac.uk/sitemaps/%d.xml";
    private static final String SITEMAP_INDEX_FILENAME = "sitemap.xml";

    private static final Logger LOGGER = LoggerFactory.getLogger(SitemapGeneratorImpl.class);

    private static final String NOT_DIRECTORY_MSG = "Directory must be provided for the sitemap generation.";

    private long fileNum = 0;
    private final XmlMapper xmlMapper;

    public SitemapGeneratorImpl(XmlMapper xmlMapper) {
        this.xmlMapper = xmlMapper;
    }

    @Override
    public void generateOne(SitemapFile xmlFile, String directoryPath) throws IOException {

        verifySitemapPath(directoryPath);

        xmlMapper.writeValue(new File(directoryPath + fileNum++ + SITEMAP_EXTENSION), xmlFile);
    }

    private void verifySitemapPath(String directoryPath) {
        Assert.isTrue(Files.isDirectory(Paths.get(directoryPath)), NOT_DIRECTORY_MSG + directoryPath);
    }

    @Override
    public void generateIndexFile(String directoryPath) throws IOException {

        verifySitemaps();

        List<SitemapProperty> sitemapProperties = new ArrayList<>();
        for (int i = 0; i < fileNum; i++) {
            String lastModificationDate =
                            LocalDateTime.ofInstant(
                                    Files.readAttributes(Paths.get(directoryPath + i + SITEMAP_EXTENSION), BasicFileAttributes.class)
                                            .lastModifiedTime().toInstant(),
                                    ZoneId.systemDefault()
                            ).format(DATE_FORMATTER);

            sitemapProperties.add(
                    new SitemapProperty(String.format(SITEMAP_INDEX_URL_TEMPLATE, i),
                    lastModificationDate
                )
            );
        }
        xmlMapper.writeValue(new File(directoryPath + SITEMAP_INDEX_FILENAME), new SitemapIndexFile(sitemapProperties));
        fileNum = 0;
    }

    private void verifySitemaps() throws IOException {
        if (fileNum == 0) {
            throw new IOException("Sitemaps must be generated before generating index file.");
        }
    }
}