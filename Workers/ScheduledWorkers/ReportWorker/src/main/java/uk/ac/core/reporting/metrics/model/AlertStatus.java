package uk.ac.core.reporting.metrics.model;

public final class AlertStatus {
    private String message;
    private AlertLevel alertLevel;

    public AlertStatus(String message, AlertLevel alertLevel) {
        this.message = message;
        this.alertLevel = alertLevel;
    }

    public String getMessage() {
        return message;
    }

    public AlertLevel getAlertLevel() {
        return alertLevel;
    }
}
