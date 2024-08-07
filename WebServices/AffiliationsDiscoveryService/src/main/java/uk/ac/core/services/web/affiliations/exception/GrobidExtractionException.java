package uk.ac.core.services.web.affiliations.exception;

public class GrobidExtractionException extends Exception {
    public GrobidExtractionException() {
        super("Some errors occurred while extracting affiliations via GROBID engine");
    }

    public GrobidExtractionException(String message) {
        super(message);
    }
}
