package uk.ac.core.notification.api.impl;

import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.notification.services.email.EmailService;
import uk.ac.core.notification.services.email.Mail;
import uk.ac.core.notification.api.CannotSendEmailException;
import uk.ac.core.notification.api.NotificationService;
import uk.ac.core.notification.configuration.NotificationConfiguration;

/**
 *
 * @author lucasanastasiou
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    NotificationConfiguration notificationConfiguration;

    @Autowired
    EmailService emailService;

    String[] recipients;

    private static final Logger LOG = Logger.getLogger(NotificationServiceImpl.class.getName());

    private static final String FROM_ADDRESS = "notification@chars.core.ac.uk";

    @PostConstruct
    private void init() throws UnsupportedEncodingException {
        this.recipients = notificationConfiguration.to_list.split(",");
    }

    public void sendFinishTaskNotificationToWatchers(Integer idRepository, TaskType taskType) throws CannotSendEmailException {
        LOG.info("Sending email for #" + idRepository + ", task: " + taskType.name());

        String subject = "[CHARS Notification] Repository #" + idRepository + " finished task " + taskType.name();
        String template = "standard";

        Map<String, String> variables = Stream.of(
                new AbstractMap.SimpleImmutableEntry<>("idRepository", "" + idRepository),
                new AbstractMap.SimpleImmutableEntry<>("task", "" + taskType.name()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, Resource> inlineImages = Stream.of(
                new AbstractMap.SimpleImmutableEntry<>("corelogo_hires.png", new ClassPathResource("templates/images/corelogo_hires.png"))
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//
//        Mail htmlMail = buildMail(subject, template, variables, inlineImages);
//        emailService.sendHtmlMessage(htmlMail);

    }

    public void sendFinishHarvestingNotificationToWatchers(Integer idRepository) throws CannotSendEmailException {
        LOG.info("Sending email for #" + idRepository);

        String subject = "[CHARS Notification] Repository #" + idRepository + " finished harvesting";
        String template = "full";
        Map<String, String> modelObject = Stream.of(
                new AbstractMap.SimpleImmutableEntry<>("idRepository", "" + idRepository)
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        
        Map<String, Resource> inlineImages = Stream.of(
                new AbstractMap.SimpleImmutableEntry<>("corelogo_hires.png", new ClassPathResource("templates/images/corelogo_hires.png"))
                ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//
//        Mail htmlMail = buildMail(subject, template, modelObject, inlineImages);
//        emailService.sendHtmlMessage(htmlMail);

    }

    private Mail buildMail(String subject, String htmlTemplateName, Map<String, String> variables, Map<String, Resource> inlineImages) {
        Mail htmlMail = new Mail();
        htmlMail.setFrom(FROM_ADDRESS);
        htmlMail.setTo(recipients);
        htmlMail.setSubject(subject);
        htmlMail.setHtmlTemplateName(htmlTemplateName);
        htmlMail.setVariables(variables);
        htmlMail.setInlineImages(inlineImages);
        return htmlMail;
    }
}
