/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.metadatadownloadworker.worker.taskStatus;

import java.util.ArrayList;
import java.util.List;
import uk.ac.core.common.model.task.TaskItemStatus;

/**
 *
 * @author mc26486
 */
public class DownloadMetadataTaskItemStatus extends TaskItemStatus {

    private List<String> oaiPMHIssueDescriptions = new ArrayList<>();

    public void addOAIPMHIssue(String oaiPmhIssueDescription) {
        oaiPMHIssueDescriptions.add(oaiPmhIssueDescription);
    }

    public List<String> getOaiPMHIssueDescriptions() {
        return oaiPMHIssueDescriptions;
    }

    public void setOaiPMHIssueDescriptions(List<String> oaiPMHIssueDescriptions) {
        this.oaiPMHIssueDescriptions = oaiPMHIssueDescriptions;
    }

}
