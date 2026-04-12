package com.devsu.customerservice.infrastructure.web;

import com.devsu.customerservice.domain.exception.ClienteDuplicadoException;
import com.devsu.customerservice.domain.exception.ClienteNoEncontradoException;
import com.devsu.customerservice.domain.exception.DomainException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Converts application and validation exceptions into API error responses. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** Returns a 404 response for missing customers. */
  @ExceptionHandler(ClienteNoEncontradoException.class)
  ResponseEntity<ApiError> notFound(RuntimeException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiError.of(HttpStatus.NOT_FOUND.value(), exception.getMessage()));
  }

  /** Returns a 409 response for duplicated customer data. */
  @ExceptionHandler(ClienteDuplicadoException.class)
  ResponseEntity<ApiError> conflict(RuntimeException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiError.of(HttpStatus.CONFLICT.value(), exception.getMessage()));
  }

  /** Returns a 400 response for domain validation errors. */
  @ExceptionHandler(DomainException.class)
  ResponseEntity<ApiError> badRequest(RuntimeException exception) {
    return ResponseEntity.badRequest()
        .body(ApiError.of(HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
  }

  /** Returns a 400 response with field-level validation errors. */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> validation(MethodArgumentNotValidException exception) {
    return ResponseEntity.badRequest()
        .body(
            new ApiError(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Solicitud invalida",
                fieldErrors(exception)));
  }

  private Map<String, String> fieldErrors(MethodArgumentNotValidException exception) {
    Map<String, String> errors = new HashMap<>();
    exception
        .getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    return errors;
  }
}
