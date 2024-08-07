package uk.ac.core.dataprovider.logic.exception;

public class DataProviderNotFoundException extends Exception {

    public DataProviderNotFoundException(long id) {
        super(String.format("Data provider %s wasn't found.", id));
    }
}
