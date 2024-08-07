package uk.ac.core.languagenormalise.stringparsers;

import java.util.Optional;

public interface LanguageStringParser {

    /***
     * Returns the matching pattern based on the Class implementation
     * @return An ISO 639-3 Language Code as a string
     */
    Optional<String> patternMatch();
}
