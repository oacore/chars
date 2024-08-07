package uk.ac.core.textextraction.exceptions;

import java.io.IOException;

/**
 * Custom exception for TextExtractor class. 
 * Exception contains error code which signifies the exception cause.
 * @author Drahomira Herrmannova <d.herrmannova@gmail.com>
 */
public class TextExtractionException extends IOException {

    private final TextExtractionErrorCodes errorCode;

    public TextExtractionException(Throwable cause, TextExtractionErrorCodes errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public TextExtractionException(String message, TextExtractionErrorCodes errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public TextExtractionException(String message, TextExtractionErrorCodes errorCode, Exception innerException) {
        this(message, errorCode);
        this.initCause(innerException);
    }
    
    public TextExtractionErrorCodes getErrorCode() { 
        return this.errorCode;
    }
}