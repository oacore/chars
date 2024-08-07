package uk.ac.core.worker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskType;
import java.util.ArrayList;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author mc26486
 */
public abstract class ScheduledWorker extends Worker {

    @Value("${scheduled.recipients:dev@core.ac.uk}")
    protected String recipients;

    private static final String SUBJECT_TEMPLATE = "[CHARS] %s results";

    public ScheduledWorker() {
        this.currentWorkingTask = this.generateTaskDescription();
    }

    @Override
    public void start() {
        boolean taskOverallSuccess = true;
        List<TaskItemStatus> results = new ArrayList<>();
        this.currentWorkingTask = this.generateTaskDescription();
        try {

            List<TaskItem> dataToProcess = this.collectData();
            results = this.process(dataToProcess);
            this.collectStatistics(results);
            taskOverallSuccess = this.evaluate(results, dataToProcess);

        } catch (Exception e) {
            logger.error("Task finished with an error. The error message:\n " + e.getMessage(), e);
            taskOverallSuccess = false;
        } finally {
            String mailMessage = this.generateReport(results, taskOverallSuccess);
            this.sendNotification(mailMessage);
        }
    }

    public void sendNotification(String messageBody) {

        JavaMailSenderImpl sender = configureMailSender();

        MimeMessage mimeMessage = sender.createMimeMessage();

        try {
            prepareMessage(mimeMessage, messageBody);
            sender.send(mimeMessage);
        } catch (MailException | MessagingException ex) {
            logger.error("Failed to send email", ex);
        }

    }

    // TODO: put all email related logic in separate email service
    private JavaMailSenderImpl configureMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("localhost");
        javaMailSender.setPort(25);
        return javaMailSender;
    }

    private void prepareMessage(MimeMessage mimeMessage, String body) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(this.recipients);
        helper.setText(body, true);
        helper.setSubject(String.format(SUBJECT_TEMPLATE, this.getTaskType().getName()));
    }

    @Override
    public boolean evaluate(List<TaskItemStatus> results, List<TaskItem> taskItems) {
        return results.size() > 0;
    }

    public abstract String generateReport(List<TaskItemStatus> results, boolean taskOverallSuccess);

    public abstract TaskType getTaskType();

    public abstract void scheduledStart();

    @Override
    public void stop() {
        logger.error("Scheduled worker are UNSTOPPABLE");
    }

    @Override
    public void pause() {
        logger.error("Scheduled worker are UNPAUSABLE");
    }

    @Override
    public void drop() {
        logger.error("Scheduled worker are UNDROPPABLE");
    }

    public abstract TaskDescription generateTaskDescription();

}
