package uk.ac.core.extractmetadata.periodic.crossref.service;

import org.xml.sax.SAXException;
import uk.ac.core.common.model.task.TaskItemStatus;

import java.io.File;

public interface CrossrefReharvestService {
    void initPersist(TaskItemStatus taskItemStatus);
    File getRawMetadataFile();
    void parseMetadata(File tmpFile) throws SAXException;
    boolean deleteTmpFile(File tmpFile);
    void flushRecordsToDatabase();
    void scheduleReindex();
    int processedCount();
}
