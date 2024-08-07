package uk.ac.core.documentdownload.downloader;

import org.junit.jupiter.api.Test;
import uk.ac.core.textextraction.extractor.TextExtractorType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author samuel
 */
public class DocumentFileCheckerTest {

    public DocumentFileCheckerTest() {
    }

    /**
     * Test of isTitleMatching method, of class DocumentFileChecker.
     */
    @Test
    public void testIsTitleMatching() throws Exception {
        String title = "Optical Excitation of Surface Plasmon Polaritons on Novel Bigratings";
        assertTrue(DocumentFileChecker.isTitleMatching(title, "test-resources/title_match.pdf", TextExtractorType.PDF.toString(), 0L));

        
    }

    @Test
    public void testIsTitleMatching2() throws Exception {
        String title = "Pathways to power: class, hyper - agency and the French corporate elite";
        assertTrue(DocumentFileChecker.isTitleMatching(title, "test-resources/title_match2.pdf", TextExtractorType.PDF.toString(), 0L));
    }

    @Test
    public void testIsValidFile() throws IOException {
        assertTrue(DocumentFileChecker.isFileValid(Paths.get("src/test/resources/pdf.pdf").toAbsolutePath().toString()));
    }

    @Test
    public void isPdfTest() throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get("src/test/resources/pdf.pdf"));
        assertTrue(DocumentFileChecker.isPdf(bytes));
    }

    @Test
    public void isDocTest() throws IOException {
        byte[] docBytes = Files.readAllBytes(Paths.get("src/test/resources/doc.doc"));
        byte[] zipBytes = Files.readAllBytes(Paths.get("src/test/resources/zip.zip"));

        assertTrue(DocumentFileChecker.isDoc(docBytes));
        assertFalse(DocumentFileChecker.isDoc(zipBytes));
    }

}
