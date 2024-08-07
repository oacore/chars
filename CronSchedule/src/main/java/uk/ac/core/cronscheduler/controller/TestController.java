package uk.ac.core.cronscheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.notifications.exceptions.NoDataForEmailException;
import uk.ac.core.notifications.model.UserNotificationProperties;
import uk.ac.core.notifications.service.EmailComposerService;
import uk.ac.core.notifications.service.EmailSenderService;

import java.time.LocalDateTime;

@RestController
public class TestController {
    private static final int CORE_ORG_ID = 1010;

    private final EmailComposerService composerService;
    private final EmailSenderService senderService;

    @Autowired
    public TestController(EmailComposerService composerService, EmailSenderService senderService) {
        this.composerService = composerService;
        this.senderService = senderService;
    }

    @GetMapping("/notifications/test-html/{type}")
    public String testEmail(@PathVariable("type") String type) throws NoDataForEmailException {
        UserNotificationProperties properties = new UserNotificationProperties();
        properties.setLastEmailSent(LocalDateTime.now().minusDays(10));
        properties.setOrgId(CORE_ORG_ID);
        properties.setType(type);
        return this.composerService.composeEmail(properties);
    }

    @GetMapping("/notifications/test-email-sender")
    public String testEmailSender(
            @RequestParam(value = "type") String type,
            @RequestParam(value = "email_from") String emailFrom,
            @RequestParam(value = "email_to") String emailTo) throws NoDataForEmailException {
        UserNotificationProperties properties = new UserNotificationProperties();
        properties.setOrgId(CORE_ORG_ID);
        properties.setType(type);
        properties.setLastEmailSent(LocalDateTime.now().minusDays(10));
        String html = this.composerService.composeEmail(properties);

        String subject = "TEST EMAIL NOTIFICATION (IGNORE IT)";
        this.senderService.sendEmail(emailFrom, emailTo, subject, html);

        return "done";
    }
}
