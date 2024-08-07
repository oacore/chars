package uk.ac.core.reporting.metrics.model;

import uk.ac.core.database.model.TaskUpdateStatus;

public enum AlertLevel {

    RED("red"),
    AMBER("orange"),
    GREEN("green"),
    ;
    private final String color;

    AlertLevel(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return color;
    }
}
