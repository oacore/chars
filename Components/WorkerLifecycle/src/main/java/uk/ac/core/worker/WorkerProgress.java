package uk.ac.core.worker;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author lucasanastasiou
 */
@Component
public class WorkerProgress {

    private Integer total;
    private Integer processedCount =0 ;
    /* unix timestamp */
    private Long timeStarted;
    private List<Long> durations = new ArrayList<>();

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getProcessedCount() {
        return processedCount;
    }

    public void setProcessedCount(Integer processedCount) {
        this.processedCount = processedCount;
    }

    public Long getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(Long timeStarted) {
        this.timeStarted = timeStarted;
    }
    public void incProcessedCount(){
        this.processedCount++;
    }

    public void addItemDurationTime(long itemDuration) {
        this.durations.add(itemDuration);
    }
}
