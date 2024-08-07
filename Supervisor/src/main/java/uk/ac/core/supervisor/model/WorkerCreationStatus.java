package uk.ac.core.supervisor.model;

/**
 *
 * @author lucasanastasiou
 */
public class WorkerCreationStatus {

    private boolean success;
    private Integer pid;
    private Integer duration;
    private String message;

    public WorkerCreationStatus(){
        
    }
    public WorkerCreationStatus(boolean success, Integer pid, Integer duration, String message) {
        this.success = success;
        this.pid = pid;
        this.duration = duration;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
}
