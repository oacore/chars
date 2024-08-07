package uk.ac.core.baseimport.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regex enum for different kinds of searched values in BASE.
 */
enum BaseRepositoryField {
    BASE_ID("\\?q=(.+?)\n"),
    URL("URL:(.+?)\n"),
    NAME("^(.+?)\n"),
    NUMBER_OF_DOCUMENTS("Number of documents:(.+?)\n"),
    OPEN_ACCESS("Open Access: (\\d+) "),
    SYSTEM("System:(.+?)\n"),
    BASE_URL("BASE URL:(.+?)\n"),
    LATITUDE("Latitude/Longitude:(.+?)/(.+?)\n"),
    LONGITUDE("Latitude/Longitude:(.+?)/(.+?)\n"),
    IN_BASE_SINCE("In BASE since:(.+?)\n");

    private String regex;

    private static final String unknownValue = "unbekannt";

    BaseRepositoryField(String regex) {
        this.regex = regex;
    }

    public Optional<String> getFromRow(String row) {
        Pattern pattern = Pattern.compile(getSearchRegex());
        Matcher matcher = pattern.matcher(row);
        if (matcher.find()) {
            String fieldValue = matcher.group(1).trim();
            if (fieldValue.equals(unknownValue)) {
                return Optional.empty();
            }
            if (this.equals(BaseRepositoryField.LONGITUDE)) {
                fieldValue = matcher.group(2).trim();
            }
            return Optional.of(fieldValue);
        } else {
            return Optional.empty();
        }
    }

    private String getSearchRegex() {
        return this.regex;
    }
}