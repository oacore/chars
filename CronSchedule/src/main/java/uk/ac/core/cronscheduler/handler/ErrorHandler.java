package uk.ac.core.cronscheduler.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.ac.core.cronscheduler.exception.NotSupportedWorkerException;
import uk.ac.core.cronscheduler.model.ErrorResponse;

@ControllerAdvice(basePackages = "uk.ac.core.cronscheduler.controller")
public class ErrorHandler {

    @ResponseBody
    @ExceptionHandler(NotSupportedWorkerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleNoSupportedWorkerException(NotSupportedWorkerException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Invalid worker type is passed or it's not triggerable."));
    }

}