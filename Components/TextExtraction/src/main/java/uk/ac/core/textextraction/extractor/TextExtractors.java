package uk.ac.core.textextraction.extractor;

import java.io.IOException;
import java.nio.file.Path;

public final class TextExtractors {

    public static TextExtractor get(Path path) throws IOException {
        String stringPath = path.toString();

        if (stringPath.endsWith("pdf")) {
            return getByType(path, TextExtractorType.PDF);
        }
        if (stringPath.endsWith("doc")) {
            return getByType(path, TextExtractorType.DOC);
        }
        if (stringPath.endsWith("docx")) {
            return getByType(path, TextExtractorType.DOCX);
        } else {
            throw new IllegalArgumentException("Path doesn't contain a supported file.");
        }
    }

    public static TextExtractor getByType(Path path, TextExtractorType type) throws IOException {
        TextExtractor extractor;
        switch (type) {
            case PDF:
                extractor = new PdfExtractor(path);
                break;
            case DOC:
                extractor = new DocExtractor(path);
                break;
            case DOCX:
                extractor = new DocxExtractor(path);
                break;
            default:
                throw new IllegalArgumentException("Path doesn't contain a supported file.");
        }
        return extractor;
    }
}