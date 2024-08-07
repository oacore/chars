package uk.ac.core.elasticsearch.repositories;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ScrolledPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;
import uk.ac.core.elasticsearch.entities.BasicArticleMetadata;
import uk.ac.core.elasticsearch.services.converter.ArticleConverter;
import uk.ac.core.elasticsearch.services.model.CompactArticleBO;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Repository
public class ArticleMetadataDAOImpl implements ArticleMetadataDAO {
    private static final String ARTICLES_ALIAS = "articles";
    private static final String ARTICLE_TYPE = "article";
    private static final String DELETED_FIELD = "deleted";
    private static final String ENABLED_STATUS = "ALLOWED";
    private static final String REPOSITORY_DOCUMENT_NESTED_FIELD = "repositoryDocument";
    private static final String PDF_STATUS_FIELD = "repositoryDocument.pdfStatus";
    private static final String TEXT_STATUS_FIELD = "repositoryDocument.textStatus";
    private static final String OPTIMIZED_FOR_SCROLL_SORT = "_doc";
    private static final String FETCHED_ENABLED_ARTICLES_COUNTER_LOG = "%d enabled articles were fetched.";

    private static final int SCROLL_SIZE = 500;
    private static final long ARTICLES_AMOUNT_TO_LOG = SCROLL_SIZE * 5;

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleMetadataDAOImpl.class);

    private final ElasticsearchTemplate elasticsearchTemplate;

    public ArticleMetadataDAOImpl(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Long countAllDownloadedDocuments() {
        return elasticsearchTemplate.count(createDownloadedDocumentsQuery());
    }

    private SearchQuery createDownloadedDocumentsQuery() {
        return new NativeSearchQueryBuilder()
                .withQuery(
                        new BoolQueryBuilder().must(
                                new NestedQueryBuilder(REPOSITORY_DOCUMENT_NESTED_FIELD,
                                        new TermQueryBuilder(PDF_STATUS_FIELD, 1)
                                        , ScoreMode.Total
                                )))
                .withTypes(ARTICLE_TYPE)
                .withIndices(ARTICLES_ALIAS)
                .build();
    }

    @Override
    public void scrollEnabledArticles(Consumer<List<CompactArticleBO>> handler) {
        // 15 min
        final long searchContextExpiryTimeInMillis = 900_000;

        Page<BasicArticleMetadata> scroll = elasticsearchTemplate.startScroll(searchContextExpiryTimeInMillis, createScrollThroughEnabledArticlesQuery(), BasicArticleMetadata.class);
        String scrollId = ((ScrolledPage<BasicArticleMetadata>) scroll).getScrollId();

        int scrolledElements = 0;
        while (scroll.hasContent()) {

            logFetchedArticleAmount(scrolledElements += scroll.getNumberOfElements());

            handler.accept(scroll.getContent().stream()
                    .map(ArticleConverter::convertToArticleBO)
                    .collect(Collectors.toList()));

            scrollId = ((ScrolledPage<BasicArticleMetadata>) scroll).getScrollId();
            scroll = elasticsearchTemplate.continueScroll(
                    scrollId, searchContextExpiryTimeInMillis, BasicArticleMetadata.class
            );
        }

        elasticsearchTemplate.clearScroll(scrollId);

    }

    private void logFetchedArticleAmount(int scrolledElements) {
        if (scrolledElements % ARTICLES_AMOUNT_TO_LOG == 0) {
            LOGGER.debug(String.format(FETCHED_ENABLED_ARTICLES_COUNTER_LOG, scrolledElements));
        }
    }

    private SearchQuery createScrollThroughEnabledArticlesQuery() {

        String[] sourceFilterIncludes = new String[]{"id", TEXT_STATUS_FIELD};

        return new NativeSearchQueryBuilder()
                .withQuery(
                        new BoolQueryBuilder()
                                .must(new MatchQueryBuilder(DELETED_FIELD, ENABLED_STATUS))
//                                .must(new RangeQueryBuilder("id")
//                                        .gte(0)
//                                        .lte(20_000_000))
                )
                .withSort(new FieldSortBuilder(OPTIMIZED_FOR_SCROLL_SORT))
                .withSourceFilter(new FetchSourceFilter(sourceFilterIncludes, new String[]{""}))
                .withTypes(ARTICLE_TYPE)
                .withPageable(PageRequest.of(0, 10000))
                .withIndices(ARTICLES_ALIAS)
                .build();
    }
}