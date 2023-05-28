package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = javax.validation.ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(javax.validation.ValidationException ex) {
        log.debug(String.valueOf(ex));
        return buildErrorResponse(BAD_REQUEST, ex);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex) {
        log.debug(String.valueOf(ex));
        return buildErrorResponse(BAD_REQUEST, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex) {
        log.debug(String.valueOf(ex));
        return buildErrorResponse(BAD_REQUEST, ex);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.debug(String.valueOf(ex));
        return buildErrorResponse(BAD_REQUEST, ex);
    }

    @ExceptionHandler(value = NotFoundObjectException.class)
    public ResponseEntity<ApiError> handleNotFoundObjectException(NotFoundObjectException ex) {
        log.debug(String.valueOf(ex));
        return buildErrorResponse(NOT_FOUND, ex);
    }

    @ExceptionHandler(value = NumberFormatException.class)
    public ResponseEntity<ApiError> handleNumberFormatException(NumberFormatException ex) {
        log.debug(String.valueOf(ex));
        return buildErrorResponse(BAD_REQUEST, ex);
    }

    @ExceptionHandler(value = ConflictException.class)
    public ResponseEntity<ApiError> handleConflictException(ConflictException ex) {
        log.debug(String.valueOf(ex));
        return buildErrorResponse(CONFLICT, ex);
    }
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.debug(String.valueOf(ex));
        return buildErrorResponse(BAD_REQUEST, ex);
    }


    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity<ApiError> handleUncheckedException(Throwable ex) {
        log.debug(String.valueOf(ex));
        return buildErrorResponse(INTERNAL_SERVER_ERROR, ex);
    }

    private String getErrorDescription(MethodArgumentNotValidException fieldErrors) {
        return fieldErrors.getFieldErrors().stream()
                .map(org.springframework.validation.FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }

    private ResponseEntity<ApiError> buildErrorResponse(HttpStatus status, Throwable ex) {
        ApiError error = ApiError.builder()
                .status(status.getReasonPhrase())
                .errors(getTrace(ex))
                .message(ex.getMessage())
                .reason(Optional.of(ex)
                        .map(Throwable::getCause)
                        .map(Throwable::getMessage)
                        .orElse(null))
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, status);
    }

    private List<String> getTrace(Throwable ex) {
        return Arrays.stream(ex.getStackTrace()).map(String::valueOf).collect(Collectors.toList());
    }

}

