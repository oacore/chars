package uk.ac.core.dataprovider.api.service.rightsretention.impl;

import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.common.model.legacy.ArticleMetadata;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.database.service.document.ArticleMetadataDAO;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.dataprovider.api.model.rightsretention.HighlightedArticleMetadata;
import uk.ac.core.dataprovider.api.model.rightsretention.ReportedArticleMetadata;
import uk.ac.core.dataprovider.api.service.rightsretention.RightsRetentionService;
import uk.ac.core.elasticsearch.entities.ElasticSearchArticleMetadata;
import uk.ac.core.elasticsearch.repositories.ArticleMetadataRepository;
import uk.ac.core.textextraction.TextExtractorService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RightsRetentionServiceImpl implements RightsRetentionService {
    private static final Logger log = LoggerFactory.getLogger(RightsRetentionServiceImpl.class);

    private static final String ARTICLES_INDEX = "articles";
    private static final String FULLTEXT = "fullText";
    private static final String DELETED = "deleted";
    private static final String REPOSITORIES = "repositories";
    private static final String REPOSITORIES_ID = "repositories.id";
    private static final String SET_SPECS = "setSpecs.keyword";
    private static final float ES_SCORE_THRESHOLD = 10.F;
    private static final double RX_SCORE_THRESHOLD = 50.;
    private static final String FULL_TEXT_PATH_PREFIX = "/data/remote/core/filesystem/text/";
    private static final List<String> KEY_PHRASES = Arrays.asList(
            "For the purpose of Open Access",
            "the author has applied a",
            "has applied",
            "author accepted manuscript",
            "version arising from this submission",
            "version arising",
            "the authors have applied",
            "Creative Commons"
    );

    private static final Map<Pattern, Double> KEY_PHRASES_SCORE_MAP = new HashMap<>();
    private static final int MAX_RIGHTS_RETENTION_LENGTH = 300;

    private final Client client;
    private final ArticleMetadataRepository articleMetadataRepository;
    private final RepositoryDocumentDAO documentDAO;
    private final ArticleMetadataDAO articleMetadataDAO;

    @Autowired
    public RightsRetentionServiceImpl(Client client, ArticleMetadataRepository articleMetadataRepository, RepositoryDocumentDAO documentDAO, ArticleMetadataDAO articleMetadataDAO) {
        this.client = client;
        this.articleMetadataRepository = articleMetadataRepository;
        this.documentDAO = documentDAO;
        this.articleMetadataDAO = articleMetadataDAO;

        KEY_PHRASES_SCORE_MAP.put(
                this.getPatternFromRegex("for\\s+the\\s+purpose\\s+of\\s+open\\s+access"), 2.);
        KEY_PHRASES_SCORE_MAP.put(
                this.getPatternFromRegex("author(s)*\\s+ha(s|ve)\\s+(agreed)*\\s*(to)*\\s*appl(ied|y)"), 1.);
        KEY_PHRASES_SCORE_MAP.put(
                this.getPatternFromRegex("author\\s+accepted\\s+manuscript\\s+version\\s+arising"), 1.5);
        KEY_PHRASES_SCORE_MAP.put(
                this.getPatternFromRegex("author\\s+accepted\\s+manuscript\\s+version\\s+"), 1.5);
        KEY_PHRASES_SCORE_MAP.put(
                this.getPatternFromRegex("author\\s+accepted\\s+manuscript"), 1.);
        KEY_PHRASES_SCORE_MAP.put(
                this.getPatternFromRegex("version\\s+arising"), 1.);
        KEY_PHRASES_SCORE_MAP.put(
                this.getPatternFromRegex("arising\\s+from\\s+this\\s+submission"), 1.5);
    }

    private Pattern getPatternFromRegex(String regex) {
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public List<HighlightedArticleMetadata> findPotentialArticles(int repositoryId) {
        return this.findPotentialArticles(repositoryId, null);
    }

    @Override
    public List<HighlightedArticleMetadata> findPotentialArticles(int repositoryId, String harvestingSet) {
        log.info("Start collecting data ...");
        log.info("Start composing request ...");

        SearchRequest request = this.composeSearchRequest(repositoryId, harvestingSet);

        log.info("Request ready");
        log.info("Searching ...");

        long start = System.currentTimeMillis(), end;
        SearchResponse response = null;
        try {
            response = this.client.search(request).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to execute ES request", e);
        }
        end = System.currentTimeMillis();

        log.info("Request executed, took {} ms", end - start);

        List<HighlightedArticleMetadata> results = this.parseResponse(response);

        log.info("Results count: {}", results.size());

        return results;
    }

    private List<HighlightedArticleMetadata> parseResponse(SearchResponse response) {
        if (response == null) {
            log.info("Response is null");
            return new ArrayList<>();
        }
        SearchHit[] results = response.getHits().getHits();
        List<HighlightedArticleMetadata> articles = new ArrayList<>();

        for (SearchHit hit : results) {
            String docId = hit.getId();
            double score = hit.getScore();

            HighlightedArticleMetadata article = new HighlightedArticleMetadata();
            ElasticSearchArticleMetadata metadata = this.articleMetadataRepository.findOneById(docId);
            article.setCoreId(Integer.parseInt(docId));
            article.setOai(metadata.getOai());
            article.setPublicationDate(metadata.getPublishedDate());
            article.setDownloadUrl(metadata.getDownloadUrl());
            article.setHighlightDataEs(hit.getHighlightFields().get(FULLTEXT).toString());
            article.setScoreEs(score);
            article.setAuthors(metadata.getAuthors());
            article.setSetSpecs(metadata.getSetSpecs());

            articles.add(article);
        }

        return articles;
    }

    private SearchRequest composeSearchRequest(int repositoryId, String harvestingSet) {
        SearchRequest searchRequest = new SearchRequest(ARTICLES_INDEX);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // `should` queries
        List<MatchPhraseQueryBuilder> phraseQueries = new ArrayList<>();
        for (String phrase : KEY_PHRASES) {
            phraseQueries.add(
                    QueryBuilders
                            .matchPhraseQuery(FULLTEXT, phrase)
                            .slop(0)
            );
        }

        // `must` queries
        TermQueryBuilder articleAllowed = QueryBuilders.termQuery(DELETED, DeletedStatus.ALLOWED.name().toUpperCase());
        ExistsQueryBuilder fullTextExists = QueryBuilders.existsQuery(FULLTEXT);
        NestedQueryBuilder repositoryQuery = QueryBuilders.nestedQuery(
                REPOSITORIES,
                QueryBuilders.termQuery(REPOSITORIES_ID, repositoryId),
                ScoreMode.Max);
        TermQueryBuilder harvestingSets = null;
        if (harvestingSet != null) {
            harvestingSets = QueryBuilders.termQuery(SET_SPECS, harvestingSet);
        }

        // highlighter on `fullText` field
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(FULLTEXT);

        // add sub-queries to bool query
        for (MatchPhraseQueryBuilder phraseQuery : phraseQueries) {
            boolQueryBuilder.should(phraseQuery);
        }
        boolQueryBuilder.must(articleAllowed);
        boolQueryBuilder.must(fullTextExists);
        boolQueryBuilder.must(repositoryQuery);
        if (harvestingSet != null) {
            boolQueryBuilder.must(harvestingSets);
        }

        sourceBuilder
                .query(boolQueryBuilder)
                .highlighter(highlightBuilder)
                .sort(SortBuilders.scoreSort())
                .size(1000)
                .minScore(ES_SCORE_THRESHOLD);

        return searchRequest.source(sourceBuilder);
    }

    public ReportedArticleMetadata processSingleDocument(HighlightedArticleMetadata metadata) {
        final int articleId = metadata.getCoreId();
        try {
            log.info("Processing single document: {}", articleId);

            // create ReportedArticleMetadata based on HighlightedArticleMetadata
            ReportedArticleMetadata ram = new ReportedArticleMetadata(metadata);

            // retrieve necessary objects from DB
            RepositoryDocument document = this.documentDAO.getRepositoryDocumentById(articleId);
            ArticleMetadata articleMetadata = this.articleMetadataDAO.getArticleMetadata(articleId);

            // initialise other fields in ReportedArticleMetadata
            ram.setTitle(articleMetadata.getTitle());
            ram.setLicenceMetadata(
                    articleMetadata.getLicense() == null ? "<null>" : articleMetadata.getLicense());

            log.info("Looking for a text of the article");
            String fullTextPath = FULL_TEXT_PATH_PREFIX
                    .concat(document.getIdRepository().toString())
                    .concat("/")
                    .concat(document.getIdDocument().toString())
                    .concat(".txt");
            File file = new File(fullTextPath);
            if (!file.exists()) {
                log.warn("Full text was not extracted for this article");
                return null;
            }

            log.info("Full text exists: {}", file.getPath());
            log.info("Reading the file ...");

            this.matchRrsPhrases(file, ram);

            return ram;
        } catch (Exception e) {
            log.error("Exception while processing single item: {}", articleId);
            log.error("", e);
        }
        return null;
    }

    private void extractAndRecogniseLicense(ReportedArticleMetadata ram, String fileContent, int startIdx, int endIdx) {
        // if the region is too large
        //
        if (endIdx - startIdx > MAX_RIGHTS_RETENTION_LENGTH) {
            startIdx = endIdx - MAX_RIGHTS_RETENTION_LENGTH;
        }
        // extract rights retention sentence
        final int charsGap = 25;
        String rightsRetentionSentence = "..."
                .concat(fileContent.substring(startIdx - charsGap, endIdx + charsGap))
                .concat("...")
                .replaceAll("\n", " "); // replace line terminators
        // extract license string
        Pattern licenceRegex = Pattern.compile(
                "appl(y|ied)\\s+a\\s+((.+)\\s+licen(c|s)e)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcherRegex = licenceRegex.matcher(rightsRetentionSentence);
        String licenseString = "not found";
        if (matcherRegex.find()) {
            licenseString = matcherRegex.group(2);
        }
        // assign it to the metadata
        ram.setRightsRetentionSentence(rightsRetentionSentence);
        ram.setLicenceRecognised(licenseString);
    }

    @Override
    public List<ReportedArticleMetadata> validatePotentialArticles(List<HighlightedArticleMetadata> articles) {
        log.info("Start validating potential RR articles ...");
        List<ReportedArticleMetadata> filtered = new ArrayList<>();
        for (HighlightedArticleMetadata metadata : articles) {
            ReportedArticleMetadata ram = this.processSingleDocument(metadata);
            if (ram != null) {
                filtered.add(ram);
            }
        }
        log.info("Validated all metadata");
        return filtered;
    }

    @Override
    public ReportedArticleMetadata validateExternalFile(File pdfFile) {
        long start, end;
        File txtFile = null;
        try (TextExtractorService textExtractor = new TextExtractorService(pdfFile.toPath())) {
            log.info("Checking if the document follows RRS ...");
            ReportedArticleMetadata ram = new ReportedArticleMetadata();

            start = System.currentTimeMillis();
            log.info("Extracting text ...");
            final String tmpTxtPath = "/tmp/"
                    .concat(FilenameUtils.getBaseName(pdfFile.getPath()))
                    .concat(".txt");
            textExtractor.extractTextFromDocumentTo(tmpTxtPath);
            end = System.currentTimeMillis();
            log.info("Done in {} ms", end - start);

            txtFile = new File(tmpTxtPath);

            start = System.currentTimeMillis();
            log.info("Matching RRS phrases ...");
            this.matchRrsPhrases(txtFile, ram);
            end = System.currentTimeMillis();
            log.info("Done in {} ms", end - start);

            return ram;
        } catch (IOException e) {
            log.error("I/O exception occurred while extracting text from uploaded PDF", e);
            return null;
        } finally {
            this.deleteFile(pdfFile);
            this.deleteFile(txtFile);
        }
    }

    private void deleteFile(File file) {
        try {
            if (file != null && Files.exists(file.toPath())) {
                Files.delete(file.toPath());
                log.info("File successfully deleted - {}", file.getPath());
            } else {
                log.warn("Could not delete file");
                log.warn("File object either null or related file does not exist");
            }
        } catch (IOException e) {
            log.error("Could not delete file because of I/O exception", e);
        }
    }

    private void matchRrsPhrases(File file, ReportedArticleMetadata ram) throws IOException {
        String fileContent = new String(Files.readAllBytes(file.toPath()));
        String rawText = fileContent
                .replaceAll("(\\w+)\\s*-\\s*(\\w+)", "$1$2") // remove hyphens inside words
                .replaceAll("\\p{P}", ""); // remove punctuations

        log.info("Begin regular expressions matching ...");
        double score = 0.;
        double maxScore = 0.;
        int minStartIdx = fileContent.length();
        int maxEndIdx = 0;
        for (Map.Entry<Pattern, Double> phraseScore : KEY_PHRASES_SCORE_MAP.entrySet()) {
            Matcher matcher = phraseScore.getKey().matcher(rawText);
            if (matcher.find()) {
                log.info("Found match for phrase `{}`", phraseScore.getKey().pattern());
                score += phraseScore.getValue();
                int startIdx = matcher.start();
                int endIdx = matcher.end();
                if (startIdx < minStartIdx) {
                    minStartIdx = startIdx;
                }
                if (endIdx > maxEndIdx) {
                    maxEndIdx = endIdx;
                }
            }
            maxScore += phraseScore.getValue();
        }

        double normalizedScore = 100 * score / maxScore;
        ram.setConfidence(normalizedScore);
        // extract rights retention sentence and recognise license data
        if (normalizedScore > RX_SCORE_THRESHOLD) {
            this.extractAndRecogniseLicense(ram, rawText, minStartIdx, maxEndIdx);
        }
    }
}
