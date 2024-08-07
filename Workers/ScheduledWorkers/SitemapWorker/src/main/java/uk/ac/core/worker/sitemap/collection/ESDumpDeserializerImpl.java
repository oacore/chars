package uk.ac.core.worker.sitemap.collection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.core.worker.sitemap.collection.entity.ESDump;
import uk.ac.core.worker.sitemap.converter.ESDumpToSitemapFileConverter;
import uk.ac.core.worker.sitemap.factory.ArticleUrls;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ESDumpDeserializerImpl implements ESDumpDeserializer {

    private final ObjectMapper mapper;

    private static final String ENABLED_ARTICLE_STATUS = "ALLOWED";
    private static final String FILE_OUTDATED_MSG = "ES dump file is outdated.";
    private static final int ARTICLES_TO_LOG_AMOUNT = 500_000;
    private static final int ARTICLE_AMOUNT_TO_PARTITION = 49_999;
    private static final int FILE_AGE_THRESHOLD = 7;

    private static final Logger LOGGER = LoggerFactory.getLogger(ESDumpDeserializerImpl.class);

    public ESDumpDeserializerImpl(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void deserialize(String path, Consumer<ArticleUrls> urlsConsumer) throws IOException {

        if (isFileOlderThan(FILE_AGE_THRESHOLD, path)) {
            throw new IOException(FILE_OUTDATED_MSG);
        }

        long deserializedObjectCounter = 0;

        String line;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path))) {

            ArticleUrls urlsPerSitemap = new ArticleUrls();

            ESDumpToSitemapFileConverter esDumpToSitemapFileConverter = new ESDumpToSitemapFileConverter();

            while ((line = reader.readLine()) != null) {
                logPeriodically(++deserializedObjectCounter);

                if(!line.contains(ENABLED_ARTICLE_STATUS)) {
                    continue;
                }

                ArticleUrls currentUrls = esDumpToSitemapFileConverter.toArticleUrls(
                        mapper.readValue(line, ESDump.class)
                );

                int currentArticleAmount = urlsPerSitemap.size() + currentUrls.size();

                if (currentArticleAmount > ARTICLE_AMOUNT_TO_PARTITION) {
                    urlsConsumer.accept(urlsPerSitemap);
                    urlsPerSitemap.clear();
                }

                urlsPerSitemap.addAll(currentUrls);
            }

        }
    }

    private static boolean isFileOlderThan(int days, String path) throws IOException {
        return LocalDate.now().isAfter(LocalDate.ofEpochDay(Files.readAttributes(Paths.get(path), BasicFileAttributes.class)
                .lastModifiedTime().to(TimeUnit.DAYS)).plusDays(days));
    }

    private void logPeriodically(long articleCounter) {
        if (articleCounter % ARTICLES_TO_LOG_AMOUNT == 0) {
            LOGGER.debug(String.format("Deserialized #%d articles.", articleCounter));
        }
    }
}