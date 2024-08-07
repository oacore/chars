package uk.ac.core.dataprovider.api.model;

import uk.ac.core.dataprovider.api.converter.DataProviderResourceConverter;
import uk.ac.core.dataprovider.api.model.dataprovider.CompactDataProviderResponse;
import uk.ac.core.dataprovider.logic.exception.DataProviderDuplicateException;

import java.util.Set;
import java.util.stream.Collectors;

public class DataProviderDuplicateErrorResponse extends ErrorResponse {
    private final Set<CompactDataProviderResponse> existingDataProviders;

    public DataProviderDuplicateErrorResponse(DataProviderDuplicateException dataProviderDuplicateException) {
        super("Data provider already exists.");
        this.existingDataProviders = dataProviderDuplicateException
                .getDataProviderBOs().stream()
                .map(DataProviderResourceConverter::toCompactDataProviderResponseWithEnabledStatus).collect(Collectors.toSet());
    }

    public Set<CompactDataProviderResponse> getExistingDataProviders() {
        return existingDataProviders;
    }
}
