package uk.ac.core.dataprovider.api.service;

import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.common.model.legacy.ArticleMetadata;
import uk.ac.core.database.model.WorksToDocumentDTO;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.database.service.document.impl.MySQLArticleMetadataDAO;
import uk.ac.core.database.service.documetduplicates.DocumentDuplicateDao;
import uk.ac.core.dataprovider.api.model.DuplicatesDTO;
import uk.ac.core.filesystem.services.FilesystemDAO;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DuplicatesService {
    private static final LevenshteinDistance ld = new LevenshteinDistance();
    private static final int threshold = 75;

    private String path;
    //private String path = "/Users/a1/Downloads/test.csv";

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DuplicatesService.class);


    @Autowired
    private DocumentDuplicateDao documentDuplicateDao;

    @Autowired
    private DedupClient dedupClient;

    @Autowired
    private MySQLArticleMetadataDAO mySQLArticleMetadataDAO;

    @Autowired
    private RepositoryDocumentDAO repoDocumentDAO;

    @Autowired
    private FilesystemDAO filesystemDAO;


    @PostConstruct
    public void postConstruct() {
        path = filesystemDAO.getMetadataStoragePath() + "/dedup/validation_dataset.csv";
    }

    public Set<Integer> getDuplicates(Integer documentId) {
        return findAnnoyAndDoiDuplicates(documentId).stream().map(WorksToDocumentDTO::getDocumentId).collect(Collectors.toSet());
    }

    public List<WorksToDocumentDTO> findAnnoyAndDoiDuplicates(Integer documentId) {

        ArticleMetadata articleMetadata = mySQLArticleMetadataDAO.getArticleMetadata(documentId);
        if (articleMetadata != null) {
            Integer originalArticleYear = articleMetadata.getYear();
            try {
                String doi = articleMetadata.getDoi();
                String title = articleMetadata.getTitle();
                Set<Integer> doiDuplicates = new HashSet<>();
                List<WorksToDocumentDTO> realDuplicates = new ArrayList<>();

                if (doi != null) {
                    doiDuplicates = documentDuplicateDao.findAllDuplicatesByDoi(doi);
                    logger.info("Size of doi duplicates is {}", doiDuplicates.size());
                    for (Integer doiDuplicateId : doiDuplicates) {
                        ArticleMetadata currentArticleMetadata = mySQLArticleMetadataDAO.getArticleMetadata(doiDuplicateId);
                        if(currentArticleMetadata != null){
                            Integer currentArticleYear = currentArticleMetadata.getYear();
                            if(isTheSameArticles(originalArticleYear, currentArticleYear, currentArticleMetadata.getAuthorsNamesReverted(),
                                    articleMetadata.getAuthorsNamesReverted())){
                                realDuplicates.add(new WorksToDocumentDTO(doiDuplicateId, WorksToDocumentDTO.Explanation.doi));
                            }
                        }
                    }
                }


                Set<Integer> duplicatesBySmash = dedupClient.getDuplicatesRequest(title);
                for (Integer currentDocumentId : duplicatesBySmash) {
                    if (!doiDuplicates.contains(currentDocumentId)) {
                        ArticleMetadata currentArticleMetadata = mySQLArticleMetadataDAO.getArticleMetadata(currentDocumentId);
                        if (currentArticleMetadata != null) {
                            Integer currentArticleYear = currentArticleMetadata.getYear();

                            logger.info("Process document {} where main publication year is {}, main authors size is {}, " +
                                            "current publication year is {}, current author size is {}", currentDocumentId,
                                    originalArticleYear, articleMetadata.getAuthorsNamesReverted().size(), currentArticleYear,
                                    currentArticleMetadata.getAuthorsNamesReverted().size());
                            if (isTheSameArticles(originalArticleYear, currentArticleYear, currentArticleMetadata.getAuthorsNamesReverted(),
                                    articleMetadata.getAuthorsNamesReverted())) {
                                logger.info("Document {} has right creation year and Authors", currentDocumentId);
                                realDuplicates.add(new WorksToDocumentDTO(currentDocumentId, WorksToDocumentDTO.Explanation.dedupv1));
                            }
                        }
                    }
                }

                Set<Integer> duplicateIds = realDuplicates.stream()
                        .map(WorksToDocumentDTO::getDocumentId).collect(Collectors.toSet());


                if (realDuplicates.isEmpty() || !duplicateIds.contains(documentId)) {
                    realDuplicates.add(new WorksToDocumentDTO(documentId, WorksToDocumentDTO.Explanation.dedupv1));
                }

                logger.info("Total number of duplicates is {}", realDuplicates.size());
                return realDuplicates;
            } catch (Exception e) {
                logger.error("Error while fetching annoy duplicates", e);
                return Collections.singletonList(new WorksToDocumentDTO(documentId, WorksToDocumentDTO.Explanation.dedupv1));
            }
        } else {
            logger.info("Article metadata for the repo {} is null", documentId);
            return Collections.singletonList(new WorksToDocumentDTO(documentId, WorksToDocumentDTO.Explanation.dedupv1));
        }
    }

    private Boolean isTheSameArticles(Integer mainArticleYear, Integer currentArticleYear, List<String> currentArticleAuthors,
                                      List<String> mainArticleAuthors) {
        Double confidence = calculateConfidence(mainArticleYear, currentArticleYear, currentArticleAuthors, mainArticleAuthors);

        return confidence >= 0.75;
    }

    public Double calculateConfidence(Integer mainArticleYear, Integer currentArticleYear,
                                      List<String> currentArticleAuthors, List<String> mainArticleAuthors) {
        Double yearScore = 0d;
        if(mainArticleYear != null && currentArticleYear != null){
            yearScore = calculateYearScore(mainArticleYear, currentArticleYear);
        }

        Double authorsScore = 0d;
        if(!currentArticleAuthors.isEmpty() && !mainArticleAuthors.isEmpty()){
            authorsScore = getScoreAuthors(currentArticleAuthors, mainArticleAuthors);
            logger.info("Match of authors is {}", authorsScore);

        } else {
            //if authors are empty in one of the document, but years are the same we want to mark the document as duplicate
            if(yearScore == 50){
                yearScore = 75d;
            }
        }

        return BigDecimal.valueOf((authorsScore + yearScore) / 150)
                .setScale(2, RoundingMode.DOWN).doubleValue();
    }

    private Double calculateYearScore(Integer mainArticleYear, Integer currentArticleYear) {
        double score = 0d;
        if (mainArticleYear != null && currentArticleYear != null) {
            if (mainArticleYear.equals(currentArticleYear)) {
                score += 50;
            } else if (Math.abs(mainArticleYear - currentArticleYear) == 1) {
                score += 25;
            }
        }

        return score;
    }


    private Double getScoreAuthors(List<String> firstAuthors, List<String> secondAuthors) {
        Integer sizeMin = Math.min(firstAuthors.size(), secondAuthors.size());
        Integer differenceAuthors = Math.abs(firstAuthors.size() - secondAuthors.size());
        if (differenceAuthors * 1.0 / sizeMin > 0.3) {
            return 0d;
        }

        Double percentFullText = getDifferencePercent(getFullNames(firstAuthors, secondAuthors));
        if (percentFullText >= 90) {
            return percentFullText;
        }

        Double percentInitials = getDifferencePercent(getBothInitials(firstAuthors, secondAuthors));

        return Math.max(percentFullText, percentInitials);

    }

    private Double getDifferencePercent(Pair<String, String> authors) {
        int length = Math.max(authors.getLeft().length(), authors.getRight().length());
        if (length == 0) {
            return 0d;
        }

        double difference = ld.apply(authors.getLeft(), authors.getRight());
        double percent = (1 - difference / length) * 100;
//        logger.info("\nfirst: {}\nsecond: {}\nmatch: {}",
//                authors.getLeft(), authors.getRight(), percent);
        return percent;
    }

    private Pair<String, String> getFullNames(List<String> firstAuthors, List<String> secondAuthors) {
        firstAuthors.sort(String.CASE_INSENSITIVE_ORDER);
        secondAuthors.sort(String.CASE_INSENSITIVE_ORDER);

        String firstFullNames = String.join(",", firstAuthors);
        String secondFullNames = String.join(",", secondAuthors);

        return Pair.of(firstFullNames, secondFullNames);
    }

    private Pair<String, String> getBothInitials(List<String> firstAuthors, List<String> secondAuthors) {
        firstAuthors.sort(String.CASE_INSENSITIVE_ORDER);
        secondAuthors.sort(String.CASE_INSENSITIVE_ORDER);

        return Pair.of(getAuthorsInitials(firstAuthors), getAuthorsInitials(secondAuthors));
    }


    private String getAuthorsInitials(List<String> authors) {
        return authors.stream()
                .map(a -> a.replaceAll("[^a-zA-Z ]", ""))
                .map(a -> Stream.of(a.split(" ")).sorted()
                        .filter(s -> !s.isEmpty())
                        .map(w -> Character.toString(w.charAt(0)))
                        .collect(Collectors.joining("")))
                .collect(Collectors.joining(""));
    }

    public Pair<Double, Double> processDataFromFile() {

        Long startTime = System.nanoTime();

        ImmutableAverager result = new ImmutableAverager();
        try (Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
            result = stream.flatMap(e -> Arrays.stream(e.split(",")))
                    .map(Integer::parseInt)
                    .map(e -> new DuplicatesDTO(e, getDuplicates(e), findWorksDuplicates(e)))
                    .map(e -> Pair.of(calculatePrecision(e), calculateRecall(e)))
                    .reduce(new ImmutableAverager(),
                            ImmutableAverager::accept,
                            ImmutableAverager::combine);
        } catch (IOException e) {
            logger.error("Error reading file");
        }
        Long endTime = System.nanoTime();
        Long totalTime = endTime - startTime;
        logger.info("Time: {}, precision: {}, recall: {}", totalTime, result.precision(), result.recall());
        return Pair.of(result.precision(), result.recall());
    }

    public void generateDuplicatesCsv() throws IOException {
        List<DuplicatesDTO> duplicatesDTOS = generateDuplicateDto();
        writeToCsvFile(duplicatesDTOS);
    }

    private List<DuplicatesDTO> generateDuplicateDto() throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
            return stream.flatMap(e -> Arrays.stream(e.split(",")))
                    .map(Integer::parseInt)
                    .map(e -> new DuplicatesDTO(e, getDuplicates(e), findWorksDuplicates(e)))
                    .collect(Collectors.toList());
        }
    }

    private void writeToCsvFile(List<DuplicatesDTO> duplicatesDTOS) {
        File file = new File("/data/user-data/mat522/result_" + Instant.now() + ".csv");
        PrintWriter pw = null;

        try {
            pw = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        pw.write(generateStringForAllDocsFromFile(duplicatesDTOS));
        pw.close();
    }

    public void generateDatasetCsv() throws IOException {
        Map<Integer, Set<Integer>> dataFromFile = parseFile();
        final AtomicInteger recordsCounter = new AtomicInteger();

        Set<DuplicatesDTO> duplicatesDTOS = dataFromFile.entrySet().stream()
                .map(e -> new DuplicatesDTO(e.getKey(), getDuplicates(e.getKey()), e.getValue()))
                .collect(Collectors.toSet());


        File file = new File("/data/dedup-api-service/results/result_" + Instant.now() + ".csv");
        logger.info("File is created");
        try (PrintWriter pw = new PrintWriter(file)) {
            logger.info("Start writing to the file");
            for (DuplicatesDTO duplicatesDTO : duplicatesDTOS) {
                pw.append(generateStringForDocument(duplicatesDTO) + "\n");
            }
        } catch (FileNotFoundException e) {
            logger.error("Error while creating csv writer", e);
        }
        logger.info("Finished");
    }


    private String generateStringForDocument(DuplicatesDTO duplicatesDTO) {
        Integer documentId = duplicatesDTO.getDocumentId();
        Set<Integer> doiDuplicates = duplicatesDTO.getWorksDuplicates();
        Set<Integer> annoyDuplicates = duplicatesDTO.getAnnoyDuplicates();

        StringBuilder stringBuilder = new StringBuilder();

        if (doiDuplicates.isEmpty() && annoyDuplicates.isEmpty()) {
            return stringBuilder.append(documentId + ",,, \n").toString();
        }

        if (!doiDuplicates.isEmpty()) {
            for (Integer doiDocumentId : doiDuplicates) {
                stringBuilder.append(documentId + "," + doiDocumentId + ",");
                if (annoyDuplicates.contains(doiDocumentId)) {
                    stringBuilder.append(doiDocumentId);
                    annoyDuplicates.remove(doiDocumentId);
                } else {
                    stringBuilder.append(",");
                }
                stringBuilder.append("\n");
            }
        }

        if (!annoyDuplicates.isEmpty()) {
            for (Integer annoyDuplicate : annoyDuplicates) {
                stringBuilder.append(documentId + ",," + annoyDuplicate + " \n");
            }
        }
        return stringBuilder.toString();
    }

    private String generateStringForAllDocsFromFile(List<DuplicatesDTO> duplicatesDTOS) {
        StringBuilder stringBuilder = new StringBuilder();
        for (DuplicatesDTO duplicatesDTO : duplicatesDTOS) {
            stringBuilder.append(generateStringForDocument(duplicatesDTO) + "\n");
        }
        return stringBuilder.toString();
    }

    private HashSet<Integer> findWorksDuplicates(Integer documentId) {
        String doi = documentDuplicateDao.getDoiByDocumentId(documentId);
        HashSet<Integer> result = new HashSet<>(documentDuplicateDao.findAllDuplicatesByDoi(doi));
        result.remove(documentId);

        return result;
    }

    private double calculatePrecision(DuplicatesDTO duplicatesDTO) {
        Set<Integer> intersection = duplicatesDTO.getAnnoyDuplicates().stream()
                .filter(e -> duplicatesDTO.getWorksDuplicates().contains(e))
                .collect(Collectors.toSet());

        if (intersection.isEmpty()) {
            return 0d;
        } else {
            return intersection.size() * 1.0 / duplicatesDTO.getWorksDuplicates().size();
        }
    }

    private double calculateRecall(DuplicatesDTO duplicatesDTO) {
        Set<Integer> intersection = duplicatesDTO.getAnnoyDuplicates().stream()
                .filter(e -> duplicatesDTO.getWorksDuplicates().contains(e))
                .collect(Collectors.toSet());

        if (intersection.isEmpty()) {
            return 0d;
        } else {
            return intersection.size() * 1.0 / duplicatesDTO.getAnnoyDuplicates().size();
        }
    }

    public Pair<Double, Double> calculateRecallForDataset() throws IOException {

        Map<Integer, Set<Integer>> dataFromFile = parseFile();
        logger.info("Finished processing of the file");

        Set<DuplicatesDTO> duplicatesDTOS = dataFromFile.entrySet().parallelStream()
                .map(e -> new DuplicatesDTO(e.getKey(), getDuplicates(e.getKey()), e.getValue()))
                .collect(Collectors.toSet());

        logger.info("Created list of duplicatesDTOS:");

        ImmutableAverager result = duplicatesDTOS.stream().map(e -> Pair.of(calculatePrecision(e), calculateRecall(e)))
                .reduce(new ImmutableAverager(),
                        ImmutableAverager::accept,
                        ImmutableAverager::combine);

        logger.info("Result precision: {}, recall {}", result.precision(), result.recall());

        return Pair.of(result.precision(), result.recall());
    }

    private Map<Integer, Set<Integer>> parseFile() throws IOException {
        Map<Integer, Set<Integer>> result = new HashMap<>();
        Gson gson = new Gson();
        logger.info("Start reading the file");
//        try(BufferedReader br = new BufferedReader(new FileReader("/Users/a1/Downloads/deduplication_dataset_2020/Ground_Truth_data_20.jsonl"))) {
        try (BufferedReader br = new BufferedReader(new FileReader("/data/user-data/mat522/Ground_Truth_data.jsonl"))) {
            String line = br.readLine();
            Integer count = 0;
            while (line != null) {
//                count++;
//                if(count > 10000){
//                    return result;
//                }
                Map<String, Object> object = gson.fromJson(line, Map.class);
                Integer documentId = Integer.parseInt(object.get("core_id").toString());
                logger.info("Process document id {}", documentId);
                if (mySQLArticleMetadataDAO.getArticleMetadata(documentId) == null ||
                        mySQLArticleMetadataDAO.getArticleMetadata(documentId).getDeleted().equals(1)) {
                    logger.info("No article metadata for docuemnt {}", documentId);
                    line = br.readLine();
                    continue;
                }
                Set<Integer> realDuplicates = Collections.emptySet();
                if (object.get("labelled_duplicates") != null) {
                    realDuplicates = ((List<String>) object.get("labelled_duplicates")).stream()
                            .map(Integer::parseInt)
                            .filter(e -> {
                                ArticleMetadata articleMetadata = mySQLArticleMetadataDAO.getArticleMetadata(e);
                                if (articleMetadata == null) {
                                    return false;
                                }

                                DeletedStatus deletedStatus = repoDocumentDAO.getDeletedStatus(articleMetadata.getId());
                                if (deletedStatus == null || deletedStatus != DeletedStatus.ALLOWED) {
                                    return false;
                                }

                                return true;
                            })
                            .collect(Collectors.toSet());
                }


                logger.info("For document {} duplicates: {}", documentId, realDuplicates.size());

                result.put(documentId, realDuplicates);
                line = br.readLine();
            }
        }
        return result;
    }

    static class ImmutableAverager {
        private final double precisionTotal;
        private final double precisionCount;

        private final double recallTotal;
        private final double recallCount;

        ImmutableAverager() {
            this.precisionTotal = 0;
            this.precisionCount = 0;
            this.recallTotal = 0;
            this.recallCount = 0;
        }

        ImmutableAverager(double precisionTotal, double precisionCount, double recallTotal, double recallCount) {
            this.precisionTotal = precisionTotal;
            this.precisionCount = precisionCount;
            this.recallTotal = recallTotal;
            this.recallCount = recallCount;
        }

        double precision() {
            return precisionCount > 0 ? precisionTotal / precisionCount : 0;
        }

        double recall() {
            return recallCount > 0 ? recallTotal / recallCount : 0;
        }

        ImmutableAverager accept(Pair<Double, Double> data) {
            return new ImmutableAverager(precisionTotal + data.getRight(), precisionCount + 1,
                    recallTotal + data.getLeft(), recallCount + 1);
        }

        ImmutableAverager combine(ImmutableAverager other) {
            return new ImmutableAverager(precisionTotal + other.precisionTotal, precisionCount + other.precisionCount,
                    recallTotal + other.recallTotal, recallCount + other.recallCount);
        }
    }
}
