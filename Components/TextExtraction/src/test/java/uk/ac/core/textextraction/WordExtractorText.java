package uk.ac.core.textextraction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Path;
import static uk.ac.core.textextraction.TestServiceFactory.newInstance;

public final class WordExtractorText {

    @TempDir
    static Path tempDir;

    private static final String DOC_DOCUMENT_PATH = "word/doc-sample.doc";
    private static final String DOCX_DOCUMENT_PATH = "word/docx-sample.docx";

//    @Test
    public void shouldExtractDocCorrectly() throws IOException {
        newInstance(DOC_DOCUMENT_PATH).extractTextFromDocumentTo(tempDir.resolve("output").toString());
    }

//    @Test
    public void shouldExtractDocxCorrectly() throws IOException {
        newInstance(DOCX_DOCUMENT_PATH).extractTextFromDocumentTo(tempDir.resolve("output").toString());
    }
}