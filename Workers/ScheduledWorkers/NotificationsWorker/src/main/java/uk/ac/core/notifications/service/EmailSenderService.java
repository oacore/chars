package uk.ac.core.notifications.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static uk.ac.core.notifications.config.NotificationsWorkerConfiguration.GROUPS_EMAIL;

@Service
public class EmailSenderService {
    private static final Logger log = LoggerFactory.getLogger(EmailSenderService.class);

    private final JavaMailSender mailSender;

    @Autowired
    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String from, String to, String subject, String body) {
        try {
            log.info("Sending email to {} ...", to);

            MimeMessage mimeMessage = this.mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            if (from != null) {
                helper.setFrom(from);
            }
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            // add logos on the email's header and footer
            helper.addInline("logoHeader", new ClassPathResource("core-logo-big.png"));
            helper.addInline("logoFooter", new ClassPathResource("core-logo.png"));

            // DO NOT SENT EMAIL TO THE GROUP TWICE
            if (!GROUPS_EMAIL.equals(to)) {
                helper.addBcc(GROUPS_EMAIL);
            }

            this.mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Exception while sending an email", e);
            throw new RuntimeException(e);
        }
    }
}
