package uk.ac.core.textextraction;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TestServiceFactory {

    private static final Path testResourcesDir = Paths.get("test-resources");
    
    public static TextExtractorService newInstance(String fileName) throws IOException {
        return new TextExtractorService(Paths.get(testResourcesDir.toString(), fileName));
    }
}