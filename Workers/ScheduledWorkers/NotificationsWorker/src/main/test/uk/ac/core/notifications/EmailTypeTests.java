package uk.ac.core.notifications;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.ac.core.notifications.model.EmailType;

public class EmailTypeTests {
    private static final String[] NOTIFICATION_TYPES = {
            "harvest-completed",
            "deduplication-completed"
    };

    @Test
    public void testEmailTypes() {
        for (String type: NOTIFICATION_TYPES) {
            EmailType emailType = EmailType.fromDbName(type);
            Assertions.assertNotNull(emailType, String.format(
                    "Failed to convert DB name '%s' to email type enum object", type));
        }
    }
}
