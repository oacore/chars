package uk.ac.core.textextraction.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public final class DefaultWriter implements DocWriter {

    private final String contents;
    private BufferedWriter writer;

    public DefaultWriter(String contents) {
        this.contents = contents;
    }

    @Override
    public void writeToPath(Path path) throws IOException {
        writer = new BufferedWriter(new FileWriter(path.toFile()));
        writer.write(contents);
    }

    @Override
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }
}
