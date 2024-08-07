package uk.ac.core.textextraction.model;

import org.apache.pdfbox.pdmodel.PDDocument;
import uk.ac.core.textextraction.writer.DocWriter;
import uk.ac.core.textextraction.writer.PdfWriter;

public final class PdfExtractedText implements ExtractedText {

    private final PdfWriter pdfWriter;

    public PdfExtractedText(PDDocument pdDocument) {
        this.pdfWriter = new PdfWriter(pdDocument);
    }

    @Override
    public DocWriter getWriter() {
        return pdfWriter;
    }
}