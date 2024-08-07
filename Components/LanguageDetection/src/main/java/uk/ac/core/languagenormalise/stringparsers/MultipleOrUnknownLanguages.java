package uk.ac.core.languagenormalise.stringparsers;

import uk.ac.core.languagenormalise.Languages;

import java.util.Optional;

public class MultipleOrUnknownLanguages implements LanguageStringParser {

    private final String language;



    public MultipleOrUnknownLanguages(String language) {
        this.language = language.toLowerCase();
    }

    @Override
    public Optional<String> patternMatch() {
        if (this.language.contains(" and ") ||
                this.language.contains("multiple ") ||
                this.language.contains(",") ||
                this.language.contains("/") ||
                this.language.contains(";") ||
                this.language.contains("&")
        ) {
            return Optional.of(Languages.MULTIPLE_LANGUAGES.toString());
        }

        if (this.language.contains("other") ||
                this.language.contains("unknown") ||
                this.language.contains("xxx") ||
                this.language.contains("N/A") ||
                this.language.contains("not available")) {
            return Optional.of(Languages.UNDEFINED_LANGUAGE.toString());
        }

        if (this.language.contains("not applicable") ||
                this.language.contains("no linguistic content") ||
                this.language.contains("none")) {
            return Optional.of(Languages.NO_LINGUISTIC_CONTENT.toString());
        }
        return Optional.empty();
    }
}
