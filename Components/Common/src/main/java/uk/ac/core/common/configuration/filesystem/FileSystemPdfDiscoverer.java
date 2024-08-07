package uk.ac.core.common.configuration.filesystem;

import java.nio.file.Path;


/**
 * @author Giorgio Basile
 * @since 25/04/2017
 */
public interface FileSystemPdfDiscoverer {

    Path getPdfFsPath(Path metadataPath, String metadataExt);

}
