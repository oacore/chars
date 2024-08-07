package uk.ac.core.languagenormalise.stringparsers;

import java.util.*;

public class ManualLookup implements LanguageStringParser {

    private String language;

    /***
     * A custom list (lower case) of languages and their language code
     */
    private static Map<String, String> languages;

    public ManualLookup(String language) {
        this.language = language;
        if (languages == null) {
             ManualLookup.generateLanguageList();
        }
    }

    @Override
    public Optional<String> patternMatch() {
        String a = this.language.toLowerCase();
         return Optional.ofNullable(ManualLookup.languages.get(a));
    }

    private static void generateLanguageList() {
        ManualLookup.languages = new HashMap<>();
//        languages.put("english", "eng");
        languages.put("Anglais", "eng");
        languages.put("en-aus", "eng");
        languages.put("angol", "eng"); // English (in Hungarian)
        languages.put("英语", "eng"); // English (in Simplified Chinese)
        languages.put("英語", "eng"); // English (in Traditional Chinese)
        languages.put("inglese", "eng"); //English (in Italian)
        languages.put("française", "fra"); // French
        languages.put("jp", "jpn");
        languages.put("fr en", "mul");
        languages.put("pol.", "pol");
        languages.put("português", "por");
        languages.put("ukraine", "ukr");
        languages.put("magyar", "hun"); // Hungarian
    }
}
