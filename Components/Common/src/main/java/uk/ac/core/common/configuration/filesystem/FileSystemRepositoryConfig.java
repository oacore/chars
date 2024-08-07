package uk.ac.core.common.configuration.filesystem;

/**
 * @author Giorgio Basile
 * @since 24/04/2017
 */
public class FileSystemRepositoryConfig {

    private String folder;
    private FileSystemDocumentType type;
    private Boolean find_pdf;
    private FileSystemPdfDiscovererType discoverer;
    private String metadata_ext;

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public FileSystemDocumentType getType() {
        return type;
    }

    public void setType(FileSystemDocumentType type) {
        this.type = type;
    }

    public Boolean getFind_pdf() {
        return find_pdf;
    }

    public void setFind_pdf(Boolean find_pdf) {
        this.find_pdf = find_pdf;
    }

    public FileSystemPdfDiscovererType getDiscoverer() {
        return discoverer;
    }

    public void setDiscoverer(FileSystemPdfDiscovererType discoverer) {
        this.discoverer = discoverer;
    }

    public String getMetadata_ext() {
        return metadata_ext;
    }

    public void setMetadata_ext(String metadata_ext) {
        this.metadata_ext = metadata_ext;
    }

}
