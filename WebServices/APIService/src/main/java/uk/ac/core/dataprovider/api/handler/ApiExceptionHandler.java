package uk.ac.core.dataprovider.api.handler;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.ac.core.dataprovider.api.model.DataProviderDuplicateErrorResponse;
import uk.ac.core.dataprovider.api.model.ErrorResponse;
import uk.ac.core.dataprovider.api.model.ErrorsResponse;
import uk.ac.core.dataprovider.logic.exception.DataProviderDuplicateException;
import uk.ac.core.dataprovider.logic.exception.DataProviderNotFoundException;
import uk.ac.core.dataprovider.logic.exception.OaiPmhEndpointNotFoundException;
import uk.ac.core.dataprovider.logic.exception.OaiPmhInvalidException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

@RestControllerAdvice(basePackages = "uk.ac.core.dataprovider.api.controller")
public class ApiExceptionHandler {

    /* Validation handlers */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        return new ErrorResponse(ex.getLocalizedMessage().split("\\.")[1].replace(":", ""));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsResponse processValidationErrors(MethodArgumentNotValidException ex) {

        BindingResult result = ex.getBindingResult();

        List<String> errors = new ArrayList<>();

        if (result.hasFieldErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getField() + " " + fieldError.getDefaultMessage());
            }
        }
        List<String> commaSeparatedGlobalErrors = result.getGlobalErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        errors.addAll(commaSeparatedGlobalErrors);

        return new ErrorsResponse(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        return new ErrorResponse("HTTP message is not readable.");
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNumberFormatException(NumberFormatException ex) {
        return new ErrorResponse("Wrong type of path variable/query parameter.");
    }

    @ExceptionHandler({
            JsonPatchException.class,
            InvalidDefinitionException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidPatchBodyExceptions() {
        return new ErrorResponse("Patch path is malformed/incorrect.");
    }

    /* Exceptions handlers */
    @ExceptionHandler(DataProviderDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataProviderDuplicateException(DataProviderDuplicateException ex) {
        return new DataProviderDuplicateErrorResponse(ex);
    }

    @ExceptionHandler(DataProviderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDataProviderNotFoundException(DataProviderNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(OaiPmhEndpointNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleOaiPmhEndpointNotFoundException(OaiPmhEndpointNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(OaiPmhInvalidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleValidationException(OaiPmhInvalidException ex) {
        return new ErrorResponse(ex.getMessage());
    }
}