package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = javax.validation.ValidationException.class)
    public ResponseEntity<ErrorDetails> handleValidationException(javax.validation.ValidationException ex) {
        log.debug(String.valueOf(ex));
        return buildErrorResponse(BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDetails> handleConstraintViolationException(ConstraintViolationException ex) {
        log.debug(String.valueOf(ex));
        return buildErrorResponse(BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity<ErrorDetails> handleUncheckedException(Throwable ex) {
        log.debug(String.valueOf(ex));
        return buildErrorResponse(INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.debug(String.valueOf(ex));
        return buildErrorResponse(BAD_REQUEST, getErrorDescription(ex));
    }

    @ExceptionHandler(value = StatsEndTimeException.class)
    public ResponseEntity<ErrorDetails> handleInvalidBookingStateException(StatsEndTimeException ex) {
        log.debug(String.valueOf(ex));
        return buildErrorResponse(BAD_REQUEST, ex.getMessage());
    }

    private String getErrorDescription(MethodArgumentNotValidException fieldErrors) {
        return fieldErrors.getFieldErrors().stream()
                .map(org.springframework.validation.FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }

    private ResponseEntity<ErrorDetails> buildErrorResponse(HttpStatus status, String message) {
        ErrorDetails error = new ErrorDetails(status.value(), message);
        return new ResponseEntity<>(error, status);
    }

}

