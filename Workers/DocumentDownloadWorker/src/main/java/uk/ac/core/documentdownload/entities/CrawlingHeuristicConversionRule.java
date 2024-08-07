package uk.ac.core.documentdownload.entities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import uk.ac.core.common.util.FuzzyMatcher;

/**
 *
 * @author mc26486
 */
public class CrawlingHeuristicConversionRule {

    private String input_template;
    private String output_template;

    public CrawlingHeuristicConversionRule(String input_template,
            String output_template) {
        this.input_template = input_template;
        this.output_template = output_template;
    }

    public String getInput_template() {
        return input_template;
    }

    public String getOutput_template() {
        return output_template;
    }

    public String convert(String inputUrl) {

        String toReturn = output_template;

        DiffMatchPatch dmp = new DiffMatchPatch();
        LinkedList<DiffMatchPatch.Diff> diff = dmp.diffMain(input_template, inputUrl);
        Map<String, String> variableSubstitutionMap = new HashMap<>();
        String key = "";
        for (DiffMatchPatch.Diff diff1 : diff) {
            if (diff1.operation == DiffMatchPatch.Operation.DELETE) {
                key = diff1.text;
            }
            if (diff1.operation == DiffMatchPatch.Operation.INSERT) {
                variableSubstitutionMap.put(key, diff1.text);
            }
        }

        for (Map.Entry<String, String> entry : variableSubstitutionMap.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
//                System.out.println("(key,value) = " + k+" "+v+"|");
            toReturn = toReturn.replaceAll(k, v);
        }
        return toReturn;
    }

    public String predict(String inputUrl) {
        boolean fits = FuzzyMatcher.fuzzyStringMatch(input_template, inputUrl, 0.1);
        if (!fits) {
            return null;
        } else {
            return convert(inputUrl);
        }
    }
}
