package uk.ac.core.notifications.exceptions;

import uk.ac.core.notifications.model.EmailType;

public class NoDataForEmailException extends Exception {

    public NoDataForEmailException(EmailType emailType) {
        super(String.format("Found only zero numbers for email type `%s`", emailType.getDbName()));
    }

    public NoDataForEmailException(String message) {
        super(message);
    }
}
