package uk.ac.core.languagenormalise.stringparsers;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ToIso639_3Test {

    @Test
    public void testIsoConversion() {
        assertEquals("eng", new ToIso639_3(() -> Optional.of("EN")).patternMatch().get());
        assertEquals("pol", new ToIso639_3(() -> Optional.of("Pl")).patternMatch().get());
    }
}