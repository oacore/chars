package uk.ac.core.textextraction.model;

import uk.ac.core.textextraction.writer.DocWriter;
import java.io.IOException;

public interface ExtractedText {

    DocWriter getWriter() throws IOException;
}