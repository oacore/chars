package uk.ac.core.languagenormalise;

import uk.ac.core.database.languages.LanguageDAO;
import uk.ac.core.languagenormalise.stringparsers.*;

import java.util.*;

public class NormaliseLanguage {

    private String language;
    private LanguageDAO languageDOA;

    public NormaliseLanguage(String language) {
        if (language == null) {
            language = "und";
        }
        this.language = language.replace("[[iso]]", "");
    }

    public String asIso639_3() {
        List<LanguageStringParser> actionsToTake = new ArrayList<>();

        // If the string is an ISO 639-3 language
        // Note: any string 3 letters long is treated as ISO 639-3
        //       This may conflict with ISO-639-2T/B
        actionsToTake.add(new Iso639_3Language(this.language));

        // Detect ISO 639-1 (or extract via locale)
        actionsToTake.add(
                new ToIso639_3(
                    new RegexLanguage(
                        "^([a-zA-Z]{2})([-_][a-zA-Z]{2}?)?$",
                        this.language,
                        1)
                )
        );

        // Known strings to be languages which don't match existing standards
        actionsToTake.add(new ManualLookup(this.language));

        // Full language names in local language
        actionsToTake.add(new LocaleNameToIso639_3(this.language, Locale.ENGLISH));
        actionsToTake.add(new LocaleNameToIso639_3(this.language, Locale.FRENCH));
        actionsToTake.add(new LocaleNameToIso639_3(this.language, Locale.ITALIAN));
        actionsToTake.add(new LocaleNameToIso639_3(this.language, Locale.TRADITIONAL_CHINESE));
        actionsToTake.add(new LocaleNameToIso639_3(this.language, Locale.SIMPLIFIED_CHINESE));

        // Finally, if input is explicitly multiple or an unknown language
        actionsToTake.add(new MultipleOrUnknownLanguages(this.language));

        for (LanguageStringParser parser : actionsToTake) {
            Optional<String> result = parser.patternMatch();
            if (result.isPresent() && !"".equals(result.get())) {
                return result.get();
            }
        }

        if (this.language.contains("/") || this.language.contains(";")) {
            // If none of the matches above have passed, if the string contains the characters,
            // assume multiple languages
            return "mul";
        }
        return "und";
    }

}
