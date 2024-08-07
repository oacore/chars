package uk.ac.core.dataprovider.api.converter;

import uk.ac.core.dataprovider.api.model.dataprovider.ApiDataProvider;
import uk.ac.core.dataprovider.api.model.dataprovider.CompactDataProviderResponse;
import uk.ac.core.dataprovider.logic.dto.DataProviderBO;

public final class DataProviderResourceConverter {

    private DataProviderResourceConverter() {

    }

    public static CompactDataProviderResponse toCompactDataProviderResponse(DataProviderBO dataProviderBO) {
        CompactDataProviderResponse compactDataProviderResponse = new CompactDataProviderResponse();
        compactDataProviderResponse.setDataProviderId(dataProviderBO.getId());
        compactDataProviderResponse.setOaiPmhEndpoint(dataProviderBO.getOaiPmhEndpoint());
        compactDataProviderResponse.setJournal(dataProviderBO.isJournal());
        compactDataProviderResponse.setName(dataProviderBO.getName());
        compactDataProviderResponse.setDescription(dataProviderBO.getDescription());
        compactDataProviderResponse.setLocation(toShortLocationInfo(dataProviderBO));
        return compactDataProviderResponse;
    }

    public static CompactDataProviderResponse toCompactDataProviderResponseWithEnabledStatus(DataProviderBO dataProviderBO) {
        CompactDataProviderResponse response = toCompactDataProviderResponse(dataProviderBO);
        response.setEnabled(dataProviderBO.getEnabled());
        return response;
    }

    private static ApiDataProvider.LocationInfo toShortLocationInfo(DataProviderBO dataProviderBO) {
        ApiDataProvider.LocationInfo locationInfo = new ApiDataProvider.LocationInfo();
        locationInfo.setCountryCode(dataProviderBO.getCountryCode());
        return locationInfo;
    }

    public static DataProviderBO toDataProviderBO(ApiDataProvider apiDataProvider) {
        DataProviderBO dataProviderBO = new DataProviderBO();
        dataProviderBO.setOaiPmhEndpoint(apiDataProvider.getOaiPmhEndpoint());
        dataProviderBO.setName(apiDataProvider.getName());
        dataProviderBO.setJournal(apiDataProvider.isJournal());
        dataProviderBO.setDescription(apiDataProvider.getDescription());
        if (apiDataProvider.getLocation() != null) {
            dataProviderBO.setCountryCode(apiDataProvider.getLocation().getCountryCode());
            dataProviderBO.setLongitude(apiDataProvider.getLocation().getLongitude());
            dataProviderBO.setLatitude(apiDataProvider.getLocation().getLatitude());
        }
        if (apiDataProvider.getHomepage() == null) {
            dataProviderBO.setHomepage(apiDataProvider.getOaiPmhEndpoint());
        } else {
            dataProviderBO.setHomepage(apiDataProvider.getHomepage());
        }
        dataProviderBO.setMetadataFormat(apiDataProvider.getMetadataFormat());
        dataProviderBO.setSoftware(apiDataProvider.getSoftware());
        dataProviderBO.setSource(apiDataProvider.getSource());
        return dataProviderBO;
    }
}