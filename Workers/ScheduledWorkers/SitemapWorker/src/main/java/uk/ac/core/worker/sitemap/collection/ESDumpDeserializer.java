package uk.ac.core.worker.sitemap.collection;

import uk.ac.core.worker.sitemap.collection.entity.ESDump;
import uk.ac.core.worker.sitemap.factory.ArticleUrls;
import java.io.IOException;
import java.util.function.Consumer;

public interface ESDumpDeserializer {

    /**
     * Deserializes the contents of the ES dump file into a given {@link Consumer<ArticleUrls>}.
     * <p>
     * Example of the line in the ES dump file:
     * {@code {"_index":"articles_2019_06_05","_type":"article","_id":"86311","_score":1,"_source":{"id":"86311","repositoryDocument":{"metadataUpdated":1516693801000,"tdmOnly":false,"textStatus":1}}}
     * }
     *
     * @param path absolute path to the es dump
     *
     * @param urlsConsumer data consumer
     * @throws IOException if a file can't be read
     */
    void deserialize(String path, Consumer<ArticleUrls> urlsConsumer) throws IOException;
}