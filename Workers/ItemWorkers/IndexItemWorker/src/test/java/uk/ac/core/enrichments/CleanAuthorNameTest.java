/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.enrichments;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.ac.core.common.model.legacy.ArticleMetadata;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author samuel
 */
public class CleanAuthorNameTest {

    public CleanAuthorNameTest() {
    }

    /**
     * Test of cleanAuthor method, of class CleanAuthorName.
     */
    @Test
    public void testCleanAuthor() {
        System.out.println("cleanAuthor");

        List<String> authorsToTest = new ArrayList<>();
        authorsToTest.add("Ramon, Jan; U0004411; ; ; ; ;");
        authorsToTest.add("Panos Parchas");
        authorsToTest.add("Berlingerio, Michele; ; ; JFA; CORA;");
        authorsToTest.add("Nicole Krämer");
        authorsToTest.add("Fakultas Ilmu Komputer -");

        ArticleMetadata am = new ArticleMetadata();
        am.setAuthors(authorsToTest);

        CleanAuthorName instance = new CleanAuthorName(am);

        List<String> result = instance.cleanAuthors();

        List<String> expectedAuthors = new ArrayList<>();
        expectedAuthors.add("Ramon, Jan");
        expectedAuthors.add("Panos Parchas");
        expectedAuthors.add("Berlingerio, Michele");
        expectedAuthors.add("Nicole Krämer");
        expectedAuthors.add("Fakultas Ilmu Komputer -");

        assertEquals(expectedAuthors, result);
    }

}
