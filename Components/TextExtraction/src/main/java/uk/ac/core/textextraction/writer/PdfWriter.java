package uk.ac.core.textextraction.writer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public final class PdfWriter implements DocWriter {

    private final PDDocument pdf;

    public PdfWriter(PDDocument pdf) {
        this.pdf = pdf;
    }

    @Override
    public void writeToPath(Path path) throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            new PDFTextStripper().writeText(pdf, writer);
        }
    }

    @Override
    public void close() throws IOException {
        pdf.close();
    }
}