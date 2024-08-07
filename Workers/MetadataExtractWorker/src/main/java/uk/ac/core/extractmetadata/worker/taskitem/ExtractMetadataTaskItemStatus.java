/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.extractmetadata.worker.taskitem;

import uk.ac.core.common.model.task.TaskItemStatus;

/**
 *
 * @author mc26486
 */
public class ExtractMetadataTaskItemStatus extends TaskItemStatus {

    protected Float percentage;

    protected Long totalNumBytesRead;

    protected Long maxNumBytes;

    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }

    public Long getTotalNumBytesRead() {
        return totalNumBytesRead;
    }

    public void setTotalNumBytesRead(Long totalNumBytesRead) {
        this.totalNumBytesRead = totalNumBytesRead;
    }

    public Long getMaxNumBytes() {
        return maxNumBytes;
    }

    public void setMaxNumBytes(Long maxNumBytes) {
        this.maxNumBytes = maxNumBytes;
    }
}
