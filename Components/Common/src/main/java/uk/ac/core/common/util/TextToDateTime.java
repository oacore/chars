/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.common.util;

import org.slf4j.LoggerFactory;

import java.text.ParsePosition;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Accepts a String Input and converts it
 * It doesn't support parsing for ZonedTimedData yet. The time zone information is discarded from the parsing.
 * @author samuel
 */
public class TextToDateTime {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(TextToDateTime.class);

    private final DateTimeFormatter dateTimeFormatter;
    private Pattern openaireDateParser = Pattern.compile("info:eu-repo/date/[A-Za-z]+/([0-9-]+)");



    private String dateAsText;

    // todo support new ParsePosition(5)
    public TextToDateTime(String dateAsText) {
        this.dateAsText = dateAsText;

        Matcher localeMatch = openaireDateParser.matcher(dateAsText);

        if (localeMatch.find()) {
            this.dateAsText = localeMatch.group(1);
        }

        this.dateTimeFormatter = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss[.SSSSSS]"))
                .appendOptional(DateTimeFormatter.ofPattern("d/M/yyyy HH:mm:ss[.SSSSSS]"))
                .appendOptional(DateTimeFormatter.ofPattern("dd/M/yyyy HH:mm:ss[.SSSSSS]"))
                .appendOptional(DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm:ss[.SSSSSS]"))
                .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss[.SSSSSS]"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSS"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSS]"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
                .appendOptional(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("ddMMMyyyy:HH:mm:ss.SSS[ Z]"))
                .appendOptional(DateTimeFormatter.ofPattern("ddMMMyyyy:HH:mm:ss.SSS Z"))
                .appendOptional(DateTimeFormatter.ofPattern("ddMMMyyyy HH:mm:ss[.SSS]"))
                .appendOptional(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm[:ss]"))
                .appendOptional(DateTimeFormatter.ofPattern("d MMM yyyy HH:mm[:ss]"))
                .appendOptional(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("d MMM yyyy HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:[mm:ss]"))
                .appendOptional(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("MMMM d, yyyy HH:mm:ss"))
                .appendOptional(DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm:ss"))
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter().withLocale(Locale.ENGLISH).withResolverStyle(ResolverStyle.LENIENT);
    }

    /**
     * Returns the Text as a LocalDateTime.
     * 
     * A String detected as a date only will have the time of 00:00:00
     *
     * @return
     */
    public LocalDateTime asLocalDateTime() {
        return LocalDateTime.from(dateTimeFormatter.parse(
                appendNecessaryData(this.dateAsText), new ParsePosition(0)));
    }

    public String asIsoString() {
        return asLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    public Date asUtilDate() {
        return Date.from(this.asLocalDateTime()
                .toInstant(ZoneOffset.UTC));
    }

    private String appendNecessaryData(String raw) {
        String result = raw;
        if(raw.length() == 4) {
            result = result + "-01-01";
        }
        if(raw.length() == 7 && raw.indexOf("-") == 4) {
            result = result + "-01";
        }
        if(raw.length() == 6 && raw.indexOf("-") == 4) {
            result = result + "-1";
        }
        if (!raw.contains(":")) {
            result = result + " 00:00:00";
        }

        return result;
    }
}
