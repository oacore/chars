package uk.ac.core.languagenormalise.stringparsers;

import java.util.Optional;

/**
 * Wraps a plain string in a LanguageStringParser without any processing
 */
public class PlainStringLanguageParser implements LanguageStringParser {

    String language;

    public PlainStringLanguageParser(String language) {
        this.language = language;
    }

    @Override
    public Optional<String> patternMatch() {
        return Optional.of(this.language);
    }
}
