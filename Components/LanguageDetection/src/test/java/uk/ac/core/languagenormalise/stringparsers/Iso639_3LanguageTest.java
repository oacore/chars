package uk.ac.core.languagenormalise.stringparsers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Iso639_3LanguageTest {

    @Test
    public void testISO639ValidInput() {
        assertEquals("eng", new Iso639_3Language("eng").patternMatch().get());
        assertEquals("fre", new Iso639_3Language("fre").patternMatch().get());
        assertEquals("zxx", new Iso639_3Language("zxx").patternMatch().get());
    }

    @Test
    public void testISO639InvalidInput() {
        assertFalse(new Iso639_3Language("en_GB").patternMatch().isPresent());
        assertFalse(new Iso639_3Language("fr_CA").patternMatch().isPresent());
        assertFalse(new Iso639_3Language("English").patternMatch().isPresent());
    }

}