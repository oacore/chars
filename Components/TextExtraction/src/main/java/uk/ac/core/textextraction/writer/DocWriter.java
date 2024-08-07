package uk.ac.core.textextraction.writer;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

public interface DocWriter extends Closeable {

    void writeToPath(Path path) throws IOException;
}