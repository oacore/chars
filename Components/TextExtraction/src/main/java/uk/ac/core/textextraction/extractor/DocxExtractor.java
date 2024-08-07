package uk.ac.core.textextraction.extractor;

import org.apache.commons.compress.utils.InputStreamStatistics;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import uk.ac.core.textextraction.model.ExtractedText;
import uk.ac.core.textextraction.model.WordExtractedText;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class DocxExtractor extends TextExtractor {

    private final XWPFWordExtractor docxExtractor;
    private final XWPFDocument document;

    public DocxExtractor(Path sourcePath) throws IOException {
        super(sourcePath);
        this.document = new XWPFDocument(new FileInputStream(sourcePath.toFile()));
        this.docxExtractor = new XWPFWordExtractor(document);
    }

    @Override
    public ExtractedText extractFullText() {
        return new WordExtractedText(docxExtractor.getText());
    }

    @Override
    public String getTextContainingTitle() {
        return getFirst3Paragraphs(document.getParagraphs());
    }

    private String getFirst3Paragraphs(List<XWPFParagraph> paragraphs) {
        return paragraphs.get(0).getText() + paragraphs.get(1).getText() + paragraphs.get(2).getText();
    }


    @Override
    public void close() throws IOException {
        docxExtractor.close();
        document.close();
    }
}