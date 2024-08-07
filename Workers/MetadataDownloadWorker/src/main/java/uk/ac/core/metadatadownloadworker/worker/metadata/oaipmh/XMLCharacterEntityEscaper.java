/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.metadatadownloadworker.worker.metadata.oaipmh;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author samuel
 */
public class XMLCharacterEntityEscaper {


    /**
     * Returns the string replacing any XML Character entities with its unicode representation equivalent.
     * For example, &#55349;&#56623; would be replaced with \uD835\uDD2F
     * @param text The string to search and replace
     * @return The text with the character entities replaced
     */    
    public static String escape(String text) {
        Pattern XMLRegex = Pattern.compile("&#[0-9]+;");
        Matcher m = XMLRegex.matcher(text);
        while (m.find()) {
            String s = m.group(0);
            
            String output = convertXMLEscapeSequenceToUnicode(s);
            
            text = text.replaceAll(s, output );
        }
        return text;
    }
    
    /**
     * Replaces a string containing XML character entities with its unicode equivalent 
     * @param text The string to search and replace
     * @return The text with the character entities replaced
     */ 
    private static String convertXMLEscapeSequenceToUnicode(String input) {
        Pattern XMLRegex = Pattern.compile("[0-9]+");
        Matcher m = XMLRegex.matcher(input);
        String hex = "";
        while (m.find()) {
            String s = m.group(0);
            hex += "\\\\u" + Integer.toHexString(Integer.parseInt(s)); 
            
        }
        return hex;
    }
}
