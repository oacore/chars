package uk.ac.core.languagenormalise.stringparsers;

import java.util.Optional;

/***
 * Parses a string, if it already looks like an ISO 639-3 language code, only return it
 * If not, return nothing.
 *
 * The string is not validated as an ISO 639-3 code - only that it has ^[A-Za-z]{3}$
 */
public class Iso639_3Language implements LanguageStringParser {

    private String language;

    public Iso639_3Language(String language) {
        this.language = language;
    }

    @Override
    public Optional<String> patternMatch() {
        if (this.language.matches("^[A-Za-z]{3}$")) {
            return Optional.of(this.language.toLowerCase());
        }
        return Optional.empty();
    }
}
