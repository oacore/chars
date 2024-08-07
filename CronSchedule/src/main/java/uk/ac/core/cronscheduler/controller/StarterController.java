package uk.ac.core.cronscheduler.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.cronscheduler.exception.NotSupportedWorkerException;
import uk.ac.core.cronscheduler.model.StartTaskResponse;
import uk.ac.core.notifications.worker.NotificationsEmailSender;
import uk.ac.core.worker.ScheduledWorker;

import java.util.List;
import java.util.Optional;

/**
 * @author mc26486
 */
@RestController
public class StarterController {

    private final List<ScheduledWorker> scheduledWorkers;

    @Autowired
    private NotificationsEmailSender emailSender;

    public StarterController(List<ScheduledWorker> scheduledWorkers) {
        this.scheduledWorkers = scheduledWorkers;
    }

    @PostMapping("/{task_type}/start")
    public ResponseEntity<StartTaskResponse> startTasks(@PathVariable(value = "task_type") TaskType taskType) throws Exception {
        findScheduledWorker(taskType).orElseThrow(NotSupportedWorkerException::new).start();
        return ResponseEntity.accepted().body(new StartTaskResponse(taskType));
    }

    private Optional<ScheduledWorker> findScheduledWorker(TaskType taskType) {
        return scheduledWorkers.stream()
                .filter(worker -> worker.getTaskType().equals(taskType))
                .findFirst();
    }

    @RequestMapping("/")
    public String startingPage() {
        return new Gson().toJson(scheduledWorkers);
    }

    @PostMapping("/email-notification")
    public ResponseEntity<StartTaskResponse> notifyEmail() throws Exception {
        ScheduledWorker scheduledWorker = findScheduledWorker(TaskType.REPORTING).orElseThrow(Exception::new);
        scheduledWorker.sendNotification("Test email - testing if email sending service works");
        return ResponseEntity.accepted().body(new StartTaskResponse(TaskType.REPORTING));
    }

    @GetMapping("/notification-emails")
    public String testFullWorkflow() {
        this.emailSender.sendNotificationsEmails();

        return "done";
    }

}