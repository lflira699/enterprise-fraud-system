package com.efs.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("errorCode", "CUSTOMER_RESOURCE_NOT_FOUND");
        body.put("message", exception.getMessage());
        body.put("correlationId", request.getHeader("X-Correlation-ID"));
        body.put("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DuplicateRecordException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateRecord(
            DuplicateRecordException exception,
            HttpServletRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("errorCode", "CUSTOMER_DUPLICATE_RECORD");
        body.put("message", exception.getMessage());
        body.put("correlationId", request.getHeader("X-Correlation-ID"));
        body.put("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        Map<String, String> validationErrors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        validationErrors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errorCode", "VALIDATION_ERROR");
        body.put("message", "Request validation failed");
        body.put("correlationId", request.getHeader("X-Correlation-ID"));
        body.put("path", request.getRequestURI());
        body.put("validationErrors", validationErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }
@ExceptionHandler(ValidationException.class)
public ResponseEntity<Map<String, Object>> handleBusinessValidation(
        ValidationException exception,
        HttpServletRequest request) {

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
    body.put("errorCode", "BUSINESS_VALIDATION_ERROR");
    body.put("message", exception.getMessage());
    body.put("correlationId", request.getHeader("X-Correlation-ID"));
    body.put("path", request.getRequestURI());

    return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(body);
}
}