package uk.ac.core.textextraction.model;

import uk.ac.core.textextraction.writer.DefaultWriter;
import uk.ac.core.textextraction.writer.DocWriter;

public final class WordExtractedText implements ExtractedText {

    private final String contents;

    public WordExtractedText(String contents) {
        this.contents = contents;
    }

    @Override
    public DocWriter getWriter() {
        return new DefaultWriter(contents);
    }

}
