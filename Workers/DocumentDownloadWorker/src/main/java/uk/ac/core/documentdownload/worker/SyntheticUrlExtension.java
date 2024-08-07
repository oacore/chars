package uk.ac.core.documentdownload.worker;

/**
 * This returns and parses a URL and creates a Synthetic Extention.
 *
 * URL's do not really have extentions (hence synthetic).
 *
 * Example of URL = the 'extention' returned | true if extention found or false
 * ex.com/asdf.pdf = "pdf" | true ex.com/asdf = "" | false ex.com/bzc.pdf?asdf =
 * "pdf" | true ex.com/uss?htrs=xcgh | false
 *
 * A number of repository will allow you to download a PDF without the URL
 * having a .pdf extention. Use with with caution.
 *
 *
 * @author Samuel Pearce <samuel.pearce@open.ac.uk>
 */
public class SyntheticUrlExtension {

    private final String url;

    /**
     * ctor for SyntheticExtention
     *
     * @param url
     */
    public SyntheticUrlExtension(String url) {
        this.url = url;
    }

    /**
     * Returns true if we can infer an extention from the URL
     *
     * @return
     */
    public Boolean hasExtension() {
        return !this.getExtension().isEmpty();
    }

    /**
     * Gets the 'extention' of a url
     *
     * @return
     */
    public String getExtension() {
        String str = this.url;
        if (str.contains("://")) {
            str = str.substring(str.lastIndexOf("://") + 3);
        }
        if (str.contains("?")) {
            str = str.substring(0, str.lastIndexOf("?"));
        }
        if (str.contains("/")) {
            str = str.substring(str.lastIndexOf("/"));
            if (str.contains(".")) {
                str = str.substring(str.lastIndexOf("."));
            } else {
                str = "";
            }
        } else {
            str = "";
        }
        return str;
    }

}
