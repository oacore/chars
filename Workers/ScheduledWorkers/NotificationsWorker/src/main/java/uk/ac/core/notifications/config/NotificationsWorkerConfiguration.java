package uk.ac.core.notifications.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@ComponentScan("uk.ac.core")
public class NotificationsWorkerConfiguration {
    public static final String SENDER_EMAIL = "theteam@core.ac.uk";
    public static final String DEV_EMAIL = "dev@core.ac.uk";
    public static final String GROUPS_EMAIL = "core-notifications@groups.open.ac.uk";

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("localhost");
        javaMailSender.setPort(25);
        return javaMailSender;
    }
}
