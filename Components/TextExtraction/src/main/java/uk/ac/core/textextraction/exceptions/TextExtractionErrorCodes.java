package uk.ac.core.textextraction.exceptions;

/**
 * Error codes for text extraction.
 * @author Drahomira Herrmannova <d.herrmannova@gmail.com>
 */
public enum TextExtractionErrorCodes {

    SOURCE_NOT_FOUND,
    SOURCE_LOAD_EXCEPTION,
    DESTINATION_OPEN_EXCEPTION,
    PARSER_INIT_EXCEPTION,
    PARSE_EXCEPTION,
    UNKNOWN_EXCEPTION,
    DOCUMENT_ENCRYPTED,
    UNKNOWN_ATTACHMENT_ENCRPYTION;
}