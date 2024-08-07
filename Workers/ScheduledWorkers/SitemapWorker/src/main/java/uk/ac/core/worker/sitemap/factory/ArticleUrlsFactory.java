package uk.ac.core.worker.sitemap.factory;

import uk.ac.core.elasticsearch.services.model.CompactArticleBO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ArticleUrlsFactory {

    private static final String articlePreviewUrlPath = "https://core.ac.uk/display/";
    private static final String articleFullTextUrlPath = "https://core.ac.uk/reader/";
    private static final String articleDownloadUrlTemplate = "https://core.ac.uk/download/pdf/%s.pdf";

    public static ArticleUrls newInstance(CompactArticleBO compactArticleBO) {

        List<String> articleUrls = new ArrayList<>();

        if (compactArticleBO.isFullText()) {
            articleUrls.addAll(Arrays.asList(
                    articleFullTextUrlPath + compactArticleBO.getDocumentId(),
                    String.format(articleDownloadUrlTemplate, compactArticleBO.getDocumentId()))
            );
        }
        articleUrls.add(articlePreviewUrlPath + compactArticleBO.getDocumentId());
        return new ArticleUrls(articleUrls);
    }
}
