package uk.ac.core.common.util.datetime;

/**
 * Time pattern.
 */
public enum TimePattern {

    SIMPLE_LOCAL_TIME("HH:mm");

    private final String pattern;

    TimePattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String toString() {
        return pattern;
    }
}