package ORG.oclc.oai.harvester2.verb;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HarvesterVerbTest {

    public void testCharIsFiltered(char[] character, String expected) {
        String output = HarvesterVerb.stripNonValidXMLCharacters(new String(character));
        assertEquals(expected, output);
    }

    @Test
    public void testAsLocalDateTimeAll() {
        // LATIN CAPITAL LETTER A
        this.testCharIsFiltered(new char[] { 0x41 }, "A");
        // HANGUL SYLLABLE PEJ
        this.testCharIsFiltered(new char[] { 0xD3AE }, "펮");
        // WINDOWS-1252 encoding for 'LEFT SINGLE QUOTATION MARK' (U+2018)
        this.testCharIsFiltered(new char[] { 0x91 }, "");
        // WINDOWS-1252 encoding for 'RIGHT SINGLE QUOTATION MARK' (U+2019)
        this.testCharIsFiltered(new char[] { 0x92 }, "");
        // Private Use Area (U+F8FF)
        this.testCharIsFiltered(new char[] { 0xE012 }, "");
    }



}