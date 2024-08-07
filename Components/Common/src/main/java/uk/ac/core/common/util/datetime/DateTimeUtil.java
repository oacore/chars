package uk.ac.core.common.util.datetime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Date time util.
 */
public final class DateTimeUtil {

    public static final String STANDARD_LOCAL_DATE_TIME = "yyyy/MM/dd HH:mm";

    private DateTimeUtil() {

    }

    public static String formatInStandardBasicLocalDateTime(LocalDateTime localDateTime) {
        return format(localDateTime, STANDARD_LOCAL_DATE_TIME);
    }

    private static String format(LocalDateTime localDateTime, String pattern) {
       return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
}
