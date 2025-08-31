package com.weatherapi.domain.exception;


import com.weatherapi.domain.exception.validation.FieldValidationError;
import com.weatherapi.domain.exception.validation.ValidationErrorDetails;
import com.weatherapi.domain.exception.validation.WeatherGetValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;                 // 400
    private static final HttpStatus BAD_GATEWAY = HttpStatus.BAD_GATEWAY;                 // 502
    private static final HttpStatus NOT_FOUND = HttpStatus.NOT_FOUND;                     // 404
    private static final HttpStatus UNPROCESSABLE_ENTITY = HttpStatus.UNPROCESSABLE_ENTITY; // 422
    private static final HttpStatus INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR; // 500
    private static final HttpStatus SERVICE_UNAVAILABLE = HttpStatus.SERVICE_UNAVAILABLE; // 503

    @ExceptionHandler(WeatherGetValidationException.class)
    public ResponseEntity<ErrorDetails> handleWeatherGetValidation(WeatherGetValidationException exception, HttpServletRequest request) {
        ErrorDetails errorDetails = getErrorDetails(LocalDateTime.now(), request, exception.getMessage(), BAD_REQUEST);
        return new ResponseEntity<>(errorDetails, BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
        // 404 - Resource not found
        ErrorDetails errorDetails = getErrorDetails(LocalDateTime.now(), request, exception.getMessage(), NOT_FOUND);
        return new ResponseEntity<>(errorDetails, NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String message = MessageResolver.getMessage("error.validation.global");
        List<FieldValidationError> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldValidationError(error.getField(), error.getDefaultMessage()))
                .toList();

        ValidationErrorDetails errorDetails = ValidationErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .status(BAD_REQUEST.value())
                .error(BAD_REQUEST.name())
                .path(request.getRequestURI())
                .message(message)
                .fields(fieldErrors)
                .build();

        log.warn(
                "Validation failed in [{}] - URI: {}, Status: {}, Fields: {}",
                exception.getParameter().getExecutable().getDeclaringClass().getSimpleName(),
                request.getRequestURI(),
                BAD_REQUEST.value(),
                fieldErrors.stream()
                        .map(err -> err.getField() + ": " + err.getMessage())
                        .collect(Collectors.joining(", "))
        );

        return ResponseEntity.badRequest().body(errorDetails);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorDetails> handleBusinessRule(BusinessRuleException exception, HttpServletRequest request) {
        // 422 - Business logic error
        ErrorDetails errorDetails = getErrorDetails(LocalDateTime.now(), request, exception.getMessage(), UNPROCESSABLE_ENTITY);
        return new ResponseEntity<>(errorDetails, UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgument(IllegalArgumentException exception, HttpServletRequest request) {
        // 400 - Invalid param
        ErrorDetails errorDetails = getErrorDetails(LocalDateTime.now(), request, exception.getMessage(), BAD_REQUEST);
        return new ResponseEntity<>(errorDetails, BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDetails> handleRuntime(RuntimeException exception, HttpServletRequest request) {
        // 500 - Unexpected
        String message = MessageResolver.getMessage("error.internal.server");

        ErrorDetails errorDetails = getErrorDetails(LocalDateTime.now(), request, exception.getMessage(), INTERNAL_SERVER_ERROR);

        log.error(message, exception);

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorDetails);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorDetails> handleServiceUnavailable(ServiceUnavailableException exception, HttpServletRequest request) {
        // 503 - Unnavailable service
        String message = MessageResolver.getMessage("error.internal.server");

        ErrorDetails errorDetails = getErrorDetails(LocalDateTime.now(), request, exception.getMessage(), SERVICE_UNAVAILABLE);

        log.error(message, exception);

        return new ResponseEntity<>(errorDetails, SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(BadGatewayException.class)
    public ResponseEntity<ErrorDetails> handleBadGateway(BadGatewayException exception, HttpServletRequest request) {
        // 502 - External error
        String message = MessageResolver.getMessage("error.internal.server");

        ErrorDetails errorDetails = getErrorDetails(LocalDateTime.now(), request, exception.getMessage(), BAD_GATEWAY);

        log.error(message, exception);
        return new ResponseEntity<>(errorDetails, BAD_GATEWAY);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGenericException(Exception exception, HttpServletRequest request) {
        // 500 - Generic fail
        String message = MessageResolver.getMessage("error.internal.server");
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .status(INTERNAL_SERVER_ERROR.value())
                .error(INTERNAL_SERVER_ERROR.name())
                .path(request.getRequestURL().toString())
                .message(message)
                .build();

        log.error(message, exception);

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorDetails);
    }

    private static ErrorDetails getErrorDetails(LocalDateTime now, HttpServletRequest request, String exception, HttpStatus httpStatus) {
        return new ErrorDetails(
                now,
                httpStatus.value(),
                httpStatus.name(),
                request.getRequestURL().toString(),
                exception,
                UUID.randomUUID().toString()
        );
    }
}
