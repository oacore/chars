package uk.ac.core.extractmetadata.worker.taskitem;

/**
 * @author Giorgio Basile
 * @since 21/04/2017
 */
public class FsExtractMetadataTaskItemStatus extends ExtractMetadataTaskItemStatus {

    protected Integer totalFolders = 0;
    protected Integer currentFolder = 0;
    protected Integer totalFolderFiles = 0;
    protected Integer currentFolderFile = 0;

    public Integer getTotalFolders() {
        return totalFolders;
    }

    public void setTotalFolders(Integer totalFolders) {
        this.totalFolders = totalFolders;
    }

    public Integer getCurrentFolder() {
        return currentFolder;
    }

    public void setCurrentFolder(Integer currentFolder) {
        this.currentFolder = currentFolder;
    }

    public Integer getTotalFolderFiles() {
        return totalFolderFiles;
    }

    public void setTotalFolderFiles(Integer totalFolderFiles) {
        this.totalFolderFiles = totalFolderFiles;
    }

    public Integer getCurrentFolderFile() {
        return currentFolderFile;
    }

    public void setCurrentFolderFile(Integer currentFolderFile) {
        this.currentFolderFile = currentFolderFile;
    }

    public void incCurrentFolderFile(){
        this.currentFolderFile++;
    }

    public void incCurrentFolder(){
        this.currentFolder++;
    }
}
