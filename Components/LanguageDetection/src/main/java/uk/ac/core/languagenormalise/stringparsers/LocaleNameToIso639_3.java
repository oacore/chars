package uk.ac.core.languagenormalise.stringparsers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/***
 * Parses a language comparing the name to a Locale
 *
 * Not thread-safe
 */
public class LocaleNameToIso639_3 implements LanguageStringParser {

    private String language;
    private Locale displayLanguageLocale;
    // todo: move caching layer somewhere else
    private static Map<Locale, Map<String, String>> localeMap;

    public LocaleNameToIso639_3(String language, Locale displayLanguageLocale) {
        this.displayLanguageLocale = displayLanguageLocale;
        this.language = language;
        if (localeMap == null) {
            localeMap = new HashMap<>();
        }
        if (!localeMap.containsKey(displayLanguageLocale)) {
            localeMap.put(displayLanguageLocale, languages(displayLanguageLocale));
        }
    }

    @Override
    public Optional<String> patternMatch() {
        return Optional.ofNullable(
                this.localeMap.get(
                        this.displayLanguageLocale).get(
                            this.language.toLowerCase()
                )
        );
    }

    private static Map<String, String> languages(Locale displayLocale) {
        String[] languages = Locale.getISOLanguages();
        Map<String, String> localeMap = new HashMap<String, String>(languages.length);
        for (String language : languages) {
            Locale locale = new Locale(language);
            localeMap.put(locale.getDisplayLanguage(displayLocale).toLowerCase(), locale.getISO3Language());
        }
        return localeMap;
    }
}
