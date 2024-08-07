package uk.ac.core.textextraction.extractor;

import uk.ac.core.textextraction.exceptions.TextExtractionErrorCodes;
import uk.ac.core.textextraction.exceptions.TextExtractionException;
import uk.ac.core.textextraction.model.ExtractedText;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

public abstract class TextExtractor implements Closeable {

    private final Path sourcePath;

    public TextExtractor(Path sourcePath) throws TextExtractionException {
        if (!sourcePath.toFile().exists()) {
            throw new TextExtractionException("Source file does not exist",
                    TextExtractionErrorCodes.SOURCE_NOT_FOUND);
        }
        this.sourcePath = sourcePath;
    }

    public abstract ExtractedText extractFullText() throws IOException;

    public abstract String getTextContainingTitle() throws IOException;

    public Path getSourcePath() {
        return sourcePath;
    }
}