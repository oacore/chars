
package uk.ac.core.extractmetadata.worker.oaipmh.XMLParser;

/**
 *
 * @author damirah
 */
public enum XMLParseMethodEnum {

    /**
     * old method, loads whole metadata into a list
     */
    PARSEALL("parseall"),
    /**
     * when XMLParser is run in a separate thread, reading metadata is synchronized with their
     * processing
     */
    ONDEMAND("ondemand"),
    /**
     * The class will only parse the metadata to gather statistics. Addinitonal information about
     * the article (as name or abstract) will not be parsed and set in the created ArticleMetadata
     * class.
     */
    STATISTICS("statistics"),
    /**
     * The class will only parse the metadata to gather statistics about DOI occurrence in the
     * metadata. The class will gather information about the number of times DOI appeared in each
     * tag.
     */
    STATISTICS_DOI("statistics_doi"),
    /**
     * don't load all metadata but only a small collection of specified size
     */
    PARSEALL_TEST("parseall_test"),
    /**
     * don't load all metadata but only a small collection of specified size, parsing will be run in
     * separate thread, this allows synchronization of reading and processing of metadata
     */
    ONDEMAND_TEST("ondemand_test");

    private String text;

    XMLParseMethodEnum(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
