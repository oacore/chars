package uk.ac.core.metadatadownloadworker.exception;

import uk.ac.core.common.util.datastructure.Tuple;

/**
 * Resumption Token Exception.
 */
public class ResumptionTokenException extends Exception {

    private String message;
    private Tuple<String, String> details;

    public ResumptionTokenException(String problematicVerb) {
        this.message = "Resumption token issues were detected.";
        this.details = new Tuple<>("problematicVerb", problematicVerb);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Tuple<String, String> getDetails() {
        return details;
    }
}
