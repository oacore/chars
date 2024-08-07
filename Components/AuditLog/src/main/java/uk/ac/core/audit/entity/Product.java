package uk.ac.core.audit.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Product {
    DASHBOARD("DASHBOARD"),
    APPLE("APPLE");

    private final String value;

    Product(final String description) {
        this.value = description;
    }

    public static Product fromValue(String value) {
        if (value != null) {
            for (Product color : values()) {
                if (color.value.equals(value)) {
                    return color;
                }
            }
        }
        throw new IllegalArgumentException("Invalid Product: " + value);
    }

    @JsonValue
    final String value() {
        return this.value;
    }
}