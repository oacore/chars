package uk.ac.core.languagenormalise.stringparsers;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexLanguage implements LanguageStringParser {

    private String pattern;
    private String input;
    private int regexGroup;

    /***
     * Holds a Regex which has a single capture group for the language component
     * @param pattern The Regular Expression to Match
     * @param input The Input String
     * @param regexGroup Which group should the patternMatch() method return
     */
    public RegexLanguage(String pattern, String input, int regexGroup) {
        this.pattern = pattern;
        this.input = input;
        this.regexGroup = regexGroup;
    }

    @Override
    public Optional<String> patternMatch() {
        Pattern locale = Pattern.compile(this.pattern);
        Matcher localeMatch = locale.matcher(this.input);
        while (localeMatch.find()) {
            return Optional.of(localeMatch.group(this.regexGroup));
        }
        return Optional.empty();
    }
}
