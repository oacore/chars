package uk.ac.core.textextraction.extractor;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.core.textextraction.exceptions.DocumentEncryptedException;
import uk.ac.core.textextraction.model.ExtractedText;
import uk.ac.core.textextraction.model.PdfExtractedText;
import java.io.IOException;
import java.nio.file.Path;

public final class PdfExtractor extends TextExtractor {

    private PDFTextStripper stripper;

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfExtractor.class);

    public PdfExtractor(Path path) throws IOException {
        super(path);
        try {
            this.stripper = new PDFTextStripper();
        } catch (IOException ex) {
            LOGGER.warn(ex.getMessage(), ex);
        }
    }

    @Override
    public ExtractedText extractFullText() throws IOException {

        Path sourcePath = super.getSourcePath();

        PDDocument pdf = loadPdf(sourcePath);

        if (pdf.isEncrypted()) {
            throw new DocumentEncryptedException("PDF document is Encrypted: " + sourcePath);
        }

        return new PdfExtractedText(pdf);

    }

    private PDDocument loadPdf(Path path) throws IOException {
        return PDDocument.load(path.toFile());
    }

    @Override
    public String getTextContainingTitle() throws IOException {
        int numberOfPages = 2;

        PDDocument pdf = loadPdfWithLimit(super.getSourcePath(), numberOfPages);

        if (pdf.isEncrypted()) {
            pdf.setAllSecurityToBeRemoved(true);
        }
        try {
            return stripper.getText(pdf);
        } finally {
            pdf.close();
        }

    }

    private PDDocument loadPdfWithLimit(Path path, int limit) throws IOException {
        stripper.setEndPage(limit);
        return loadPdf(path);
    }

    @Override
    public void close() throws IOException {
    }
}