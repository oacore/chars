package uk.ac.core.languagenormalise.stringparsers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class ToIso639_3 implements LanguageStringParser {

    private LanguageStringParser languageStringParser;
    private static Map<String, String> localeMap;

    public ToIso639_3(LanguageStringParser languageStringParser) {
        this.languageStringParser = languageStringParser;
        if (ToIso639_3.localeMap == null) {
            ToIso639_3.localeMap = ToIso639_3.languages();
        }
    }

    public ToIso639_3(String twoLetterLanguageCode) {
        this(new PlainStringLanguageParser(twoLetterLanguageCode));
    }


    @Override
    public Optional<String> patternMatch() {
        Optional<String> p = this.languageStringParser.patternMatch();
        if (p.isPresent()) {
            String s = localeMap.get(p.get().toLowerCase());
            return Optional.ofNullable(s);
        } else {
            return Optional.empty();
        }
    }

    private static Map<String, String> languages() {
        String[] languages = Locale.getISOLanguages();
        Map<String, String> localeMap = new HashMap<String, String>(languages.length);
        for (String language : languages) {
            Locale locale = new Locale(language);
            localeMap.put(language, locale.getISO3Language());
        }
        return localeMap;
    }
}
