package uk.ac.core.rioxxvalidation.rioxx.jaxb_v2;

import java.util.concurrent.atomic.AtomicInteger;
import uk.ac.core.common.model.task.TaskItemStatus;

/**
 *
 * @author mc26486
 */
public class RioxxComplianceTaskItemStatusV2 extends TaskItemStatus {

    private final AtomicInteger validRecordsBasic = new AtomicInteger(0);
    private final AtomicInteger validRecordsFull = new AtomicInteger(0);

    public AtomicInteger getValidRecordsBasic() {
        return validRecordsBasic;
    }

    public AtomicInteger getValidRecordsFull() {
        return validRecordsFull;
    }

}
