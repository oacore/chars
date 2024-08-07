package uk.ac.core.textextraction.extractor;

import org.apache.poi.hwpf.extractor.WordExtractor;
import uk.ac.core.textextraction.model.ExtractedText;
import uk.ac.core.textextraction.model.WordExtractedText;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public final class DocExtractor extends TextExtractor {

    private final WordExtractor docExtractor;

    public DocExtractor(Path sourcePath) throws IOException {
        super(sourcePath);
        this.docExtractor = new WordExtractor(new FileInputStream(sourcePath.toFile()));
    }

    @Override
    public ExtractedText extractFullText() {
        return new WordExtractedText(docExtractor.getText());
    }

    @Override
    public String getTextContainingTitle() {
       return getFirst3Paragraphs();
    }

    private String getFirst3Paragraphs() {
        String[] paragraphs = docExtractor.getParagraphText();

        return paragraphs[0] + paragraphs[1] + paragraphs[2];
    }

    @Override
    public void close() throws IOException {
        docExtractor.close();
    }
}
