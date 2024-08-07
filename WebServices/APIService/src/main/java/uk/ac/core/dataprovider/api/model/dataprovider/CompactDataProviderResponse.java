package uk.ac.core.dataprovider.api.model.dataprovider;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompactDataProviderResponse extends ApiDataProvider {

    @JsonProperty(value = "id")
    private Long dataProviderId;

    private Boolean enabled;

    public CompactDataProviderResponse() {

    }

    public Long getDataProviderId() {
        return dataProviderId;
    }

    public void setDataProviderId(Long dataProviderId) {
        this.dataProviderId = dataProviderId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}