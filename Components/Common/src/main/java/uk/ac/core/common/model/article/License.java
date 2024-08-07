package uk.ac.core.common.model.article;

/**
 * License provides methods to interpret the license terms.
 * <p>
 * This only describes what a license allows or disallows. It does not consider any local laws, regulations or any
 * desires of the data provider (such as if a data provider is set to TDM only etc)
 */
public class License {

    private final String license;

    public License(String license) {
        this.license = (null == license) ? "" : license;
    }

    /**
     * If a license is considered Open Access
     * @return true if the license appears to be Open Access, otherwise false
     */
    public boolean isOpenAccess() {
        // check if there is a URL for CC
        boolean isStrict = this.license.contains("/creativecommons");
        // check if CC mentioned in the license string
        boolean isOpenAccess =
                this.license.toLowerCase().contains("cc-") ||
                this.license.toLowerCase().contains("cc by") ||
                this.license.toLowerCase().contains("cc_by") ||
                this.license.toLowerCase().contains("creative commons") ||
                this.license.toLowerCase().contains("openaccess") ||
                this.license.toLowerCase().contains("open access");
        return isStrict || isOpenAccess;
    }

    public boolean unknownLicense() {
        return this.license.isEmpty();
    }

}
