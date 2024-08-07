/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.reporting.worker;

import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.reporting.metrics.service.dto.CompleteGlobalMetricsBO;
import java.util.Optional;

class ReportingTaskItem implements TaskItem {

    private CompleteGlobalMetricsBO currentCompleteGlobalMetricsBO;
    private CompleteGlobalMetricsBO lastGlobalMetricBO;

    CompleteGlobalMetricsBO getCurrentCompleteGlobalMetricsBO() {
        return currentCompleteGlobalMetricsBO;
    }

    void setCurrentCompleteGlobalMetricsBO(CompleteGlobalMetricsBO currentGlobalMetricEntity) {
        this.currentCompleteGlobalMetricsBO = currentGlobalMetricEntity;
    }

    Optional<CompleteGlobalMetricsBO> getLastGlobalMetricsBO() {
        return lastGlobalMetricBO == null ? Optional.empty() : Optional.of(lastGlobalMetricBO);
    }

    void setLastGlobalMetricsBO(CompleteGlobalMetricsBO lastGlobalMetricEntity) {
        this.lastGlobalMetricBO = lastGlobalMetricEntity;
    }

}
