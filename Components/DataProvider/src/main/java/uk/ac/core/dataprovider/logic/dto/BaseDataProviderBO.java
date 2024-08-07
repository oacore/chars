package uk.ac.core.dataprovider.logic.dto;

public class BaseDataProviderBO extends DataProviderBO {

    private long baseId;

    public BaseDataProviderBO(String oaiPmhEndpoint, String name, String description, boolean journal, String countryCode) {
        super(oaiPmhEndpoint, name, description, journal, countryCode);
    }

    public long getBaseId() {
        return baseId;
    }

    public void setBaseId(long baseId) {
        this.baseId = baseId;
    }


}
