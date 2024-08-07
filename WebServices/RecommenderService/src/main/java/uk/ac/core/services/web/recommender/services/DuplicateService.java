package uk.ac.core.services.web.recommender.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;
import uk.ac.core.common.util.simhash.BinaryWordSeg;
import uk.ac.core.common.util.simhash.SimHash;
import uk.ac.core.elasticsearch.entities.ElasticSearchArticleMetadata;
import uk.ac.core.elasticsearch.entities.ElasticSearchSimilarDocument;

/**
 *
 * @author mc26486
 */
@Component
public class DuplicateService {

    private static final Integer DEFAULT_THRESHOLD = 15;

    public List<ElasticSearchSimilarDocument> cleanResults(ElasticSearchArticleMetadata targetArticle, List<ElasticSearchSimilarDocument> results) {
        return cleanResults(targetArticle, results, DEFAULT_THRESHOLD);
    }

    public List<ElasticSearchSimilarDocument> cleanResults(ElasticSearchArticleMetadata targetArticle, List<ElasticSearchSimilarDocument> results, int threshold) {
        /* -------------------------
         SIMHASH implementation
         -------------------------
         */
        if (results != null) {
            // Getting rid of duplicates with simhash function
            final SimHash simHasher = new SimHash(new BinaryWordSeg());

            if (results.size() > 1) {
                List<ElasticSearchSimilarDocument> duplicates = new ArrayList<>();

                //calculate the simhash of the target article (input article)
                Long simhashOfTargetArticle = new Long(0);
                if (targetArticle!=null && targetArticle.getTitle() != null && targetArticle.getAuthors() != null) {
                    simhashOfTargetArticle = simHasher.simhash64(targetArticle.getTitle() + Arrays.toString(targetArticle.getAuthors().toArray()));
                }

                // traverse the list of results and remove duplicates to target article
                for (ElasticSearchSimilarDocument essd : results) {
                    // First, calculate the simhash for each of the similar documents
                    essd.setSimhash(simHasher.simhash64(essd.getTitle() + Arrays.toString(essd.getAuthors().toArray())));

                    // similar doc is exactly the same as the target article
                    if (targetArticle != null && essd.getId().equals(targetArticle.getId())) {
                        duplicates.add(essd);
                        continue;
                    }

                    // similar doc is almost identical to the target article
                    int distanceToTargetArticle = simHasher.hammingDistance(simhashOfTargetArticle, essd.getSimhash());
                    if (distanceToTargetArticle < threshold) {
                        duplicates.add(essd);
                        continue;
                    }
                }
                results.removeAll(duplicates);

                // sort by value to avoid creating all permutations to find similar documents with the calculated simhash value
                Collections.sort(results, new Comparator<ElasticSearchSimilarDocument>() {
                    @Override
                    public int compare(ElasticSearchSimilarDocument o1, ElasticSearchSimilarDocument o2) {
                        return o1.getSimhash().compareTo(o2.getSimhash());
                    }
                });

                // traverse the rest of the list and remove results that appear twice
                for (int i = 0; i < (results.size() - 1); i++) {
                    // long distance = simHasher.hammingDistance(simHasher.simhash64(similarResults.get(i).getTitle()), simHasher.simhash64(similarResults.get(i+1).getTitle()));
                    long distance = simHasher.hammingDistance(results.get(i).getSimhash(), results.get(i + 1).getSimhash());

                    // This is calculated in percentage so >85% similarity is considered to be duplication
                    if (distance < threshold) {
                        // Add one of the two to the list of which the intersection with the results will be omitted.
                        duplicates.add(results.get(i));
                    }
                }
                results.removeAll(duplicates);
            }

            // Re-sort them by their mlt score
            Collections.sort(results, new Comparator<ElasticSearchSimilarDocument>() {

                @Override
                public int compare(ElasticSearchSimilarDocument o1, ElasticSearchSimilarDocument o2) {
                    return o2.getScore().compareTo(o1.getScore());
                }
            }
            );
            return results;
        } else {
            List<ElasticSearchSimilarDocument> emptyList = new ArrayList<ElasticSearchSimilarDocument>();
            return emptyList;
        }
    }

}
