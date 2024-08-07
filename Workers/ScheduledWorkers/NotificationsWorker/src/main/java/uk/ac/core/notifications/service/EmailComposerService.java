package uk.ac.core.notifications.service;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import uk.ac.core.dataprovider.logic.dto.DataProviderBO;
import uk.ac.core.dataprovider.logic.exception.DataProviderNotFoundException;
import uk.ac.core.dataprovider.logic.service.origin.DataProviderService;
import uk.ac.core.notifications.database.NotificationEventDAO;
import uk.ac.core.notifications.exceptions.NoDataForEmailException;
import uk.ac.core.notifications.model.BaseEmailData;
import uk.ac.core.notifications.model.EmailType;
import uk.ac.core.notifications.model.NotificationEvent;
import uk.ac.core.notifications.model.UserNotificationProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class EmailComposerService {
    private static final Logger log = LoggerFactory.getLogger(EmailComposerService.class);

    private final NotificationEventDAO notificationEventDAO;
    private final DataProviderService dataProviderService;
    private final EmailApiService apiService;

    @Autowired
    public EmailComposerService(
            NotificationEventDAO notificationEventDAO,
            DataProviderService dataProviderService,
            EmailApiService apiService) {
        this.notificationEventDAO = notificationEventDAO;
        this.dataProviderService = dataProviderService;
        this.apiService = apiService;
    }

    public String composeEmail(final UserNotificationProperties properties) throws NoDataForEmailException {
        long start = System.currentTimeMillis(), end;
        log.info("Start composing HTML-based email");
        NotificationEvent ne;
        try {
            ne = this.notificationEventDAO.getLatestNotificationEvent(properties)
                    .orElseThrow(() -> new RuntimeException(
                            "Unable to find latest notification event for organisation with ID " + properties.getOrgId()));
        } catch (EmptyResultDataAccessException exc) {
            log.error("No notification events for organisation (ID: {}) were found", properties.getOrgId());
            throw new NoDataForEmailException(
                    String.format("No notification events for organisation (ID: %d) were found", properties.getOrgId()));
        }

        DataProviderBO dataProvider;
        try {
            dataProvider = this.dataProviderService.findById(ne.getRepositoryId());
        } catch (DataProviderNotFoundException e) {
            throw new RuntimeException(e);
        }

        EmailType emailType = EmailType.fromDbName(ne.getType());
        if (emailType == null) {
            throw new IllegalArgumentException(
                    String.format(
                            "Unable to find relevant template for notification type '%s'", ne.getType()));
        }

        Map<String, Object> stats = this.getStatistics(ne, emailType, dataProvider);

        String populatedEmail = this.populateEmailTemplate(stats, emailType);

        end = System.currentTimeMillis();
        log.info("Finished composing email, took {} ms", end - start);

        return populatedEmail;
    }

    private String populateEmailTemplate(Map<String, Object> dict, EmailType emailType) {
        try {
            long start = System.currentTimeMillis(), end;
            log.info("Reading HTML template from classpath ...");

            String templateName = emailType.getTemplateName();
            InputStream is = new ClassPathResource(templateName).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String html = new String(IOUtils.toByteArray(is));

            reader.close();

            log.info("Done");
            log.info("Populating email template ...");

            for (Map.Entry<String, Object> prop : dict.entrySet()) {
                html = html.replace("${" + prop.getKey() + "}", prop.getValue().toString());
            }

            end = System.currentTimeMillis();
            log.info("Done, took {} ms", end - start);

            return html;
        } catch (IOException e) {
            log.error("Exception occurred while reading email template from classpath", e);
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> getStatistics(
            NotificationEvent latestEvent, EmailType emailType, DataProviderBO dataProvider)
            throws NoDataForEmailException {
        log.info("Getting stats numbers for populated email ...");
        BaseEmailData emailData = null;
        switch (emailType) {
            // the statement to be extended with more notifications types
            case HARVEST_COMPLETED: {
                emailData = this.apiService.getHarvestingData(latestEvent.getRepositoryId());
                break;
            }
            case DEDUPLICATION_COMPLETED: {
                emailData = this.apiService.getDuplicatesData(latestEvent.getRepositoryId());
                break;
            }
        }
        return emailData == null
                ? new HashMap<>()
                : emailData.setRepoName(dataProvider.getName()).toMap();
    }
}
