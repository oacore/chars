package uk.ac.core.rioxxvalidation.rioxx.jaxb_v3.entity;

import uk.ac.core.common.model.task.TaskItemStatus;

import java.util.concurrent.atomic.AtomicInteger;

public class RioxxComplianceTaskItemStatusV3 extends TaskItemStatus {

    private final AtomicInteger validRecordsRequiredData = new AtomicInteger(0);
    private final AtomicInteger validRecordsOptionalData = new AtomicInteger(0);

    public AtomicInteger getValidRecordsRequiredData() {
        return validRecordsRequiredData;
    }

    public AtomicInteger getValidRecordsOptionalData() {
        return validRecordsOptionalData;
    }
}
