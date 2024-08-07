package uk.ac.core.filesystem.confiuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author lucasanastasiou
 */
@Configuration
@PropertySource("file:/data/core-properties/filesystem-${spring.profiles.active}.properties")
public class FilesystemConfiguration {

    @Value("${BASE_PATH}")
    public String BASE_STORAGE_PATH;
    @Value("${PDF_DIR}")
    public String DOCUMENT_STORAGE_PATH;
    @Value("${PREVIEW_DIR}")
    public String PREVIEW_STORAGE_PATH;
    @Value("${TEXT_DIR}")
    public String TEXT_STORAGE_PATH;
    @Value("${GROBID_DIR}")
    public String GROBID_STORAGE_PATH;
    @Value("${METADATA_DIR}")
    public String METADATA_STORAGE_PATH;
    @Value("${OPENAIRE_EXPORT_DIR}")
    public String OPENAIRE_EXPORT_STORAGE_PATH;
    @Value("${GROBID_EXTRACTED_IMAGES_PATH}")
    public String GROBID_EXTRACTED_IMAGES_PATH;
    @Value("${METADATA_PAGE_DOWNLOAD_DIR}")
    public String METADATA_PAGE_DOWNLOAD_DIR;
}
