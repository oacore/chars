package uk.ac.core.common.model.article;

/**
 *
 * @author mc26486
 */
public enum PDFUrlSource {
    /**
     * Links extracted from OAI-PMH metadata 
     */
    OAIPMH,
    /**
     * Links extracted from OAI-PMH metadata but converted to other form - according to known per repository pattern
     */
    OAIPMH_TRANSFORMED,
    /**
     * Links discovered from unpaywall integration
     */
    UNPAYWALL,
    /**
     * Links coming from publisher connector (data interoperability framework)
     */
    DIT
}
