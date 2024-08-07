package uk.ac.core.dataprovider.logic.dto;

public class OpenDoarDataProviderBO extends DataProviderBO {

    private long openDoarId;

    public OpenDoarDataProviderBO() {
    }

    public OpenDoarDataProviderBO(String oaiPmhEndpoint, String name, String description, boolean journal, String countryCode) {
        super(oaiPmhEndpoint, name, description, journal, countryCode);
    }

    public long getOpenDoarId() {
        return openDoarId;
    }

    public void setOpenDoarId(long openDoarId) {
        this.openDoarId = openDoarId;
    }
}
