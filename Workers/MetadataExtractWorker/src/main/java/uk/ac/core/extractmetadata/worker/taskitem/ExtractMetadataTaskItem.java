/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.extractmetadata.worker.taskitem;

import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.extractmetadata.worker.extractMetadataService.ExtractMetadataService;

/**
 *
 * @author mc26486
 */
public class ExtractMetadataTaskItem implements TaskItem {

    private ExtractMetadataService extractor;

    public ExtractMetadataService getExtractor() {
        return extractor;
    }

    public void setExtractor(ExtractMetadataService extractor) {
        this.extractor = extractor;
    }
}
