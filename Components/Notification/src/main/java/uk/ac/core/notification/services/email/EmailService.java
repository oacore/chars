package uk.ac.core.notification.services.email;

import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class EmailService {

    private static Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private MailContentBuilder mailContentBuilder;

    public void sendSimpleMessage(final Mail mail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(mail.getSubject());
        message.setText(mail.getContent());
        message.setTo(mail.getTo());
        message.setFrom(mail.getFrom());
        try {
            emailSender.send(message);
        } catch (MailException e) {
            log.error("Cannot send email", e);
        }
    }

    public void sendHtmlMessage(final Mail htmlMail) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            messageHelper.setFrom(htmlMail.getFrom());
            messageHelper.setTo(htmlMail.getTo());
            messageHelper.setSubject(htmlMail.getSubject());

            String content = mailContentBuilder.build(htmlMail.getHtmlTemplateName(), htmlMail.getVariables());
            messageHelper.setText(content, true);
            //inline images needs to go after setting text
            for (String imageKey : htmlMail.getInlineImages().keySet()) {
                messageHelper.addInline(imageKey, htmlMail.getInlineImages().get(imageKey));
            }
        };
        try {
            emailSender.send(messagePreparator);
        } catch (MailException e) {
            log.error("Cannot send email", e);
        }
    }

}
