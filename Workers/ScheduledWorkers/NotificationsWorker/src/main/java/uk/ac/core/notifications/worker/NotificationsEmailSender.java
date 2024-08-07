package uk.ac.core.notifications.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.core.notifications.database.CoreUsersDAO;
import uk.ac.core.notifications.database.UserNotificationPropertiesDAO;
import uk.ac.core.notifications.exceptions.NoDataForEmailException;
import uk.ac.core.notifications.model.UserNotificationProperties;
import uk.ac.core.notifications.service.EmailComposerService;
import uk.ac.core.notifications.service.EmailSenderService;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static uk.ac.core.notifications.config.NotificationsWorkerConfiguration.SENDER_EMAIL;

@Component
public class NotificationsEmailSender {
    private static final Logger log = LoggerFactory.getLogger(NotificationsEmailSender.class);

    private final EmailComposerService composerService;
    private final EmailSenderService senderService;
    private final UserNotificationPropertiesDAO notificationPropertiesDAO;
    private final CoreUsersDAO usersDAO;

    @Autowired
    public NotificationsEmailSender(
            EmailComposerService composerService,
            EmailSenderService senderService,
            UserNotificationPropertiesDAO notificationPropertiesDAO,
            CoreUsersDAO usersDAO) {
        this.composerService = composerService;
        this.senderService = senderService;
        this.notificationPropertiesDAO = notificationPropertiesDAO;
        this.usersDAO = usersDAO;
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void sendNotificationsEmails() {
        List<UserNotificationProperties> properties = this.notificationPropertiesDAO.getUsersWaitingForEmail();
        log.info("Found {} user notification settings in DB", properties.size());
        for (UserNotificationProperties props : properties) {
            log.info("User ID: {}", props.getUserId());

            if (props.getLastEmailSent() == null) {
                log.info("First time sending notification email!");
                this.prepareAndSend(props);
            } else {
                Period period = Period.ZERO;
                if (!props.getInterval().equalsIgnoreCase("every time")) {
                    period = this.getPeriodFromIntervalString(props.getInterval());
                } else {
                    log.info("Period is set to `every time` !");
                }
                LocalDate lastEmailSent = props.getLastEmailSent().toLocalDate();
                LocalDate nextEmailDate = lastEmailSent.plus(period);
                LocalDate now = LocalDate.now();

                log.info("Last email sent on: {}", lastEmailSent);
                log.info("Next email to be sent on: {}", nextEmailDate);

                if (now.isEqual(nextEmailDate) || now.isAfter(nextEmailDate)) {
                    this.prepareAndSend(props);
                } else {
                    log.info("Notification email was already sent within specified interval");
                }
            }
        }
        log.info("Finished processing user notification properties");
    }

    private void prepareAndSend(UserNotificationProperties props) {
        try {
            // prepare email
            String userEmail = this.usersDAO.findEmail(props.getUserId());
            String emailBody = this.composerService.composeEmail(props);

            String subject = "A new CORE event is available for your repository";

            if (userEmail == null) {
                throw new RuntimeException(String.format("Email address for user ID %d not found", props.getUserId()));
            }

            log.info("Sending an email to {} ...", userEmail);
            this.senderService.sendEmail(SENDER_EMAIL, userEmail, subject, emailBody);

            log.info("Updating last email sent field in DB ...");
            this.notificationPropertiesDAO.updateLastEmailSent(props);

            log.info("Done");
        } catch (NoDataForEmailException e) {
//            log.info("Sending no data email to the CORE dev group");
//            String body = this.noDataEmailBody(props);
//            String subject = "[DEV GROUP ONLY] No data found for email";
//            this.senderService.sendEmail(SENDER_EMAIL, DEV_EMAIL, subject, body);
            log.warn("No data found for sending an email: type={}, ordId={}", props.getType(), props.getOrgId());
        } catch (Exception e) {
            log.error("Exception raised while preparing and sending one of the emails");
            log.error("Exception: {}", e.toString());
            log.error("Message: {}", e.getMessage());
            log.error("", e);
        }
    }

    private String noDataEmailBody(UserNotificationProperties props) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder();
        sb.append("<p> Found no data while sending a Dashboard notification. </p>");
        sb.append("<p> User notification properties (UNP): </p>");
        sb.append("<ul>");
        sb.append(String.format("<li>UNP ID: %d</li>", props.getId()));
        sb.append(String.format("<li>Organisation ID: %d</li>", props.getOrgId()));
        sb.append(String.format("<li>User ID: %d</li>", props.getUserId()));
        sb.append(String.format("<li>Last email sent on %s</li>", props.getLastEmailSent().format(dtf)));
        sb.append(String.format("<li>Email type: %s</li>", props.getType()));
        sb.append(String.format("<li>Interval: %s</li>", props.getInterval()));
        sb.append("</ul>");
        return sb.toString();
    }

    private Period getPeriodFromIntervalString(String intervalString) {
        if (intervalString.equalsIgnoreCase("every month")) {
            intervalString = "1 Month";
        }
        String[] interval = intervalString.split(" ");
        String periodString = "P" + interval[0] + interval[1].charAt(0);
        return Period.parse(periodString);
    }
}
