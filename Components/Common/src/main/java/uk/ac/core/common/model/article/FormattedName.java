/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.common.model.article;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Formats the input name to Firstname Lastname
 *
 * If the input is Lastname, Firstname, when toString() is called,
 * the names will output as Firstname Lastname
 * 
 * @author samuel
 */
public class FormattedName {

    private final String authorname;

    public FormattedName(final String authorname) {
        this.authorname = authorname;
    }

    @Override
    public String toString() {        
        Pattern pattern = Pattern.compile("([\\w-]+), (\\w+)");
        Matcher matcher = pattern.matcher(this.authorname);
        if (matcher.find()) {
            return matcher.group(2) + " " + matcher.group(1);
        } else {
            return this.authorname;
        }
    }
    
    
}
