package uk.ac.core.textextraction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.core.textextraction.extractor.TextExtractor;
import uk.ac.core.textextraction.extractor.TextExtractorType;
import uk.ac.core.textextraction.extractor.TextExtractors;
import uk.ac.core.textextraction.model.ExtractedText;
import uk.ac.core.textextraction.writer.DocWriter;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author lucasanastasiou
 */
public final class TextExtractorService implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextExtractorService.class);

    private final TextExtractor textExtractor;

    public TextExtractorService(Path docPath) throws IOException {
        this.textExtractor = TextExtractors.get(docPath);
    }

    public TextExtractorService(Path docPath, TextExtractorType textExtractorType) throws IOException {
        this.textExtractor = TextExtractors.getByType(docPath, textExtractorType);
    }

    public void extractTextFromDocumentTo(String textLocation)
            throws IOException {

        LOGGER.info("Extracting text from : " + textExtractor.getSourcePath());

        ExtractedText extractedText = textExtractor.extractFullText();

        try (DocWriter docWriter = extractedText.getWriter()) {
            docWriter.writeToPath(Paths.get(textLocation));
        }

        LOGGER.info("Text saved in:" + textLocation);
    }

    public String getTextContainingTitle() throws IOException {
        return textExtractor.getTextContainingTitle();
    }

    public Path getPath() {
        return this.textExtractor.getSourcePath();
    }

    @Override
    public void close() throws IOException {
        textExtractor.close();
    }
}