package uk.ac.core.dataprovider.logic.exception;

import uk.ac.core.dataprovider.logic.dto.DataProviderBO;

import java.util.List;

/**
 * The exception, which is thrown to indicate that data providers already exist.
 */
public final class DataProviderDuplicateException extends Exception {

    private final List<DataProviderBO> dataProviderBOs;

    public DataProviderDuplicateException(List<DataProviderBO> dataProviderBOs) {
        this.dataProviderBOs = dataProviderBOs;
    }

    public List<DataProviderBO> getDataProviderBOs() {
        return dataProviderBOs;
    }
}