package uk.ac.core.languagenormalise.stringparsers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManualLookupTest {

    @Test
    public void testManualLanguageLookup() {
        assertEquals("fra", new ManualLookup("Française").patternMatch().get());
        assertEquals("jpn", new ManualLookup("JP").patternMatch().get());
    }
}