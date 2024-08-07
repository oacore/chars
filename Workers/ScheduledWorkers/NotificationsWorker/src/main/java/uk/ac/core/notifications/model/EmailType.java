package uk.ac.core.notifications.model;

public enum EmailType {
    HARVEST_COMPLETED(
            "harvest-completed-email-template.html", "harvest-completed"),
    DEDUPLICATION_COMPLETED(
            "deduplication-completed-email-template.html", "deduplication-completed");

    private final String templateName;
    private final String dbName;

    EmailType(String templateName, String dbName) {
        this.templateName = templateName;
        this.dbName = dbName;
    }

    public static EmailType fromDbName(String dbName) {
        for (EmailType emailType: values()) {
            if (emailType.getDbName().equals(dbName)) {
                return emailType;
            }
        }
        return null;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getDbName() {
        return dbName;
    }
}
