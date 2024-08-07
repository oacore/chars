package uk.ac.core.languagenormalise;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NormaliseLanguageTest {

    @BeforeAll
    static void beforeAll() {
        new NormaliseLanguage("en");
    }

    @Test
    public void testNormaliseLanguage() throws Exception {
        assertEquals("eng", new NormaliseLanguage("en_GB").asIso639_3());
        assertEquals("eng", new NormaliseLanguage("EN").asIso639_3());
        assertEquals("eng", new NormaliseLanguage("[[iso]]en_US").asIso639_3());
        assertEquals("eng", new NormaliseLanguage("英语").asIso639_3());
        assertEquals("eng", new NormaliseLanguage("ENGLISH").asIso639_3());
        assertEquals("fra", new NormaliseLanguage("Français").asIso639_3());
        assertEquals("jpn", new NormaliseLanguage("JP").asIso639_3());
        assertEquals("kor", new NormaliseLanguage("[[iso]]ko").asIso639_3());
        assertEquals("msa", new NormaliseLanguage("malay").asIso639_3());
        assertEquals("mul", new NormaliseLanguage("et/en").asIso639_3());
        assertEquals("mul", new NormaliseLanguage("English, Polish").asIso639_3());
        assertEquals("pol", new NormaliseLanguage("pl_PL").asIso639_3());
        assertEquals("rus", new NormaliseLanguage("Russian").asIso639_3());
        assertEquals("rus", new NormaliseLanguage("ru").asIso639_3());
        assertEquals("zho", new NormaliseLanguage("zh-Tw").asIso639_3());
        assertEquals("zho", new NormaliseLanguage("zh_CN").asIso639_3());
        assertEquals("zho", new NormaliseLanguage("中文").asIso639_3());
        assertEquals("ukr", new NormaliseLanguage("uk").asIso639_3());
        assertEquals("und", new NormaliseLanguage(null).asIso639_3());




    }
}