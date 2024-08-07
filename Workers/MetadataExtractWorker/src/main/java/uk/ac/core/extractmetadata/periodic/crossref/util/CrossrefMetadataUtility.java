package uk.ac.core.extractmetadata.periodic.crossref.util;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrossrefMetadataUtility {
    private static final List<Pattern> DOI_PATTERNS = Arrays.asList(
            Pattern.compile("(<\\d*::AID-[A-Z]+\\d+>)"),
            Pattern.compile("(<\\d*:[A-Z]+\\d*>)"),
            Pattern.compile("(10\\.\\S+<>\\S+)</")
    );

    private static final List<Pattern> OTHER_PATTERNS = Arrays.asList(
            Pattern.compile("(\\w+\\s+&\\s+\\w+)"),
            Pattern.compile("<title>(\\s*.*\\s*)</title>"),
            Pattern.compile("(<sub>)"),
            Pattern.compile("(</sub>)")
    );

    public static String escapeSpecialChars(final String rawMetadata) throws UnsupportedEncodingException {
        String fixedXml = rawMetadata;
        for (Pattern pattern: DOI_PATTERNS) {
            fixedXml = escapeByRegex(pattern, fixedXml, true);
        }
        for (Pattern pattern: OTHER_PATTERNS) {
            fixedXml = escapeByRegex(pattern, fixedXml, false);
        }
        return fixedXml;
    }

    private static String escapeByRegex(Pattern pattern, String original, boolean doi) throws UnsupportedEncodingException {
        int lastIndex = 0;
        StringBuilder sb = new StringBuilder();
        Matcher matcher = pattern.matcher(original);
        while (matcher.find()) {
            sb.append(original, lastIndex, matcher.start(1));
            if (doi) {
                sb.append(URLEncoder.encode(matcher.group(1), "utf-8"));
            } else {
                sb.append(StringEscapeUtils.escapeXml(matcher.group(1)));
            }


            lastIndex = matcher.end(1);
        }
        if (lastIndex < original.length()) {
            sb.append(original, lastIndex, original.length());
        }
        return sb.toString();
    }
}
