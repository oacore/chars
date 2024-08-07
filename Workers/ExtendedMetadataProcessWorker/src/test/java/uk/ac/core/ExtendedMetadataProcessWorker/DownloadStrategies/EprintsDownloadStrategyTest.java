package uk.ac.core.ExtendedMetadataProcessWorker.DownloadStrategies;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import uk.ac.core.ExtendedMetadataProcessWorker.worker.MetadataPageProcessTaskItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class EprintsDownloadStrategyTest {

    private static final Path testResourcesDir = Paths.get("test-resources");

    @Test
    public void repositoryMetadataRecordPublishDate() throws IOException {
        EprintsDownloadStrategy strat = new EprintsDownloadStrategy();
        ClassLoader classLoader = getClass().getClassLoader();

        String fileLocation = "test-resources/eprints-deposited-on-test.html";
        Document doc = Jsoup.parse(
                FileUtils.readFileToString(
                        new File(fileLocation)
                )
        );
        LocalDateTime dateTime = strat.repositoryMetadataRecordPublishDate(new MetadataPageProcessTaskItem(1, "oai"), doc);
        assertTrue(LocalDateTime.of(2018, 10, 24, 12, 10).equals(dateTime));
    }
}