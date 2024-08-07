package uk.ac.core.textextraction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.ac.core.textextraction.exceptions.DocumentEncryptedException;
import java.io.IOException;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.ac.core.textextraction.TestServiceFactory.newInstance;

public final class PdfExtractorTest {

    @TempDir
    static Path tempDir;

    private static final String NO_SECURITY_PDF_NAME = "nosecurity.pdf";

    /**
     * Tests extraction of password protected file (User Password). User
     * passwords are software enforced. The text is plaintext but the software
     * prevents you from
     */
    @Test
    public void shouldGetTextContainingPasswordFromPasswordProtectedDocument() throws Exception {

        String extractedText = newInstance("userpasswordencrypted.pdf").getTextContainingTitle().replaceAll("[^\\p{L}\\p{Nd}]+", "");

        String knownTitle = "HORMONEMEDIATEDSTRATEGIESTOENHANCETRAININGANDPERFORMANCE";
        assertTrue(extractedText.contains(knownTitle));
    }

    /**
     * Tests extraction of password protected file (User Password). User
     * passwords are software enforced. The text is plaintext but the software
     * prevents you from opening it. Some software allows you to override
     * these settings
     *
     * @throws java.lang.Exception
     */
    @Test
    public void failIfPdfPasswordProtected() {

        assertThrows(DocumentEncryptedException.class, () -> {
            newInstance("userpasswordencrypted.pdf").extractTextFromDocumentTo(tempDir.resolve("output").toString());
        });
    }

    /**
     * Tests extraction of password protected file (User Password). User
     * passwords are software enforced. The text is plaintext but the software
     * prevents you from
     */
    @Test
    public void testExtractTextFromPdfNoSecurity() throws Exception {
        newInstance(NO_SECURITY_PDF_NAME).extractTextFromDocumentTo(tempDir.resolve("output").toString());
    }

    @Test
    public void shouldCorrectlyGetTextContainingTitle() throws IOException {

        String text = newInstance(NO_SECURITY_PDF_NAME).getTextContainingTitle().trim();

        String expected = ("HORMONE-MEDIATED " +
                "STRATEGIES TO ENHANCE " +
                "TRAINING AND PERFORMANCE");

        assertTrue(normaliseText(text).contains(normaliseText(expected)));
   }

   private String normaliseText(String input) {
        return input
                .replace("\n", "")
                .replace("\r\n", "")
                .replace(" ", "");
   }
}