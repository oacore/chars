package uk.ac.core.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author mc26486
 */
public class FuzzyMatcher {

    /**
     * Check if string is contained inside other string using Levenshtein
     * distance Normalizes distance according to string length and returns true
     * if below the threshold
     *
     * @param haystack
     * @param needle
     * @param threshold
     * @return
     */
    public static boolean fuzzyStringMatch(String haystack, String needle, double threshold) {
        // exit if empty arguments
        if (haystack == null || needle == null) {
            return false;
        }
        if (needle.length() <= 5) {
            return false;
        }

        // perform basic string cleaning 
        haystack = haystack.toLowerCase().trim();
        needle = needle.toLowerCase().trim();

        String needleFirstChars = needle.substring(0, 5);

        int startIndex = StringUtils.indexOfIgnoreCase(haystack, needleFirstChars);

        if (startIndex >= 0) {
            String haystackMeaningfullPart = null;
            if (startIndex + needle.length() < haystack.length()) {
                haystackMeaningfullPart = haystack.substring(startIndex, startIndex + needle.length());
            } else {
                haystackMeaningfullPart = haystack.substring(startIndex);
            }
            if (haystackMeaningfullPart != null) {
                int levDistance = StringUtils.getLevenshteinDistance(haystackMeaningfullPart, needle);

                double levDistanceNormalized = (double) levDistance / needle.length();

                return (levDistanceNormalized < threshold);
            }
        }

        return false;
    }

}
