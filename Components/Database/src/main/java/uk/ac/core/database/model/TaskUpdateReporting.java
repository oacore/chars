package uk.ac.core.database.model;

/**
 * @author Giorgio Basile
 * @since 15/12/2017
 */
public class TaskUpdateReporting extends TaskUpdate {

    private String countryCode;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}
