package uk.ac.core.elasticsearch.recommendation;

import com.google.gson.JsonObject;

import java.util.Calendar;
import java.util.List;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.functionscore.DecayFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ExponentialDecayFunctionBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.stereotype.Service;
import uk.ac.core.elasticsearch.entities.ElasticSearchSimilarDocument;

/**
 * @author mc26486
 */
@Service
public class ElasticSearchRecommendationService {

    public static final String RESULT_TYPE_OUTPUT = "output";
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    private static final String[] STOPWORDS = {"a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "to", "was", "will", "with"};

    private final String[] MLTFields = {"fullText", "description", "title"};

    /**
     * Returns a list of ElasticSearchSimilarDocuments based on an already
     * indexed articleID.
     *
     * @param articleId       an index article ID
     * @param blackList       a list of blacklisted articleID's
     * @param repositoryId    the Repository ID of the article. Null if unknown
     * @param algorithmParams
     * @return
     */
    public List<ElasticSearchSimilarDocument> moreLikeThisWithArticleID(Integer articleId,
                                                                        List<Integer> blackList,
                                                                        String repositoryId,
                                                                        JsonObject algorithmParams, String resultType) {
        MoreLikeThisQueryBuilder.Item item = null;
        if (resultType.equals(RESULT_TYPE_OUTPUT)) {
            item = new MoreLikeThisQueryBuilder.Item("articles", "article", String.valueOf(articleId));

        } else {
            item = new MoreLikeThisQueryBuilder.Item("works", "works", String.valueOf(articleId));
        }
        MoreLikeThisQueryBuilder.Item[] items = {item};
        MoreLikeThisQueryBuilder mltQuery = QueryBuilders.moreLikeThisQuery(MLTFields, null, items);

        return this.moreLikeThis(mltQuery, blackList, repositoryId, algorithmParams, resultType);
    }

    public List<ElasticSearchSimilarDocument> moreLikeThis(String title,
                                                           String aabstract,
                                                           List<Integer> blackList,
                                                           String repositoryId,
                                                           JsonObject algorithmParams, String resultType) {

        // don't provide search just only for title
        if (aabstract == null || aabstract.isEmpty()) {
            return null;
        }

        String likeText = title + " " + aabstract;

        // Multiple to single spaces and trimmed spaces at the start/end of string (in case the
        // above added unnecessary spaces.
        likeText = likeText.replaceAll("\\s+", " ").trim();

        return this.moreLikeThis(likeText, blackList, repositoryId, algorithmParams, resultType);
    }

    /**
     * Gets a list of ElasticSearchSimilarDocument's based on free text input.
     * There are no limits however, elasticsearch will return better quality
     * results with more text
     *
     * @param freeText        Any string
     * @param blackList
     * @param repositoryId
     * @param algorithmParams
     * @return
     */
    public List<ElasticSearchSimilarDocument> moreLikeThis(String freeText,
                                                           List<Integer> blackList,
                                                           String repositoryId,
                                                           JsonObject algorithmParams, String resultType) {

        String[] likeTexts = {freeText};

        MoreLikeThisQueryBuilder mltQuery = QueryBuilders.moreLikeThisQuery(MLTFields, likeTexts, null);

        return this.moreLikeThis(mltQuery, blackList, repositoryId, algorithmParams, resultType);
    }

    private List<ElasticSearchSimilarDocument> moreLikeThis(MoreLikeThisQueryBuilder mltQueryBuilder,
                                                            List<Integer> blackList,
                                                            String repositoryId,
                                                            JsonObject algorithmParams, String resultType) {

        // parse algorithm parameters from json object - use defualts if not set
        final Boolean useYearDecayScoring = algorithmParams.has("useFullTextFilter") ? algorithmParams.get("useFullTextFilter").getAsBoolean() : Boolean.TRUE;
        final Boolean useFullTextFilter = algorithmParams.has("useYearDecayScoring") ? algorithmParams.get("useYearDecayScoring").getAsBoolean() : Boolean.TRUE;
        final Integer minDocFreq = algorithmParams.has("min_doc_freq") ? algorithmParams.get("min_doc_freq").getAsInt() : 2;
        final Integer year_decay_scale = algorithmParams.has("year_decay_scale") ? algorithmParams.get("year_decay_scale").getAsInt() : 20;
        final Integer year_decay_origin = Calendar.getInstance().get(Calendar.YEAR);

        BoolQueryBuilder boolQueryBuilder = boolQuery();

        // mlt query part        
        mltQueryBuilder.include(false)
                .minTermFreq(1)
                .maxQueryTerms(12)
                .minDocFreq(5)
                .minWordLength(3)
                .stopWords(STOPWORDS);

        DecayFunctionBuilder decayFunctionBuilder = new ExponentialDecayFunctionBuilder("year", new Long(2018), new Long(20), new Long(0), 0.75);
        if (!resultType.equals(RESULT_TYPE_OUTPUT)) {
            decayFunctionBuilder = new ExponentialDecayFunctionBuilder("yearPublished", new Long(2018), new Long(20), new Long(0), 0.75);
        }
        FunctionScoreQueryBuilder functionScoreQueryBuilder = new FunctionScoreQueryBuilder(mltQueryBuilder, decayFunctionBuilder);

        // deleted:ALLOWED filter
        QueryBuilder allowedTermQuery = QueryBuilders.termQuery("deleted", "ALLOWED");


        // authors exists filter
        QueryBuilder authorsExistsQuery = QueryBuilders.existsQuery("authors");

//        // fullText exists : must or should?
//        QueryBuilder fullTextExistsQuery = QueryBuilders.existsQuery("fullText");
//        boolQueryBuilder = boolQueryBuilder.must(fullTextExistsQuery);

        boolQueryBuilder = boolQueryBuilder.must(functionScoreQueryBuilder);
        if (resultType.equals(RESULT_TYPE_OUTPUT)) {
            boolQueryBuilder = boolQueryBuilder.must(allowedTermQuery);
        }
        boolQueryBuilder = boolQueryBuilder.must(authorsExistsQuery);

        QueryBuilder yearExistsQuery = QueryBuilders.existsQuery("year");
        if (!resultType.equals(RESULT_TYPE_OUTPUT)) {
            yearExistsQuery = QueryBuilders.existsQuery("yearPublished");
        }
        QueryBuilder fullTextExistsQuery = QueryBuilders.existsQuery("fullText");
        QueryBuilder descriptionExistsQuery = QueryBuilders.existsQuery("description");

        boolQueryBuilder = boolQueryBuilder.should(yearExistsQuery);
        boolQueryBuilder = boolQueryBuilder.should(fullTextExistsQuery);
        boolQueryBuilder = boolQueryBuilder.should(descriptionExistsQuery);

        if (blackList != null) {
            for (Integer blacklistId : blackList) {
                boolQueryBuilder.mustNot(termQuery("id", Integer.toString(blacklistId)));
            }
        }

        if (repositoryId != null && !repositoryId.isEmpty()) {
            NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("repositories", termQuery("repositories.id", repositoryId), ScoreMode.Total);
            boolQueryBuilder.must(nestedQueryBuilder);
        }

        SearchQuery searchQuery = new NativeSearchQuery(boolQueryBuilder);
        if (!resultType.equals(RESULT_TYPE_OUTPUT)) {
            searchQuery.addIndices("works");
            searchQuery.addTypes("works");
        } else {
            searchQuery.addIndices("articles");
            searchQuery.addTypes("article");
        }
        String[] includes = {"id", "title", "authors", "year", "publishedYear", "deleted", "relations", "repositories.name", "publisher", "language.code", "repositories.id", "urls", "downloadUrl"};
        SourceFilter sf = new FetchSourceFilter(includes, null);
        searchQuery.addSourceFilter(sf);
        searchQuery.setPageable(PageRequest.of(0, 60));

        System.out.println("searchQuery = " + searchQuery.getQuery().toString().replaceAll("\n", "").replaceAll(" ", ""));


        Long start = System.currentTimeMillis();
        List<ElasticSearchSimilarDocument> results = elasticsearchTemplate.query(searchQuery, new ElasticSearchSimilarDocumentExtractor());
        Long end = System.currentTimeMillis();
        System.out.println("ES duration = " + (end - start) + " ms " + results.size());

        return results;
    }

    public void setElasticsearchTemplate(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

}
