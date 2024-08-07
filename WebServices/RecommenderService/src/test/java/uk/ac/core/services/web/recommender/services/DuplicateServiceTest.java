package uk.ac.core.services.web.recommender.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import uk.ac.core.elasticsearch.entities.ElasticSearchArticleMetadata;
import uk.ac.core.elasticsearch.entities.ElasticSearchSimilarDocument;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author lucasanastasiou
 */
public class DuplicateServiceTest {

    public DuplicateServiceTest() {
    }


    /**
     * Test of cleanResults method, of class DuplicateService.
     */
    @Test
    public void testCleanResults() {

        ElasticSearchArticleMetadata sourceArticle = this.getTestSourceArticle();
        List<ElasticSearchSimilarDocument> results = this.getTestSimilarArticlesList();
        
        
        System.out.println("UNfiltered results "+results.size());
        System.out.println("------------------");
        System.out.println("Input document");
        printList(sourceArticle);
        System.out.println("------------------");
        System.out.println("UNfiltered results");
        printList(results);
        System.out.println("");
        
        DuplicateService instance = new DuplicateService();
        List<ElasticSearchSimilarDocument> expResult = null;
        
        List<ElasticSearchSimilarDocument> result15 = new ArrayList<>(results);
        result15 = instance.cleanResults(sourceArticle, result15,15);
        List<ElasticSearchSimilarDocument> result30 = new ArrayList<>(results);
        result30 = instance.cleanResults(sourceArticle, result30,30);
        List<ElasticSearchSimilarDocument> result1 = new ArrayList<>(results);
        result1 = instance.cleanResults(sourceArticle, result1,1);

        System.out.println("After duplicate filtering  - Min distance allowed : 15 - Total results:"+result15.size());
        System.out.println("------------------");
        printList(result15);
        System.out.println("");
        System.out.println("After duplicate filtering  - Min distance allowed : 30 - Total results:"+result30.size());
        System.out.println("------------------");
        printList(result30);
        System.out.println("");
        System.out.println("After duplicate filtering  - Min distance allowed : 1 - Total results:"+result1.size());
        System.out.println("------------------");
        printList(result1);


        // with threshold 15 should cut out the 2 duplicates of the source (one exact match and one different only to a dot)
        // and the extra duplcate of the other
        assertEquals(result15.size(), 2);
        // too large threshold (30) should cut everything
        assertEquals(result30.size(), 0);
        // too small threshold should cut out only the exact duplicate
        assertEquals(result1.size(), 4);


    }

    /*
    real life scenario : this is article 82983213 that caused issue
     */
    private ElasticSearchArticleMetadata getTestSourceArticle() {
        ElasticSearchArticleMetadata esam = new ElasticSearchArticleMetadata();

        esam.setId(""+82983213);
        esam.setOai("oai:oro.open.ac.uk:47336");
        esam.setTitle("Drivers of ecosystem and climate change in tropical West Africa over the past ∼540.000 years");

        List<String> authors = new ArrayList<>();
        authors.add("Miller, Charlotte S.");
        authors.add("Gosling, William D.");
        authors.add("Kemp, David B.");
        authors.add("Coe, Angela L.");
        authors.add("Gilmour, Iain");
        esam.setAuthors(authors);

        return esam;
    }
    
    private ElasticSearchSimilarDocument convertEsamToEssd(ElasticSearchArticleMetadata esam){
        ElasticSearchSimilarDocument essd = new ElasticSearchSimilarDocument();
        essd.setId(""+esam.getId());
        essd.setTitle(esam.getTitle());
        essd.setAuthors(esam.getAuthors());
        essd.setScore(0.1);
        
        return essd;
    }

    /*
    these are the similar articles returned from test article 82983213
     */
    private List<ElasticSearchSimilarDocument> getTestSimilarArticlesList() {
        List<ElasticSearchSimilarDocument> testSimilarArticles = new ArrayList<>();

        //------------------   1st  ----------------
        ElasticSearchSimilarDocument essd1 = new ElasticSearchSimilarDocument();
        essd1.setId("" + 82983213);
        essd1.setTitle("Drivers of ecosystem and climate change in tropical West Africa over the past ∼540 000 years");
        List<String> authors1 = new ArrayList<>();
        authors1.add("Miller, Charlotte S.");
        authors1.add("Gosling, William D.");
        authors1.add("Kemp, David B.");
        authors1.add("Coe, Angela L.");
        authors1.add("Gilmour, Iain");

        essd1.setAuthors(authors1);
        essd1.setScore(3.5483558);

        //------------------   2nd  ----------------
        ElasticSearchSimilarDocument essd2 = new ElasticSearchSimilarDocument();
        essd2.setId("" + 77614647);
        essd2.setTitle("Drivers of ecosystem and climate change in tropical West Africa over the past ∼540 000 years");
        List<String> authors2 = new ArrayList<>();
        authors2.add("Miller, Charlotte S.");
        authors2.add("Gosling, William D.");
        authors2.add("Kemp, David B.");
        authors2.add("Coe, Angela L.");
        authors2.add("Gilmour, Iain");

        essd2.setAuthors(authors2);
        essd2.setScore(0.9328208);

        //------------------   3rd  ----------------
        ElasticSearchSimilarDocument essd3 = new ElasticSearchSimilarDocument();
        essd3.setId("" + 78483828);
        essd3.setTitle("What drives the recent intensified vegetation degradation in Mongolia - Climate change or human activity?");
        List<String> authors3 = new ArrayList<>();
        authors3.add("Tian, Fang");
        authors3.add("Herzschuh, Ulrike");
        authors3.add("Mischke, Steffen");
        authors3.add("Schlütz, Frank");
        essd3.setAuthors(authors3);
        essd3.setScore(0.9149124);

        //------------------   4th  ----------------
        ElasticSearchSimilarDocument essd4 = new ElasticSearchSimilarDocument();
        essd4.setId("" + 82982270);
        essd4.setTitle("Terrestrial biosphere changes over the last 120 kyr");
        List<String> authors4 = new ArrayList<>();
        authors4.add("Hoogakker, B. A. A.");
        authors4.add("Smith, R. S.");
        authors4.add("Singarayer, J. S.");
        authors4.add("Marchant, R.");
        authors4.add("Prentice, I. C.");
        authors4.add("Allen, J. R. M.");
        authors4.add("Anderson, R. S.");
        authors4.add("Bhagwat, S.A.");
        authors4.add("Behling, H.");
        authors4.add("Borisova, O.");
        authors4.add("Bush, M.");
        authors4.add("Correa-Metrio, A.");
        authors4.add("de Vernal, A.");
        authors4.add("Finch, J. M.");
        authors4.add("Fréchette, B.");
        authors4.add("Lozano-Garcia, S.");
        authors4.add("Gosling, W. D.");
        authors4.add("Granoszewski, W.");
        authors4.add("Grimm, E. C.");
        authors4.add("Grüger, E.");
        authors4.add("Hanselman, J.");
        authors4.add("Harrison, S. P.");
        authors4.add("Hill, T. R.");
        authors4.add("Huntley, B.");
        authors4.add("Jiménez-Moreno, G.");
        authors4.add("Kershaw, P.");
        authors4.add("Ledru, M.-P.");
        authors4.add("Magri, D.");
        authors4.add("McKenzie, M.");
        authors4.add("Müller, U.");
        authors4.add("Nakagawa, T.");
        authors4.add("Novenko, E.");
        authors4.add("Penny, D.");
        authors4.add("Sadori, L.");
        authors4.add("Scott, L.");
        authors4.add("Stevenson, J.");
        authors4.add("Valdes, P. J.");
        authors4.add("Vandergoes, M.");
        authors4.add("Velichko, A.");
        authors4.add("Whitlock, C.");
        authors4.add("Tzedakis, C.");
        essd4.setAuthors(authors4);
        essd4.setScore(0.7794188);

        //------------------   5th  ----------------
        ElasticSearchSimilarDocument essd5 = new ElasticSearchSimilarDocument();
        essd5.setId("" + 39826062);
        essd5.setTitle("Terrestrial biosphere changes over the last 120 kyr");
        List<String> authors5 = new ArrayList<>();
        authors5.add("Hoogakker, B. A. A.");
        authors5.add("Smith, R. S.");
        authors5.add("Singarayer, J. S.");
        authors5.add("Marchant, R.");
        authors5.add("Prentice, I. C.");
        authors5.add("Allen, J. R. M.");
        authors5.add("Anderson, R. S.");
        authors5.add("Bhagwat, S. A.");
        authors5.add("Behling, H.");
        authors5.add("Borisova, O.");
        authors5.add("Bush, M.");
        authors5.add("Correa-Metrio, A.");
        authors5.add("de Vernal, A.");
        authors5.add("Finch, J. M.");
        authors5.add("Frechette, B.");
        authors5.add("Lozano-Garcia, S.");
        authors5.add("Gosling, W. D.");
        authors5.add("Granoszewski, W.");
        authors5.add("Grimm, E. C.");
        authors5.add("Gruger, E.");
        authors5.add("Hanselman, J.");
        authors5.add("Harrison, S. P.");
        authors5.add("Hill, T. R.");
        authors5.add("Huntley, B.");
        authors5.add("Jimenez-Moreno, G.");
        authors5.add("Kershaw, P.");
        authors5.add("Ledru, Marie-Pierre");
        authors5.add("Magri, D.");
        authors5.add("McKenzie, M.");
        authors5.add("Muller, U.");
        authors5.add("Nakagawa, T.");
        authors5.add("Novenko, E.");
        authors5.add("Penny, D.");
        authors5.add("Sadori, L.");
        authors5.add("Scott, L.");
        authors5.add("Stevenson, J.");
        authors5.add("Valdes, P. J.");
        authors5.add("Vandergoes, M.");
        authors5.add("Velichko, A.");
        authors5.add("Whitlock, C.");
        authors5.add("Tzedakis, C.");
        essd5.setAuthors(authors5);
        essd5.setScore(0.7793238);

        testSimilarArticles.add(essd1);
        testSimilarArticles.add(essd2);
        testSimilarArticles.add(essd3);
        testSimilarArticles.add(essd4);
        testSimilarArticles.add(essd5);
        
        return testSimilarArticles;

    }
    
    private void printList(ElasticSearchArticleMetadata sd){
            System.out.println("\t"+sd.getId()+"\t"+sd.getTitle());            
    }
    
    private void printList(List<ElasticSearchSimilarDocument> result){
        int ctn=0;
        System.out.println("Counter\tCORE id\tSimhash\tScore\tTitle");
        for (ElasticSearchSimilarDocument sd:result){
            ctn++;
            System.out.println("" + ctn+"\t"+sd.getId()+"\t"+sd.getSimhash()+"\t"+sd.getScore()+"\t"+sd.getTitle());            
        }
    }

}
