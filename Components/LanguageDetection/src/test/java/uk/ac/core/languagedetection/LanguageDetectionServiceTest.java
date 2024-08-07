package uk.ac.core.languagedetection;

import org.junit.jupiter.api.Test;
import uk.ac.core.languagenormalise.NormaliseLanguage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 *
 * @author lucas
 */
public class LanguageDetectionServiceTest {

    public LanguageDetectionServiceTest() {
    }

    /**
     * Test of detectLanguage method, of class LanguageDetectionService.
     */
    @Test
    public void testDetectLanguage() throws Exception {
        System.out.println("detectLanguage");
        LanguageDetectionService service = new LanguageDetectionService();
        assertEquals("eng", new NormaliseLanguage(service.detectLanguage("This is english text obviously, I expect to identify it as English")).asIso639_3());
        assertEquals("ell", new NormaliseLanguage(service.detectLanguage("Αυτό το κειμενο είναι ελληνικά")).asIso639_3());
    }
}
